package com.malibin.morse.data.service.response

import com.malibin.morse.data.entity.Account

/**
 * Created By Malibin
 * on 2월 02, 2021
 */

data class MyPageResponse(
    val nickname: String,
    val point: Int,
    val followNickname: List<String>?,
) {
    fun toAccount(): Account = Account(
        nickname = nickname,
        point = point,
        followings = followNickname ?: emptyList(),
    )
}
