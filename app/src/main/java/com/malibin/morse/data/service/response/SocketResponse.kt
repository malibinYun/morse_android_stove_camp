package com.malibin.morse.data.service.response

import com.malibin.morse.data.entity.ID

/**
 * Created By Malibin
 * on 1월 20, 2021
 */

data class SocketResponse(
    private val id: String,
    val response: String?,
    val sdpAnswer: String?,
    val candidate: Candidate?,
    val message: String,
    val success: Boolean,
    val from: String?,
) {
    val responseId: ID
        get() = ID.findBy(id)
}
