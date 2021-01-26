package com.malibin.morse.rtc

import android.content.Context
import com.malibin.morse.R
import com.malibin.morse.data.entity.ID
import com.malibin.morse.data.service.response.SocketResponse
import com.malibin.morse.presentation.utils.printLog
import org.java_websocket.handshake.ServerHandshake
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 21, 2021
 */

class WebRtcClient(
    val context: Context,
    eglBase: EglBase,
    private val streamingMode: StreamingMode,
    private val webRtcClientEvents: WebRtcClientEvents,
) {
    private val peerConnectionFactory: PeerConnectionFactory by lazy {
        createPeerConnectionFactory(context, eglBase)
    }
    private val peerConnectionClient: PeerConnectionClient by lazy {
        PeerConnectionClient(createPeerConnection())
    }
    private val webSocketRtcClient: WebSocketRtcClient by lazy {
        WebSocketRtcClient(WebSocketCallbackImpl())
    }
    private val mediaTrackManager: MediaTrackManager by lazy {
        MediaTrackManager(eglBase, context, peerConnectionFactory)
    }

    private fun createPeerConnection(): PeerConnection {
        return peerConnectionFactory.createPeerConnection(
            createRtcConfiguration(),
            PeerConnectionObserver()
        ) ?: error("Cannot Create Peer Connection")
    }

    private fun createRtcConfiguration(): PeerConnection.RTCConfiguration {
        return PeerConnection.RTCConfiguration(ICE_SERVERS).apply {
            tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
            bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
            rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
            keyType = PeerConnection.KeyType.ECDSA
            enableDtlsSrtp = true
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
        }
    }

    fun connectPeer() {
        if (streamingMode == StreamingMode.BROADCAST) {
            peerConnectionClient.addTracks(
                mediaTrackManager.audioTrack,
                mediaTrackManager.videoTrack
            )
        }
        webSocketRtcClient.setTrustedCertificate(context.resources.openRawResource(R.raw.kurento_example_certification))
        webSocketRtcClient.connectRoom()
    }

    fun attachVideoRenderer(renderer: VideoSink) = when (streamingMode) {
        StreamingMode.BROADCAST -> {
            mediaTrackManager.attachVideoRenderer(renderer)
        }
        StreamingMode.VIEWER -> {
            peerConnectionClient.attachRenderer(streamingMode, renderer)
        }
    }

    fun detachVideoRenderer(renderer: VideoSink) = when (streamingMode) {
        StreamingMode.BROADCAST -> {
            mediaTrackManager.detachVideoRenderer(renderer)
        }
        StreamingMode.VIEWER -> {
            peerConnectionClient.detachRenderer(streamingMode, renderer)
        }
    }

    fun close() {
        webSocketRtcClient.close()
        peerConnectionClient.close()
        mediaTrackManager.dispose()
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

    private inner class WebSocketCallbackImpl : WebSocketCallback {
        override fun onOpen(handshake: ServerHandshake?) {
            printLog("Socket Opened")
            peerConnectionClient.createOffer(CreateOfferCallbackImpl())
        }

        override fun onMessage(response: SocketResponse?) {
            printLog("onMessage Called // response : $response")
            if (response == null) return

            when (response.responseId) {
                ID.PRESENTER_RESPONSE, ID.VIEWER_RESPONSE -> {
                    printLog("onMessage ${response.responseId} called")
                    val sdp = SessionDescription(SessionDescription.Type.ANSWER, response.sdpAnswer)
                    peerConnectionClient.setRemoteDescription(sdp)
                }
                ID.ICE_CANDIDATE -> {
                    printLog("onMessage ICE_CANDIDATE called")
                    val candidateResponse = response.candidate ?: error("candidate cannot be null")
                    peerConnectionClient.addRemoteIceCandidate(candidateResponse.toIceCandidate())
                }
                ID.STOP_COMMUNICATION -> {
                    // 멈추기
                }
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            printLog("Socket onClose // code : $code reason : $reason, remote : $remote")
            if (remote) close()
        }

        override fun onError(exception: Exception?) {
            printLog("Socket onError // ${exception?.message}")
            exception?.printStackTrace()
            close()
        }
    }

    private inner class CreateOfferCallbackImpl : CreateOfferCallback {
        override fun onOfferSetSuccess(sessionDescription: SessionDescription) {
            webSocketRtcClient.sendOfferSessionDescription(sessionDescription, streamingMode)
        }
    }

    private inner class PeerConnectionObserver : PeerConnection.Observer {
        override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
            printLog("onSignalingChange // SignalingState: $newState")
        }

        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
            printLog("onIceConnectionChange // ICE newState : $newState")
            if (newState == PeerConnection.IceConnectionState.CONNECTED) {
                webRtcClientEvents.onStateChanged(WebRtcClientEvents.State.CONNECTED)
                printLog("IceConnection Connected!")
            }
        }

        override fun onIceConnectionReceivingChange(newState: Boolean) {
            printLog("onIceConnectionReceivingChange // IceConnectionReceiving changed to $newState")
        }

        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
            printLog("onIceGatheringChange // IceGatheringState : $newState")
        }

        override fun onIceCandidate(iceCandidate: IceCandidate) {
            printLog("onIceCandidate // iceCandidate : $iceCandidate")
            webSocketRtcClient.sendLocalIceCandidate(iceCandidate)
        }

        override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate?>) {
            printLog("onIceCandidatesRemoved")
            webSocketRtcClient.sendLocalIceCandidateRemovals(iceCandidates)
        }

        override fun onAddStream(mediaStream: MediaStream?) {
            printLog("onAddStream // $mediaStream")
        }

        override fun onRemoveStream(mediaStream: MediaStream?) {
            printLog("onRemoveStream // $mediaStream")
        }

        override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<MediaStream>?) {
            printLog("onAddTrack")
        }

        override fun onDataChannel(dataChannel: DataChannel?) {
            printLog("onDataChannel // DataChannel opened")

            dataChannel?.registerObserver(object : DataChannel.Observer {
                override fun onMessage(buffer: DataChannel.Buffer?) {
                    val byteArray = ByteArray(buffer?.data?.capacity() ?: return)
                    buffer.data.get(byteArray)
                    printLog("DataChannel onMessage : $byteArray")
                }

                override fun onBufferedAmountChange(previousAmount: Long) {
                    printLog("DataChannel onBufferedAmountChange // label : ${dataChannel.label()}, state : ${dataChannel.state()}")
                }

                override fun onStateChange() {
                    printLog("DataChannel onStateChange // label : ${dataChannel.label()}, state : ${dataChannel.state()}")
                }
            })
        }

        override fun onRenegotiationNeeded() {
            printLog("onRenegotiationNeeded")
        }
    }
}
