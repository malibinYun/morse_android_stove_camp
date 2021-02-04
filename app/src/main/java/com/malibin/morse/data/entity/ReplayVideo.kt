package com.malibin.morse.data.entity

import java.util.*

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

data class ReplayVideo(
    val id: Int,
    val url: String,
    val thumbnailUrl: String,
    val title: String,
    val nickname: String,
    val content: String,
    val broadCastDate: Date
)
