package com.malibin.morse.presentation.broadcast

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.rtc.PeerConnectionClient
import dagger.hilt.android.qualifiers.ApplicationContext
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
) : ViewModel(), PeerConnection.Observer {

    private lateinit var peerConnectionClient: PeerConnectionClient

    fun connectPeer(eglBase: EglBase, videoCapturer: VideoCapturer, localRenderer: VideoSink) {
        peerConnectionClient = PeerConnectionClient(context, eglBase)
        peerConnectionClient.connectPeer(videoCapturer, localRenderer, this)
    }

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

    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        // 비동기동작
        // appRtcClient.sendLocalIceCandidate(candidate)
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>?) {
        // 비동기동작
        //    appRtcClient.sendLocalIceCandidateRemovals(candidates)
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
