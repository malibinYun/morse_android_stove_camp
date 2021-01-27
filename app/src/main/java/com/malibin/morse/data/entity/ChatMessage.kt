package com.malibin.morse.data.entity

/**
 * Created By Malibin
 * on 1월 27, 2021
 */

data class ChatMessage(
    val message: String,
    val userNickname: String,
) {
    enum class Color {
        BLACK, WHITE;
    }
}
