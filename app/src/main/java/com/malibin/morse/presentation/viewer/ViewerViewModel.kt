package com.malibin.morse.presentation.viewer

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
 * on 1월 21, 2021
 */

class ViewerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private lateinit var webRtcClient: WebRtcClient

    fun connect(eglBase: EglBase, videoRenderer: VideoSink) {
        webRtcClient =
            WebRtcClient(context, eglBase, StreamingMode.VIEWER, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(videoRenderer)
    }

    fun attachRenderer(renderer: VideoSink) {
        webRtcClient.attachVideoRenderer(renderer)
    }

    fun detachRenderer(renderer: VideoSink) {
        webRtcClient.detachVideoRenderer(renderer)

    }

    fun disconnect() {

    }

    override fun onCleared() {
        super.onCleared()
        webRtcClient.close()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents
}
