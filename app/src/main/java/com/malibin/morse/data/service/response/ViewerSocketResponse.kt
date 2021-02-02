package com.malibin.morse.data.service.response

/**
 * Created By Malibin
 * on 2월 02, 2021
 */

data class ViewerSocketResponse(
    val presenterIdx: Int,
    val roomIdx: Int,
    val viewerCount: Int,
    val title: String,
    val contents: String,
    val presenterNickname: String,
    val createdDt: String,
)
