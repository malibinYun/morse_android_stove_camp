package com.malibin.morse.data.service.params

/**
 * Created By Malibin
 * on 2월 01, 2021
 */

data class RequestRoomParams(
    val token: String,
    val title: String? = null,
    val content: String? = null,
    val roomId: Int? = null,
)
