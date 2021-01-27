package com.malibin.morse.data.service.response

/**
 * Created By Malibin
 * on 1월 22, 2021
 */

data class BaseResponse<T>(
    val timestamp: String,
    val message: String,
    val data: T,
)
