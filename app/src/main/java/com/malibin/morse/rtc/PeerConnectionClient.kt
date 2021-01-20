package com.malibin.morse.rtc

import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpSender
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

class PeerConnectionClient(
    private val peerConnectionFactory: PeerConnectionFactory,
    private val mediaManager: MediaManager,
) {
    private lateinit var peerConnection: PeerConnection
    private var createOfferCallback: CreateOfferCallback? = null

    private lateinit var dataChannel: DataChannel

    fun connectPeer(observer: PeerConnection.Observer) {
        val rtcConfiguration = PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, observer)
            ?: error("Cannot Create Peer Connection")
        val mediaStreamLabels = listOf("ARDAMS")
        peerConnection.addTrack(mediaManager.audioTrack, mediaStreamLabels)
        peerConnection.addTrack(mediaManager.videoTrack, mediaStreamLabels)

        val dataChannelInit = DataChannel.Init()
        dataChannel = peerConnection.createDataChannel("dataChannelLabel", dataChannelInit)

        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)
        setVideoMaxBitrate(1700)
    }

    private fun setVideoMaxBitrate(maxBitrateKbps: Int?) {
        val localVideoSender = findVideoSender() ?: return
        val parameters = localVideoSender.parameters
        parameters.encodings.forEach { encoding ->
            encoding.maxBitrateBps = maxBitrateKbps?.let { it * 1_000 }
        }
        localVideoSender.parameters = parameters
    }

    private fun findVideoSender(): RtpSender? {
        return peerConnection.senders.find { it.track()?.kind() == "video" }
    }

    fun createOffer(callback: CreateOfferCallback) {
        this.createOfferCallback = callback
        val sdpObserver = SessionDescriptionProtocolObserver()
        val sdpMediaConstraints = createSdpConstraints()
        peerConnection.createOffer(sdpObserver, sdpMediaConstraints)
    }

    private fun createSdpConstraints(): MediaConstraints {
        return MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
    }

    fun setRemoteDescription(sessionDescription: SessionDescription) {
        val sdpDescription = preferCodec(sessionDescription.description, "VP8", false)
        //sdpDescription = setStartBitrate(sdpDescription)
        val remoteDescription = SessionDescription(sessionDescription.type, sdpDescription)
        peerConnection.setRemoteDescription(null, remoteDescription)
        // drain 하는거 말고 하는게 없음.
    }

    fun addRemoteIceCandidate(iceCandidate: IceCandidate) {
        CoroutineScope(Dispatchers.IO).launch {
            peerConnection.addIceCandidate(iceCandidate)
        }
    }

    fun attachLocalVideoRenderer(renderer: VideoSink) {
        mediaManager.attachLocalVideoRenderer(renderer)
    }

    fun detachLocalVideoRenderer(renderer: VideoSink) {
        mediaManager.detachLocalVideoRenderer(renderer)
    }

    fun close() = CoroutineScope(Dispatchers.IO).launch {
        mediaManager.dispose()
        dataChannel.dispose()
        peerConnectionFactory.stopAecDump()
        peerConnectionFactory.dispose()
        peerConnection.dispose()
        PeerConnectionFactory.stopInternalTracingCapture()
        PeerConnectionFactory.shutdownInternalTracer()
    }

    companion object {
        private val MORSE_TURN_SERVER = PeerConnection.IceServer
            .builder("turn:117.17.196.61:3478")
            .setUsername("testuser")
            .setPassword("root")
            .createIceServer()
        private val ICE_SERVERS = listOf(MORSE_TURN_SERVER)

        @JvmStatic
        private fun preferCodec(
            sessionDescription: String,
            codec: String,
            isAudio: Boolean
        ): String {
            val descriptions = sessionDescription.split("\r\n").toMutableList()
            val mediaType = if (isAudio) "m=audio " else "m=video "
            val mediaDescription = descriptions.find { it.startsWith(mediaType) }
                ?: return sessionDescription.also { printLog("No mediaDescription line, so can't prefer $codec") }

            val codecRegex = Regex("^a=rtpmap:(\\d+) $codec(/\\d+)+[\r]?$")
            val codecPayloadTypes = descriptions
                .mapNotNull { codecRegex.find(it) }
                .map { it.groupValues[1] }

            val updatedMediaDescription =
                movePayloadTypesToFront(codecPayloadTypes, mediaDescription)

            val mediaDescriptionIndex = descriptions.indexOf(mediaDescription)
            descriptions[mediaDescriptionIndex] = updatedMediaDescription
            return descriptions.joinToString("\r\n")
        }

        @JvmStatic
        private fun movePayloadTypesToFront(
            preferredPayloadTypes: List<String>,
            mediaDescription: String,
        ): String {
            val mediaDescriptionParts = mediaDescription.split(" ")
            val header = mediaDescriptionParts.subList(0, 3)
            val unPreferredPayLoadTypes =
                mediaDescriptionParts.subList(3, mediaDescriptionParts.size).toMutableList()
            unPreferredPayLoadTypes.removeAll(preferredPayloadTypes)

            return mutableListOf<String>().apply {
                addAll(header)
                addAll(preferredPayloadTypes)
                addAll(unPreferredPayLoadTypes)
            }.joinToString(" ")
        }
    }

    private inner class SessionDescriptionProtocolObserver : SdpObserver {

        private var localDescription: SessionDescription? = null

        override fun onCreateSuccess(sessionDescription: SessionDescription) {
            if (localDescription != null) {
                printLog("onCreateSuccess / Multiple SDP create.")
                return
            }
            val description = preferCodec(sessionDescription.description, "VP8", false)
            val localDescription = SessionDescription(sessionDescription.type, description)
            this.localDescription = localDescription
            printLog("onCreateSuccess // set local description")
            // 비동기 동작
            peerConnection.setLocalDescription(this, localDescription)
        }

        override fun onSetSuccess() {
            printLog("onSetSuccess")
            if (peerConnection.remoteDescription == null) {
                printLog("onSetSuccess // peerConnection.remoteDescription == null")
                val localDescription = localDescription ?: error("localDescription cannot be null")
                createOfferCallback?.onOfferSetSuccess(localDescription)
                return
            }
            //drainCandidates()
        }

        override fun onCreateFailure(message: String?) {
            printLog("SDP onCreateFailure : $message")
        }

        override fun onSetFailure(message: String?) {
            printLog("SDP onSetFailure : $message")
        }
    }
}
