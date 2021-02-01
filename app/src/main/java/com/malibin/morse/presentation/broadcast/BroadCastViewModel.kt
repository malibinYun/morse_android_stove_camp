package com.malibin.morse.presentation.broadcast

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.data.repository.AuthRepository
import com.malibin.morse.data.repository.ChatMessageRepository
import com.malibin.morse.data.service.params.RequestRoomParams
import com.malibin.morse.data.service.response.ChatMessageResponse
import com.malibin.morse.data.websocket.ChatMessageReceiveClient
import com.malibin.morse.rtc.StreamingMode
import com.malibin.morse.rtc.WebRtcClient
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import org.webrtc.EglBase
import org.webrtc.VideoSink

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

class BroadCastViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    val eglBase: EglBase,
    private val chatMessageRepository: ChatMessageRepository,
    private val authRepository: AuthRepository,
) : ViewModel(), ChatMessageReceiveClient.Callback {

    private lateinit var webRtcClient: WebRtcClient
    private lateinit var chatMessageReceiveClient: ChatMessageReceiveClient

    private val _rtcState = MutableLiveData(WebRtcClientEvents.State.INITIAL)
    val rtcState: LiveData<WebRtcClientEvents.State> = _rtcState

    private val _isMicActivated = MutableLiveData(true)
    val isMicActivated: LiveData<Boolean> = _isMicActivated

    private val _chatMessages = MutableLiveData<List<ChatMessage>>()
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    fun createBroadcastRoom(roomTitle: String, roomContent: String) {
        val params = RequestRoomParams(
            token = runBlocking { authRepository.getAccessToken() }
                ?: error("token cannot be null"),
            title = roomTitle,
            content = roomContent,
        )
        webRtcClient =
            WebRtcClient(context, eglBase, StreamingMode.BROADCAST, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(params)
        chatMessageReceiveClient = ChatMessageReceiveClient(1, this)
    }

    override fun onMessage(response: ChatMessageResponse) {
        webRtcClient.sendChatMessage(response)
    }

    fun attachRenderer(renderer: VideoSink) {
        webRtcClient.attachVideoRenderer(renderer)
    }

    fun detachRenderer(renderer: VideoSink) {
        webRtcClient.detachVideoRenderer(renderer)
    }

    fun switchCamera() {
        webRtcClient.switchCamera()
    }

    fun toggleMic() {
        val isMicActivated = _isMicActivated.value ?: error("_isMicActivated cannot be null")
        _isMicActivated.value = !isMicActivated
        webRtcClient.toggleMic(!isMicActivated)
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

    fun disconnect() {
        webRtcClient.close()
    }

    override fun onCleared() {
        super.onCleared()
        eglBase.release()
    }

    private inner class WebRtcClientEventsImpl : WebRtcClientEvents {
        override fun onStateChanged(state: WebRtcClientEvents.State) {
            _rtcState.postValue(state)
        }

        override fun onChatReceived(chatMessage: ChatMessage) {
            val currentChatMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
            currentChatMessages.add(chatMessage)
            _chatMessages.value = currentChatMessages
        }
    }
}
