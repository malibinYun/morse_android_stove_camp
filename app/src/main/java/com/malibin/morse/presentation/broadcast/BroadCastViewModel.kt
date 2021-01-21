package com.malibin.morse.presentation.broadcast

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.malibin.morse.R
import com.malibin.morse.data.StreamingMode
import com.malibin.morse.data.entity.ID
import com.malibin.morse.data.service.response.SocketResponse
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.rtc.CreateOfferCallback
import com.malibin.morse.rtc.MediaTrackManager
import com.malibin.morse.rtc.PeerConnectionClient
import com.malibin.morse.rtc.WebRtcClient
import com.malibin.morse.rtc.WebRtcClientEvents
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
import org.webrtc.SessionDescription
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class BroadCastViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private lateinit var webRtcClient: WebRtcClient

    fun connect(eglBase: EglBase, videoRenderer: VideoSink) {
        webRtcClient = WebRtcClient(context, eglBase, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(videoRenderer, StreamingMode.BROADCAST)
    }

    fun disconnect() {
        webRtcClient.close()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents
}
