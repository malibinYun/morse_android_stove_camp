package com.malibin.morse.presentation.rooms.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malibin.morse.databinding.ActivityCreateRoomBinding

class CreateRoomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
