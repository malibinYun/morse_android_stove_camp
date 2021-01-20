package com.malibin.morse.data.service.response

import com.google.gson.annotations.SerializedName
import org.webrtc.IceCandidate

/**
 * Created By Malibin
 * on 1월 20, 2021
 */

data class Candidate(
    @SerializedName("candidate")
    val sdp: String,
    val sdpMid: String,
    val sdpMLineIndex: Int,
) {
    fun toIceCandidate(): IceCandidate = IceCandidate(sdpMid, sdpMLineIndex, sdp)
}