package com.malibin.morse.data

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
