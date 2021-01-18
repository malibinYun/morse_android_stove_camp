package com.malibin.morse.rtc

import org.java_websocket.handshake.ServerHandshake

/**
 * Created By Malibin
 * on 1월 18, 2021
 */

interface WebSocketCallback {

    fun onOpen(handshake: ServerHandshake?)

    fun onMessage(message: String?)

    fun onClose(code: Int, reason: String?, remote: Boolean)

    fun onError(exception: Exception?)
}
