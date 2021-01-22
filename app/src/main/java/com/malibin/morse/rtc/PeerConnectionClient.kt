package com.malibin.morse.rtc

import com.malibin.morse.presentation.utils.printLog
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.RtpSender
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

class PeerConnectionClient(
    private val peerConnection: PeerConnection,
) {
    private var createOfferCallback: CreateOfferCallback? = null

    private val dataChannel: DataChannel =
        peerConnection.createDataChannel("dataChannelLabel", DataChannel.Init())

    // 이노메 옵저버 위치 다시 생각해봐야함
    private val sdpObserver = SessionDescriptionProtocolObserver()

    fun addTracks(audioTrack: AudioTrack, videoTrack: VideoTrack) {
        val mediaStreamLabels = listOf("ARDAMS")
        peerConnection.addTrack(audioTrack, mediaStreamLabels)
        peerConnection.addTrack(videoTrack, mediaStreamLabels)

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
        printLog("setRemoteDescription Called")
        val sdpDescription = preferCodec(sessionDescription.description, "VP8", false)
        //sdpDescription = setStartBitrate(sdpDescription)
//        val temp = Temp.setStartBitrate("opus", false, sdpDescription, 32)
        val remoteDescription = SessionDescription(sessionDescription.type, sdpDescription)
        peerConnection.setRemoteDescription(sdpObserver, remoteDescription)
        printLog("Remote SDP set succesfully")
        // drain 하는거 말고 하는게 없음.
    }

    fun addRemoteIceCandidate(iceCandidate: IceCandidate) {
        printLog("addRemoteIceCandidate called")
        peerConnection.addIceCandidate(iceCandidate)
    }

    fun close() {
        try{
            dataChannel.dispose()
        }catch (e:Exception){
        }
        peerConnection.dispose()
    }

    companion object {
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
            peerConnection.setLocalDescription(this, localDescription)
        }

        override fun onSetSuccess() {
            printLog("onSetSuccess")
            if (peerConnection.remoteDescription == null) {
                printLog("onSetSuccess // Local SDP set succesfully")
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
