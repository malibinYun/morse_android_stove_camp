package com.malibin.morse.data.entity

import java.util.*

/**
 * Created By Malibin
 * on 1월 27, 2021
 */

data class ChatMessage(
    val message: String,
    val userNickname: String,
    val id: String = UUID.randomUUID().toString(),
) {
    enum class Color {
        BLACK, WHITE;
    }
}
