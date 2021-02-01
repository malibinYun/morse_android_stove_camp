package com.malibin.morse.rtc

import com.malibin.morse.data.service.params.RequestRoomParams
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

interface RtcClient {

    fun connectRoom(params: RequestRoomParams)

    fun sendOfferSessionDescription(
        sessionDescription: SessionDescription,
        streamingMode: StreamingMode
    )

    fun sendAnswerSessionDescription(sessionDescription: SessionDescription)

    fun sendLocalIceCandidate(
        iceCandidate: IceCandidate,
        streamingMode: StreamingMode
    )

    fun sendLocalIceCandidateRemovals(iceCandidates: Array<IceCandidate?>)
}
