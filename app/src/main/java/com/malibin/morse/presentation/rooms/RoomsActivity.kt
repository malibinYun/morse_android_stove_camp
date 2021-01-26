package com.malibin.morse.presentation.rooms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malibin.morse.databinding.ActivityRoomsBinding

class RoomsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
