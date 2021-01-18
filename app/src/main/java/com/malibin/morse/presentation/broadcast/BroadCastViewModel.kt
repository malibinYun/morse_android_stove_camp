package com.malibin.morse.presentation.broadcast

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.rtc.PeerConnectionClient
import com.malibin.morse.rtc.WebSocketCallback
import com.malibin.morse.rtc.WebSocketRtcClient
import com.malibin.morse.rtc.createPeerConnectionFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import org.java_websocket.handshake.ServerHandshake
import org.webrtc.DataChannel
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.RtpReceiver
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class BroadCastViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private lateinit var peerConnectionClient: PeerConnectionClient
    private val rtcClient = WebSocketRtcClient(WebSocketCallbackImpl())

    fun connectPeer(eglBase: EglBase, videoCapturer: VideoCapturer, localRenderer: VideoSink) {
        peerConnectionClient = PeerConnectionClient(createPeerConnectionFactory(context, eglBase))
        peerConnectionClient.connectPeer(videoCapturer, localRenderer, PeerConnectionObserver())
    }

    private inner class WebSocketCallbackImpl : WebSocketCallback {
        override fun onOpen(handshake: ServerHandshake?) {
            printLog("Socket Opened")
            peerConnectionClient.createOffer()
        }

        override fun onMessage(message: String?) {

        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {

        }

        override fun onError(exception: Exception?) {

        }
    }

    private inner class PeerConnectionObserver : PeerConnection.Observer {
        override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
            printLog("onSignalingChange // SignalingState: $newState")
        }

        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
            // 아래 전부 비동기
            printLog("onIceConnectionChange // ICE newState : $newState")
            if (newState == PeerConnection.IceConnectionState.CONNECTED) {
//            peerConnectionClient.enableStatsEvents(true, 1000)
            }
        }

        override fun onIceConnectionReceivingChange(newState: Boolean) {
            printLog("onIceConnectionReceivingChange // IceConnectionReceiving changed to $newState")
        }

        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
            printLog("onIceGatheringChange // IceGatheringState : $newState")
        }

        override fun onIceCandidate(iceCandidate: IceCandidate) {
            // 비동기동작
            rtcClient.sendLocalIceCandidate(iceCandidate)
        }

        override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate?>) {
            // 비동기동작
            rtcClient.sendLocalIceCandidateRemovals(iceCandidates)
        }

        override fun onAddStream(mediaStream: MediaStream?) {}
        // 중국인 앱 봐야함

        override fun onRemoveStream(mediaStream: MediaStream?) {}
        // 중국인 앱 봐야함

        override fun onDataChannel(dataChannel: DataChannel?) {}
        // 일단 데이터 채널 꺼뒀으니 일단은 ...

        override fun onRenegotiationNeeded() {}

        override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<MediaStream>?) {}
    }
}
