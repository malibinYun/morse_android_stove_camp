package com.malibin.morse.data.entity

/**
 * Created By Malibin
 * on 2월 02, 2021
 */

data class Account(
    val nickname: String,
    val point: Int,
    val followings: List<String>,
)
