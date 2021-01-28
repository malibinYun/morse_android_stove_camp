package com.malibin.morse.presentation.chatting

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.databinding.ItemChatMessageBinding

/**
 * Created By Malibin
 * on 1월 27, 2021
 */

class ChatMessagesAdapter(
    private val randomColorGenerator: ColorGenerator,
) : ListAdapter<ChatMessage, ChatMessagesAdapter.ViewHolder>(DiffItemCallback()) {

    private val nicknameColors = mutableMapOf<String, String>()

    var chatMessageColor = ChatMessage.Color.BLACK

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemChatMessageBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatMessage = getItem(position)
        if (nicknameColors[chatMessage.userNickname] == null) {
            nicknameColors[chatMessage.userNickname] = randomColorGenerator.createColorCode()
        }
        holder.bind(chatMessage)
    }

    fun getLastPosition(): Int = itemCount - 1

    inner class ViewHolder(
        private val binding: ItemChatMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessage) {
            binding.message.text = createChatMessageSpan(chatMessage)
        }

        private fun createChatMessageSpan(chatMessage: ChatMessage): CharSequence {
            val nicknameColor = nicknameColors[chatMessage.userNickname]
            val nicknameSpan = SpannableString(chatMessage.userNickname)
            nicknameSpan.setSpan(
                ForegroundColorSpan(Color.parseColor(nicknameColor)),
                0,
                chatMessage.userNickname.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            nicknameSpan.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                chatMessage.userNickname.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val messageColor = when (chatMessageColor) {
                ChatMessage.Color.BLACK -> Color.BLACK
                ChatMessage.Color.WHITE -> Color.WHITE
            }
            val messageSpan = SpannableString(" : ${chatMessage.message}".replace(" ", "\u00A0"))
            messageSpan.setSpan(
                ForegroundColorSpan(messageColor),
                0,
                chatMessage.userNickname.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return TextUtils.concat(nicknameSpan, messageSpan)
        }
    }

    private class DiffItemCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
