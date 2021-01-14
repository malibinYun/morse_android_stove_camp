package com.malibin.morse.rtc

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * Created By Malibin
 * on 1월 14, 2021
 */

interface RtcClient {

    fun connectRoom()

    fun sendOfferSessionDescription(sessionDescription: SessionDescription)

    fun sendAnswerSessionDescription(sessionDescription: SessionDescription)

    fun sendLocalIceCandidate(iceCandidate: IceCandidate)

    fun sendLocalIceCandidateRemovals(iceCandidates: Array<IceCandidate?>)
}
