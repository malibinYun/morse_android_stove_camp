package com.malibin.morse.data.service.response

import com.malibin.morse.data.entity.ChatMessage

data class ChatMessageResponse(
    val id: String,
    val nickname: String,
    val message: String,
    val time: String,
) {
    fun toChatMessage() = ChatMessage(
        message = message,
        userNickname = nickname,
        isPresenter = true,
    )
}
