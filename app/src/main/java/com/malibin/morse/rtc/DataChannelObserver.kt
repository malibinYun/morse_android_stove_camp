package com.malibin.morse.rtc

import org.webrtc.DataChannel

/**
 * Created By Malibin
 * on 2월 03, 2021
 */

abstract class DataChannelObserver : DataChannel.Observer {
    override fun onBufferedAmountChange(p0: Long) {
        // do nothing
    }

    override fun onStateChange() {
        // do nothing
    }
}
