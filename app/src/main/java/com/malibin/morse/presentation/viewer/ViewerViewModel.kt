package com.malibin.morse.presentation.viewer

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.malibin.morse.presentation.utils.printLog
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
    val eglBase: EglBase,
) : ViewModel() {

    private lateinit var webRtcClient: WebRtcClient

    private val _rtcState = MutableLiveData(WebRtcClientEvents.State.INITIAL)
    val rtcState: LiveData<WebRtcClientEvents.State> = _rtcState

    var isInitial: Boolean = true
        private set

    val isNotInitial: Boolean
        get() = !isInitial

    fun connect(videoRenderer: VideoSink) {
        webRtcClient =
            WebRtcClient(context, eglBase, StreamingMode.VIEWER, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(videoRenderer)
    }

    // 일단 위에거 대충 복사
    fun connect() {
        if (isInitial) {
            isInitial = false
            webRtcClient =
                WebRtcClient(context, eglBase, StreamingMode.VIEWER, WebRtcClientEventsImpl())
            webRtcClient.connectPeer()
        }
    }

    fun attachRenderer(renderer: VideoSink) {
        webRtcClient.attachVideoRenderer(renderer)
    }

    fun detachRenderer(renderer: VideoSink) {
        webRtcClient.detachVideoRenderer(renderer)
    }

    override fun onCleared() {
        super.onCleared()
        webRtcClient.close()
        printLog("onCleared webRtcClient closed")
        eglBase.release()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents {
        override fun onStateChanged(state: WebRtcClientEvents.State) {
            _rtcState.postValue(state)
        }
    }
}
