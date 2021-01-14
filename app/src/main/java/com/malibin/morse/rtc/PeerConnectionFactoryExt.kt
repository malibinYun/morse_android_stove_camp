package com.malibin.morse.rtc

import android.content.Context
import com.malibin.morse.presentation.utils.printLog
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule

/**
 * Created By Malibin
 * on 1월 13, 2021
 */

fun createPeerConnectionFactory(
    context: Context,
    eglBase: EglBase
): PeerConnectionFactory {

    val fieldTrials = StringBuilder()
    fieldTrials.append("WebRTC-IntelVP8/Enabled/")
    //VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL

    PeerConnectionFactory.initialize(
        PeerConnectionFactory.InitializationOptions.builder(context)
            .setFieldTrials(fieldTrials.toString())
            .setEnableInternalTracer(true)
            .createInitializationOptions()
    )
//  WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true)

    val audioDeviceModule = createJavaAudioDeviceModule(context)
    val encoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, false)
    val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)

    return PeerConnectionFactory.builder()
        .setAudioDeviceModule(audioDeviceModule)
        .setVideoEncoderFactory(encoderFactory)
        .setVideoDecoderFactory(decoderFactory)
        .createPeerConnectionFactory()
}

private fun createJavaAudioDeviceModule(context: Context): AudioDeviceModule {
    return JavaAudioDeviceModule.builder(context)
        .setAudioRecordErrorCallback(AudioRecordErrorCallBack())
        .setAudioTrackErrorCallback(AudioTrackErrorCallBack())
        .createAudioDeviceModule()
}

private class AudioRecordErrorCallBack
    : JavaAudioDeviceModule.AudioRecordErrorCallback {
    override fun onWebRtcAudioRecordInitError(errorMessage: String?) {
        printLog("onWebRtcAudioRecordInitError : errorMessage = $errorMessage")
    }

    override fun onWebRtcAudioRecordStartError(
        errorCode: JavaAudioDeviceModule.AudioRecordStartErrorCode?,
        errorMessage: String?
    ) {
        printLog("onWebRtcAudioRecordStartError : errorCode = $errorCode, errorMessage = $errorMessage")

    }

    override fun onWebRtcAudioRecordError(errorMessage: String?) {
        printLog("onWebRtcAudioRecordError : errorMessage = $errorMessage")
    }
}

private class AudioTrackErrorCallBack
    : JavaAudioDeviceModule.AudioTrackErrorCallback {
    override fun onWebRtcAudioTrackInitError(errorMessage: String?) {
        printLog("onWebRtcAudioTrackInitError : errorMessage = $errorMessage")
    }

    override fun onWebRtcAudioTrackStartError(
        errorCode: JavaAudioDeviceModule.AudioTrackStartErrorCode?,
        errorMessage: String?
    ) {
        printLog("onWebRtcAudioTrackStartError : errorCode = $errorCode, errorMessage = $errorMessage")
    }

    override fun onWebRtcAudioTrackError(errorMessage: String?) {
        printLog("onWebRtcAudioTrackError : errorMessage = $errorMessage")
    }
}
