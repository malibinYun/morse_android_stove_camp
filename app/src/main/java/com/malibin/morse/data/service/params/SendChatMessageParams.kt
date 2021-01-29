package com.malibin.morse.data.service.params

data class SendChatMessageParams(
    val roomIdx: Int,
    val userType: String,
    val textMessage: String,
)
