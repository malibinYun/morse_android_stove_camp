package com.malibin.morse.data.entity

/**
 * Created By Malibin
 * on 1월 20, 2021
 */

enum class ID(
    private val value: String
) {
    PRESENTER_RESPONSE("presenterResponse"),
    VIEWER_RESPONSE("viewerResponse"),
    ICE_CANDIDATE("iceCandidate");

    fun has(value: String): Boolean = this.value == value

    companion object {
        fun findBy(value: String): ID {
            return values().find { it.has(value) }
                ?: throw IllegalArgumentException("cannot find ID of $value")
        }
    }
}
