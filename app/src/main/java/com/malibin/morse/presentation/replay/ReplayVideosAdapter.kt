package com.malibin.morse.presentation.replay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malibin.morse.data.entity.ReplayVideo
import com.malibin.morse.databinding.ItemReplayVideoBinding

/**
 * Created By Malibin
 * on 2월 04, 2021
 */

class ReplayVideosAdapter :
    ListAdapter<ReplayVideo, ReplayVideosAdapter.ViewHolder>(DiffItemCallback()) {

    private var onItemClickListener: ((ReplayVideo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReplayVideoBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemReplayVideoBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(replayVideo: ReplayVideo) {
            binding.replay = replayVideo
            binding.root.setOnClickListener { onItemClickListener?.invoke(replayVideo) }
        }
    }

    private class DiffItemCallback : DiffUtil.ItemCallback<ReplayVideo>() {
        override fun areItemsTheSame(oldItem: ReplayVideo, newItem: ReplayVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReplayVideo, newItem: ReplayVideo): Boolean {
            return oldItem == newItem
        }
    }
}
