package com.malibin.morse.presentation.rooms

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malibin.morse.data.entity.Room
import com.malibin.morse.databinding.ActivityRoomsBinding
import com.malibin.morse.databinding.ItemRoomBinding
import com.malibin.morse.presentation.mypage.MyPageActivity
import com.malibin.morse.presentation.replay.ReplaysActivity
import com.malibin.morse.presentation.rooms.create.CreateRoomActivity
import com.malibin.morse.presentation.search.SearchActivity
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.presentation.viewer.ViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomsActivity : AppCompatActivity() {
    private val roomsViewModel: RoomsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding)

        roomsViewModel.loadAllRooms()
    }

    private fun initView(binding: ActivityRoomsBinding) {
        val roomsAdapter = RoomsAdapter()
        roomsAdapter.onRoomClick = { deployViewerActivity(it) }
        binding.viewModel = roomsViewModel
        binding.lifecycleOwner = this
        binding.listRoom.adapter = roomsAdapter
        binding.buttonMypage.setOnClickListener { deployActivityOf(MyPageActivity::class.java) }
        binding.buttonCreateRoom.setOnClickListener { deployActivityOf(CreateRoomActivity::class.java) }
        binding.buttonSearch.setOnClickListener { deployActivityOf(SearchActivity::class.java) }
        binding.buttonReplayVideos.setOnClickListener { deployActivityOf(ReplaysActivity::class.java) }
        binding.windowSwipeRefresh.setOnRefreshListener { roomsViewModel.loadAllRooms() }
        roomsViewModel.rooms.observe(this) {
            roomsAdapter.submitList(it)
            binding.windowSwipeRefresh.isRefreshing = false
        }
    }

    private fun <T> deployActivityOf(targetActivity: Class<T>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
    }

    private fun deployViewerActivity(room: Room) {
        val intent = Intent(this, ViewerActivity::class.java).apply {
            putExtra(ViewerActivity.KEY_ROOM, room)
        }
        startActivityForResult(intent, ViewerActivity.REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ViewerActivity.REQUEST_CODE && resultCode == ViewerActivity.RESULT_ALREADY_CLOSED) {
            val room = data?.getSerializableExtra(ViewerActivity.KEY_ROOM) as? Room ?: return
            roomsViewModel.removeRoom(room)
        }
    }
}
