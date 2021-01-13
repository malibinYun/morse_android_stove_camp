package com.malibin.morse.presentation

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp

/**
 * Created By Malibin
 * on 1월 11, 2021
 */

@HiltAndroidApp
class MorseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.addLogAdapter(AndroidLogAdapter(getLoggerFormatStrategy()))
    }

    private fun getLoggerFormatStrategy(): FormatStrategy {
        return PrettyFormatStrategy.newBuilder()
            .methodCount(0)
            .tag("MalibinDebug")
            .build()
    }
}
