package com.malibin.morse.presentation.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.malibin.morse.databinding.ActivitySearchBinding
import com.malibin.morse.presentation.rooms.RoomsAdapter
import com.malibin.morse.presentation.viewer.ViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roomsAdapter = RoomsAdapter()
        roomsAdapter.onRoomClick = {
            val intent = Intent(this, ViewerActivity::class.java)
            intent.putExtra(ViewerActivity.KEY_ROOM, it)
        }

        binding.lifecycleOwner = this
        binding.viewModel = searchViewModel
        binding.listSearchResults.adapter = roomsAdapter

        searchViewModel.rooms.observe(this) {
            roomsAdapter.submitList(it)
        }
    }
}
