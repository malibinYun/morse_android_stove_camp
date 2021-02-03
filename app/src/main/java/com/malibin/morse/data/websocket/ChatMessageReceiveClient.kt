package com.malibin.morse.data.websocket

import com.google.gson.Gson
import com.malibin.morse.data.service.response.ChatMessageResponse
import com.malibin.morse.presentation.utils.printLog
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

/**
 * Created By Malibin
 * on 1월 29, 2021
 */

class ChatMessageReceiveClient(
    private val roomIdx: Int,
    private val token: String,
    private val onMessageCallback: Callback,
) : WebSocketClient(HOST_URI) {

    private val gson: Gson = Gson()

    override fun onOpen(handshakedata: ServerHandshake?) {
        val json = JSONObject().apply {
            put("id", "start")
            put("token", token)
        }
        send(json.toString())
    }

    override fun onMessage(message: String?) {
        printLog("chat onMessage $message")
        val response = gson.fromJson(message, ChatMessageResponse::class.java)
        onMessageCallback.onMessageFromSocket(response)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        printLog("ChatMessageReceiveClient onClose // code : $code reason : $reason, remote : $remote")
    }

    override fun onError(exception: Exception?) {
        printLog("ChatMessageReceiveClient onError // ${exception?.message}")
    }

    companion object {
        private val HOST_URI = URI("ws://downsups.onstove.com:8002/chatting")
    }

    interface Callback {
        fun onMessageFromSocket(response: ChatMessageResponse)
    }
}
