package com.malibin.morse.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.malibin.morse.databinding.ActivityTestBinding
import com.malibin.morse.presentation.broadcast.BroadCastActivity
import com.malibin.morse.presentation.viewer.ViewerActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBroadcast.setOnClickListener {
            val intent = Intent(this, BroadCastActivity::class.java)
            startActivity(intent)
        }

        binding.buttonViewer.setOnClickListener {
            val intent = Intent(this, ViewerActivity::class.java)
            startActivity(intent)
        }
    }
}
