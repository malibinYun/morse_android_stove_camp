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
import com.malibin.morse.data.service.params.RequestRoomParams
import com.malibin.morse.data.service.params.SendChatMessageParams
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.rtc.StreamingMode
import com.malibin.morse.rtc.WebRtcClient
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    fun connect(roomIdx: Int) {
        if (isInitial) {
            isInitial = false
            val params = RequestRoomParams(
                token = runBlocking { authRepository.getAccessToken() }
                    ?: error("token cannot be null"),
                roomId = roomIdx,
            )
            webRtcClient =
                WebRtcClient(context, eglBase, StreamingMode.VIEWER, WebRtcClientEventsImpl())
            webRtcClient.connectPeer(params)
        }
    }

    fun isConnected(): Boolean = rtcState.value == WebRtcClientEvents.State.CONNECTED

    fun attachRenderer(renderer: VideoSink) {
        webRtcClient.attachVideoRenderer(renderer)
    }

    fun detachRenderer(renderer: VideoSink) {
        webRtcClient.detachVideoRenderer(renderer)
    }

    fun sendChatMessage(message: String, presenterId: Int) {
//        val chatMessage = ChatMessage(message, "말리빈")
//        val currentChatMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
//        currentChatMessages.add(chatMessage)
//        _chatMessages.value = currentChatMessages

        viewModelScope.launch {
            val chatMessage = SendChatMessageParams("viewer", message, presenterId.toString())
            chatMessageRepository.sendChatMessage(chatMessage)
        }
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

        override fun onChatReceived(chatMessage: ChatMessage) {
            printLog("onChatReceived : $chatMessage")
            val currentChatMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
            currentChatMessages.add(chatMessage)
            _chatMessages.value = currentChatMessages
        }
    }
}
