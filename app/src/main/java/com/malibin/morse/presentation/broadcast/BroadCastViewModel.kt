package com.malibin.morse.presentation.broadcast

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.malibin.morse.rtc.StreamingMode
import com.malibin.morse.rtc.WebRtcClient
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.qualifiers.ApplicationContext
import org.webrtc.EglBase
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
        webRtcClient =
            WebRtcClient(context, eglBase, StreamingMode.BROADCAST, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(videoRenderer)
    }

    fun disconnect() {
        webRtcClient.close()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents{
        override fun onStateChanged(state: WebRtcClientEvents.State) {
            println("onStateChanged $state")
        }
    }
}
