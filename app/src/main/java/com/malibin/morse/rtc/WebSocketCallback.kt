package com.malibin.morse.rtc

import com.malibin.morse.data.service.response.SocketResponse
import org.java_websocket.handshake.ServerHandshake

/**
 * Created By Malibin
 * on 1월 18, 2021
 */

interface WebSocketCallback {

    fun onOpen(handshake: ServerHandshake?)

    fun onMessage(response: SocketResponse?)

    fun onClose(code: Int, reason: String?, remote: Boolean)

    fun onError(exception: Exception?)
}
