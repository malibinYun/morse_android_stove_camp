package com.malibin.morse.presentation.replay.player

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import android.widget.MediaController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.malibin.morse.databinding.ActivityReplayLandscapeBinding
import com.malibin.morse.presentation.utils.printLog

class ReplayActivity : AppCompatActivity() {

    private val replayViewModel: ReplayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityReplayLandscapeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        binding.viewModel = replayViewModel
        binding.lifecycleOwner = this
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val mediaController = MediaController(this).apply { setAnchorView(binding.videoView) }
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoPath("https://downsups.s3-ap-northeast-1.amazonaws.com/20210204004513561671.webm")
        binding.videoView.setOnPreparedListener {
            replayViewModel.changeLoadingTo(false)
            binding.videoView.start()
        }
        binding.videoView.setOnInfoListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> printLog("MEDIA_INFO_VIDEO_RENDERING_START")
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> replayViewModel.changeLoadingTo(true)
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> replayViewModel.changeLoadingTo(false)
                else -> printLog("somethingelse $what")
            }
            return@setOnInfoListener true
        }
    }

    companion object {
        const val KEY_VIDEO_URL = "KEY_VIDEO_URL"
    }
}
