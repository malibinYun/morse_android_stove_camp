package com.malibin.morse.data.service.params

data class SendChatMessageParams(
    val userType: String,
    val textMessage: String,
    val presenterIdx: Int? = null,
)
