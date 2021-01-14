package com.malibin.morse.rtc

import android.content.Context
import com.malibin.morse.presentation.utils.printLog
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.Logging
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpSender
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink
import org.webrtc.VideoTrack

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

class PeerConnectionClient(
    context: Context,
    private val eglBase: EglBase,
) {
    private val peerConnectionFactory: PeerConnectionFactory =
        createPeerConnectionFactory(context, eglBase)
    private lateinit var peerConnection: PeerConnection

    fun connectPeer(videoCapturer: VideoCapturer, localRenderer: VideoSink) {
        val rtcConfiguration = PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }

        peerConnection = peerConnectionFactory
            .createPeerConnection(rtcConfiguration, PeerConnectionObserver())
            ?: error("Cannot Create Peer Connection")

        // Set INFO libjingle logging.
        // NOTE: this _must_ happen while |factory| is alive!
        Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO)

        val mediaStream = peerConnectionFactory.createLocalMediaStream("ARDAMS")
        mediaStream.addTrack(createAudioTrack())
        mediaStream.addTrack(createVideoTrack(videoCapturer, localRenderer))
        peerConnection.addStream(mediaStream)
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

    private fun findVideoSender(peerConnection: PeerConnection): RtpSender? {
        return peerConnection.senders.find { it.track()?.kind() == "video" }
    }

    private fun createSdpConstraints(): MediaConstraints {
        return MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
    }

    companion object {
        private val MORSE_TURN_SERVER = PeerConnection.IceServer
            .builder("turn:117.17.196.61:3478")
            .setUsername("testuser")
            .setPassword("root")
            .createIceServer()
        private val ICE_SERVERS = listOf(MORSE_TURN_SERVER)
    }

    private inner class PeerConnectionObserver : PeerConnection.Observer {
        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
            printLog("onSignalingChange : $p0")
        }

        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
            printLog("onIceConnectionChange : $p0")
        }

        override fun onIceConnectionReceivingChange(p0: Boolean) {
            printLog("onIceConnectionReceivingChange : $p0")
        }

        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
            printLog("onIceGatheringChange : $p0")
        }

        override fun onIceCandidate(p0: IceCandidate?) {
            printLog("onIceCandidate : $p0")
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
            printLog("onIceCandidatesRemoved : $p0")
        }

        override fun onAddStream(p0: MediaStream?) {
            printLog("onAddStream : $p0")
        }

        override fun onRemoveStream(p0: MediaStream?) {
            printLog("onRemoveStream : $p0")
        }

        override fun onDataChannel(p0: DataChannel?) {
            printLog("onDataChannel : $p0")
        }

        override fun onRenegotiationNeeded() {
            printLog("onRenegotiationNeeded ")
        }

        override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
            printLog("onAddTrack : $p0 , $p1")
        }
    }
}
