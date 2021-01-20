package com.malibin.morse.rtc

import android.content.Context
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSink
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

/**
 * Created By Malibin
 * on 1월 19, 2021
 */

class MediaManager(
    private val eglBase: EglBase,
    private val context: Context,
    private val peerConnectionFactory: PeerConnectionFactory,
) {
    val audioTrack = createAudioTrack()
    val videoTrack = createVideoTrack()

    private fun createAudioTrack(): AudioTrack {
        val audioSource = peerConnectionFactory.createAudioSource(createAudioConstraints(false))
        return peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource) //AUDIO_TRACK_ID
            .apply { setEnabled(true) }
    }

    private fun createAudioConstraints(noAudioProcessing: Boolean): MediaConstraints {
        if (noAudioProcessing) {
            return MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "false"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "false"))
            }
        }
        return MediaConstraints()
    }

    private fun createVideoTrack(): VideoTrack {
        val videoSource = createVideoSource()
        val videoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        videoTrack.setEnabled(true)
        return videoTrack
    }

    private fun createVideoSource(): VideoSource {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
        val videoCapturer = createVideoCapturer()
        val videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(1280, 720, 30) // video width, height, fps
        return videoSource
    }

    private fun createVideoCapturer(): VideoCapturer {
        val cameraEnumerator = Camera2Enumerator(context)
        val deviceName = cameraEnumerator.deviceNames.find { cameraEnumerator.isFrontFacing(it) }
            ?: cameraEnumerator.deviceNames.find { cameraEnumerator.isBackFacing(it) }
            ?: error("Cannot Find Camera")
        return cameraEnumerator.createCapturer(deviceName, null)
    }

    fun attachLocalVideoRenderer(renderer: VideoSink) {
        videoTrack.addSink(renderer)
    }

    fun detachLocalVideoRenderer(renderer: VideoSink) {
        videoTrack.removeSink(renderer)
    }

    fun dispose() {
        audioTrack.dispose()
        videoTrack.dispose()
    }

    companion object {
        private const val MEDIA_STREAM_ID = "ARDAMS"
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
    }
}
