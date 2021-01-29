package com.malibin.morse.presentation.viewer

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.data.repository.AuthRepository
import com.malibin.morse.data.repository.ChatMessageRepository
import com.malibin.morse.rtc.StreamingMode
import com.malibin.morse.rtc.WebRtcClient
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.webrtc.EglBase
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 21, 2021
 */

class ViewerViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    val eglBase: EglBase,
    private val chatMessageRepository: ChatMessageRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private lateinit var webRtcClient: WebRtcClient

    private val _rtcState = MutableLiveData(WebRtcClientEvents.State.INITIAL)
    val rtcState: LiveData<WebRtcClientEvents.State> = _rtcState

    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    private var isInitial: Boolean = true

    fun connect() {
        if (isInitial) {
            isInitial = false
            webRtcClient =
                WebRtcClient(context, eglBase, StreamingMode.VIEWER, WebRtcClientEventsImpl())
            webRtcClient.connectPeer()
        }
    }

    fun isConnected(): Boolean = rtcState.value == WebRtcClientEvents.State.CONNECTED

    fun attachRenderer(renderer: VideoSink) {
        webRtcClient.attachVideoRenderer(renderer)
    }

    fun detachRenderer(renderer: VideoSink) {
        webRtcClient.detachVideoRenderer(renderer)
    }

    fun sendChatMessage(message: String, roomIdx: Int) {
        val chatMessage = ChatMessage(message, "말리빈")
        val currentChatMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
        currentChatMessages.add(chatMessage)
        _chatMessages.value = currentChatMessages

//        viewModelScope.launch {
//            val token = authRepository.getAccessToken() ?: error("Token must not be null")
//            chatMessageRepository.sendChatMessage(token, roomIdx, ChatMessage(message))
//        }
    }

    override fun onCleared() {
        super.onCleared()
        webRtcClient.close()
        eglBase.release()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents {
        override fun onStateChanged(state: WebRtcClientEvents.State) {
            _rtcState.postValue(state)
        }
    }
}
