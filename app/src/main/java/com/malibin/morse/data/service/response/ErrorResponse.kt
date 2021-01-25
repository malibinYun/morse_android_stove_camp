package com.malibin.morse.data.service.response

/**
 * Created By Malibin
 * on 1월 25, 2021
 */

data class ErrorResponse(
    val timestamp: String,
    val status: Int,
    val message: String,
    val path: String,
)
