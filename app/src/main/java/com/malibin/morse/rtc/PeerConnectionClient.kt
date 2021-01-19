package com.malibin.morse.rtc

import com.malibin.morse.presentation.utils.printLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.AudioTrack
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpSender
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink
import org.webrtc.VideoTrack

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

class PeerConnectionClient(
    private val peerConnectionFactory: PeerConnectionFactory
) {
    private lateinit var peerConnection: PeerConnection
    private var createOfferCallback: CreateOfferCallback? = null

    fun connectPeer(
        videoCapturer: VideoCapturer,
        localRenderer: VideoSink,
        observer: PeerConnection.Observer
    ) {
        val rtcConfiguration = PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfiguration, observer)
            ?: error("Cannot Create Peer Connection")

        // Set INFO libjingle logging.
        // NOTE: this _must_ happen while |factory| is alive!
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

        val mediaStream = peerConnectionFactory.createLocalMediaStream("ARDAMS")
        mediaStream.addTrack(createAudioTrack())
        mediaStream.addTrack(createVideoTrack(videoCapturer, localRenderer))
        peerConnection.addStream(mediaStream)

        setVideoMaxBitrate(1700)
    }

    private fun createAudioTrack(): AudioTrack {
        val audioSource = peerConnectionFactory.createAudioSource(createAudioConstraints(false))
        return peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource) //AUDIO_TRACK_ID
            .apply { setEnabled(true) }
    }

    private fun createVideoTrack(
        videoCapturer: VideoCapturer,
        localRenderer: VideoSink,
    ): VideoTrack {
        // 구글코드엔 videoCapturer.initialize 를 하네.. 뭘까..
        videoCapturer.startCapture(1280, 720, 30) // video width, height, fps
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        val videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource)
        //VIDEO_TRACK_ID
        videoTrack.setEnabled(true)
        videoTrack.addSink(localRenderer)
        return videoTrack
    }

    private fun createAudioConstraints(noAudioProcessing: Boolean): MediaConstraints {
        if (noAudioProcessing) {
            return MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "false"))
            }
        }
        return MediaConstraints()
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

    fun close() = CoroutineScope(Dispatchers.IO).launch {
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
            printLog("onCreateSuccess / set local description")
            // 비동기 동작
            peerConnection.setLocalDescription(this, localDescription)
        }

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
                .map { it.value }

            val updatedMediaDescription =
                movePayloadTypesToFront(codecPayloadTypes, mediaDescription)

            val mediaDescriptionIndex = descriptions.indexOf(mediaDescription)
            descriptions[mediaDescriptionIndex] = updatedMediaDescription
            return descriptions.joinToString("\r\n", postfix = "\r\n")
        }

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

        override fun onSetSuccess() {
            if (peerConnection.remoteDescription == null) {
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
