package com.malibin.morse.data.service.response

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

    fun isRejected(): Boolean = response == "rejected"

    enum class ID(
        private val value: String
    ) {
        PRESENTER_RESPONSE("presenterResponse"),
        VIEWER_RESPONSE("viewerResponse"),
        ICE_CANDIDATE("iceCandidate"),
        STOP_COMMUNICATION("stopCommunication"),
        ERROR_RESPONSE("exception");

        fun has(value: String): Boolean = this.value == value

        companion object {
            fun findBy(value: String): ID {
                return values().find { it.has(value) }
                    ?: throw IllegalArgumentException("cannot find ID of $value")
            }
        }
    }
}
