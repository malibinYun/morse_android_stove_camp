package com.malibin.morse.rtc

import android.content.Context
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
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

class MediaTrackManager(
    eglBase: EglBase,
    context: Context,
    factory: PeerConnectionFactory,
) {
    val audioTrack: AudioTrack by lazy { createAudioTrack(factory, audioSource) }
    private val audioSource: AudioSource by lazy { createAudioSource(factory) }

    val videoTrack: VideoTrack by lazy { createVideoTrack(factory, videoSource) }
    private val videoCapturer: VideoCapturer by lazy { createVideoCapturer(context) }
    private val videoSource: VideoSource by lazy {
        createVideoSource(factory, eglBase, context, videoCapturer)
    }

    private fun createAudioSource(factory: PeerConnectionFactory): AudioSource {
        return factory.createAudioSource(createAudioConstraints(false))
    }

    private fun createAudioTrack(
        factory: PeerConnectionFactory,
        audioSource: AudioSource
    ): AudioTrack {
        return factory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
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

    private fun createVideoTrack(
        factory: PeerConnectionFactory,
        videoSource: VideoSource,
    ): VideoTrack {
        val videoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        videoTrack.setEnabled(true)
        return videoTrack
    }

    private fun createVideoSource(
        factory: PeerConnectionFactory,
        eglBase: EglBase,
        context: Context,
        videoCapturer: VideoCapturer,
    ): VideoSource {
        val surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
        val videoSource = factory.createVideoSource(videoCapturer.isScreencast)
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.capturerObserver)
        videoCapturer.startCapture(1280, 720, 30) // video width, height, fps
        return videoSource
    }

    private fun createVideoCapturer(context: Context): VideoCapturer {
        val cameraEnumerator = Camera2Enumerator(context)
        val deviceName = cameraEnumerator.deviceNames.find { cameraEnumerator.isFrontFacing(it) }
            ?: cameraEnumerator.deviceNames.find { cameraEnumerator.isBackFacing(it) }
            ?: error("Cannot Find Camera")
        return cameraEnumerator.createCapturer(deviceName, null)
    }

    fun attachVideoRenderer(renderer: VideoSink) {
        videoTrack.addSink(renderer)
    }

    fun detachVideoRenderer(renderer: VideoSink) {
        videoTrack.removeSink(renderer)
    }

    fun switchCamera() {
        (videoCapturer as CameraVideoCapturer).switchCamera(null)
    }

    fun dispose() {
        audioTrack.dispose()
        audioSource.dispose()

        videoTrack.dispose()
        videoCapturer.stopCapture()
        videoCapturer.dispose()
        videoSource.dispose()
    }

    companion object {
        private const val MEDIA_STREAM_ID = "ARDAMS"
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
    }
}
