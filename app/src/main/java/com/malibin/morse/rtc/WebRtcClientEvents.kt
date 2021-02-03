package com.malibin.morse.rtc

import com.malibin.morse.data.entity.ChatMessage

/**
 * Created By Malibin
 * on 1월 25, 2021
 */

interface WebRtcClientEvents {
    fun onStateChanged(state: State)

    fun onChatReceived(chatMessage: ChatMessage)

    fun onCreateRoomId(roomId: Int) {
    }

    enum class State {
        INITIAL,
        CONNECTED,
        DISCONNECTED,
        FINISH_BROADCAST,
        ALREADY_CLOSED,
        ERROR;
    }
}
