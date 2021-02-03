package com.malibin.morse.presentation.broadcast

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
import com.malibin.morse.data.service.response.ChatMessageResponse
import com.malibin.morse.data.websocket.ChatMessageReceiveClient
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

    private val accessToken = runBlocking { authRepository.getAccessToken() }
        ?: error("token cannot be null")

    var roomId: Int = -1
        private set

    fun createBroadcastRoom(roomTitle: String, roomContent: String) {
        val params = RequestRoomParams(
            token = accessToken,
            title = roomTitle,
            content = roomContent,
        )
        webRtcClient =
            WebRtcClient(context, eglBase, StreamingMode.BROADCAST, WebRtcClientEventsImpl())
        webRtcClient.connectPeer(params)
    }

    private fun createChatReceiveWebSocketInternal() {
        chatMessageReceiveClient = ChatMessageReceiveClient(accessToken, this)
        chatMessageReceiveClient.connect()
    }

    override fun onMessageFromSocket(response: ChatMessageResponse) {
        webRtcClient.sendChatMessage(response)

        val currentChatMessages = _chatMessages.value?.toMutableList() ?: mutableListOf()
        currentChatMessages.add(response.toChatMessage())
        _chatMessages.postValue(currentChatMessages)
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

    fun sendChatMessage(message: String) = viewModelScope.launch {
        val sendingMessageParams = SendChatMessageParams(
            userType = "presenter",
            textMessage = message,
            presenterIdx = null
        )
        chatMessageRepository.sendChatMessage(sendingMessageParams)
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
        }

        override fun onCreateRoomId(roomId: Int) {
            createChatReceiveWebSocketInternal()
            this@BroadCastViewModel.roomId = roomId
        }
    }
}
