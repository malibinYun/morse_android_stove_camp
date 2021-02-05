package com.malibin.morse.presentation.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import com.malibin.morse.databinding.ActivitySearchBinding
import com.malibin.morse.presentation.rooms.RoomsAdapter
import com.malibin.morse.presentation.viewer.ViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val roomsAdapter = RoomsAdapter()
        roomsAdapter.onRoomClick = {
            val intent = Intent(this, ViewerActivity::class.java)
            intent.putExtra(ViewerActivity.KEY_ROOM, it)
            startActivity(intent)
        }

        binding.lifecycleOwner = this
        binding.viewModel = searchViewModel
        binding.listSearchResults.adapter = roomsAdapter
        binding.textSearchKeyword.setOnEditorActionListener(this)

        searchViewModel.rooms.observe(this) {
            roomsAdapter.submitList(it)
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchViewModel.searchRooms()
            return true
        }
        return false
    }
}
