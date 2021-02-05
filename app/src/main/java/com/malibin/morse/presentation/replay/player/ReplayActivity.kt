package com.malibin.morse.presentation.replay.player

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.malibin.morse.databinding.ActivityReplayLandscapeBinding
import com.malibin.morse.presentation.utils.hideStatusBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplayActivity : AppCompatActivity() {

    private val replayViewModel: ReplayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityReplayLandscapeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        hideStatusBar()

        binding.viewModel = replayViewModel
        binding.lifecycleOwner = this
        val videoUrl = intent.getStringExtra(KEY_VIDEO_URL) ?: error("video url must be send")
        binding.videoView.setMediaController(MediaController(this))
        binding.videoView.setVideoPath(videoUrl)
        binding.videoView.setOnPreparedListener {
            replayViewModel.changeLoadingTo(false)
            binding.videoView.start()
        }
        binding.videoView.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> replayViewModel.changeLoadingTo(true)
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> replayViewModel.changeLoadingTo(false)
            }
            return@setOnInfoListener true
        }
    }

    companion object {
        const val KEY_VIDEO_URL = "KEY_VIDEO_URL"
    }
}

class ReplayViewModel @ViewModelInject constructor() : ViewModel() {
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    fun changeLoadingTo(isLoading: Boolean) {
        _isLoading.postValue(isLoading)
    }
}
