package com.malibin.morse.data.service.response

import com.malibin.morse.data.entity.ReplayVideo
import java.util.*

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

data class ReplayVideoResponse(
    val url: String,
    val thumbnailUrl: String,
    val title: String,
    val contents: String,
    val createdAt: Long,
    val id: Int,
    val nickname: String,
) {
    fun toReplayVideo() = ReplayVideo(
        id = id,
        url = url,
        thumbnailUrl = thumbnailUrl,
        title = title,
        nickname = nickname,
        content = contents,
        broadCastDate = Date(createdAt),
    )
}
