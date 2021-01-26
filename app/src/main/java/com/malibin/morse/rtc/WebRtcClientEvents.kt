package com.malibin.morse.rtc

/**
 * Created By Malibin
 * on 1월 25, 2021
 */

interface WebRtcClientEvents {
    fun onStateChanged(state: State)

    enum class State {
        INITIAL,
        CONNECTED,
        DISCONNECTED,
        FINISH_BROADCAST,
        ERROR;
    }
}
