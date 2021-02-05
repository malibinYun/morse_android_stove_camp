package com.malibin.morse.presentation.rooms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malibin.morse.data.entity.Room
import com.malibin.morse.databinding.ItemRoomBinding

/**
 * Created By Malibin
 * on 2월 05, 2021
 */

class RoomsAdapter : ListAdapter<Room, RoomsAdapter.RoomViewHolder>(ItemDiffCallback()) {

    var onRoomClick: ((Room) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RoomViewHolder(ItemRoomBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
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
            binding.root.setOnClickListener { onRoomClick?.invoke(room) }
        }
    }
}
