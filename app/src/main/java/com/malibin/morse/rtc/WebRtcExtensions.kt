package com.malibin.morse.rtc

import org.json.JSONObject
import org.webrtc.IceCandidate

/**
 * Created By Malibin
 * on 1월 18, 2021
 */

fun IceCandidate.toJson(): JSONObject {
    return JSONObject().apply {
        put("candidate", this@toJson.sdp)
        put("sdpMid", this@toJson.sdpMid)
        put("sdpMLineIndex", this@toJson.sdpMLineIndex)
    }
}
