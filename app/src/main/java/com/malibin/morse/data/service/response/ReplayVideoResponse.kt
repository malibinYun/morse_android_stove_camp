package com.malibin.morse.data.service.response

import com.malibin.morse.data.entity.ReplayVideo
import java.util.*

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

data class ReplayVideoResponse(
    val roomIdx: Int,
    val presenterIdx: Int,
    val presenterNickname: String,
    val recordLocation: String,
    val videoThumnailLocation: String?,
    val imageThumnailLocation: String?,
    val title: String,
    val contents: String,
    val createdAt: Long,
    val endStreamAt: Long,
) {
    fun toReplayVideo() = ReplayVideo(
        id = roomIdx,
        url = recordLocation,
        imageThumbnailUrl = imageThumnailLocation.orEmpty(),
        gifThumbnailUrl = videoThumnailLocation.orEmpty(),
        title = title,
        nickname = presenterNickname,
        content = contents,
        broadCastDate = Date(createdAt),
    )
}
