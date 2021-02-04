package com.malibin.morse.presentation.replay

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.malibin.morse.databinding.ActivityReplaysBinding
import com.malibin.morse.presentation.replay.player.ReplayActivity
import com.malibin.morse.presentation.replay.player.ReplayActivity.Companion.KEY_VIDEO_URL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplaysActivity : AppCompatActivity() {

    private val replaysViewModel: ReplaysViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityReplaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ReplayVideosAdapter()
        adapter.onItemClickListener = {
            val intent = Intent(this, ReplayActivity::class.java)
            intent.putExtra(KEY_VIDEO_URL, it.url)
            startActivity(intent)
        }
        binding.listReplays.adapter = adapter
        binding.viewModel = replaysViewModel
        binding.lifecycleOwner = this

        replaysViewModel.loadAllReplayVideos()
        replaysViewModel.replayVideos.observe(this) { adapter.submitList(it) }
    }
}
