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
import com.malibin.morse.presentation.rooms.create.CreateRoomActivity
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
        val adapter = RoomsAdapter()
        binding.viewModel = roomsViewModel
        binding.lifecycleOwner = this
        binding.listRoom.adapter = adapter
        binding.buttonCreateRoom.setOnClickListener { deployActivityOf(CreateRoomActivity::class.java) }
        binding.buttonMypage.setOnClickListener { deployActivityOf(MyPageActivity::class.java) }
        binding.windowSwipeRefresh.setOnRefreshListener { roomsViewModel.loadAllRooms() }
        roomsViewModel.rooms.observe(this) {
            printLog(it)
            adapter.submitList(it)
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

    private inner class RoomsAdapter : ListAdapter<Room, RoomViewHolder>(ItemDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
            return RoomViewHolder(ItemRoomBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private class ItemDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    }

    inner class RoomViewHolder(
        private val binding: ItemRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            binding.room = room
            binding.root.setOnClickListener { deployViewerActivity(room) }
        }
    }
}
