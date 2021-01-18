package com.malibin.morse.rtc

import org.webrtc.SessionDescription

/**
 * Created By Malibin
 * on 1월 18, 2021
 */

interface CreateOfferCallback {

    fun onOfferSetSuccess(sessionDescription: SessionDescription)
}
