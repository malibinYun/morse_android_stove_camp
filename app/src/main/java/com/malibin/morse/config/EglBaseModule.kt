package com.malibin.morse.config

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import org.webrtc.EglBase

/**
 * Created By Malibin
 * on 1월 26, 2021
 */

@Module
@InstallIn(ApplicationComponent::class)
object EglBaseModule {

    @Provides
    fun provideEglBase(): EglBase {
        return EglBase.create()
    }
}
