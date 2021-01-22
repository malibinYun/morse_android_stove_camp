package com.malibin.morse.rtc

/**
 * Created By Malibin
 * on 1월 21, 2021
 */

enum class StreamingMode(
    val id: String,
) {
    BROADCAST("presenter"),
    VIEWER("viewer");
}
