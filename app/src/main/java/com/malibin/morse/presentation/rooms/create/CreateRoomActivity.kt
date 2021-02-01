package com.malibin.morse.presentation.rooms.create

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malibin.morse.databinding.ActivityCreateRoomBinding
import com.malibin.morse.presentation.broadcast.BroadCastActivity

class CreateRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonCreateRoom.setOnClickListener {
            it.isEnabled = false
            val roomTitle = binding.textRoomTitle.text.toString()
            val roomContent = binding.textRoomContent.text.toString()
            val intent = Intent(this, BroadCastActivity::class.java).apply {
                putExtra(BroadCastActivity.KEY_ROOM_TITLE, roomTitle)
                putExtra(BroadCastActivity.KEY_ROOM_CONTENT, roomContent)
            }
            startActivity(intent)
            finish()
        }
    }
}
