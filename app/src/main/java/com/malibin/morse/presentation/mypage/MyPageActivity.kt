package com.malibin.morse.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.malibin.morse.databinding.ActivityMypageBinding
import com.malibin.morse.databinding.ItemFollowingBinding
import com.malibin.morse.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageActivity : AppCompatActivity() {

    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = FollowsAdapter()

        binding.lifecycleOwner = this
        binding.viewModel = myPageViewModel
        binding.listFollows.adapter = adapter
        binding.buttonLogout.setOnClickListener { logout() }

        myPageViewModel.account.observe(this) {
            adapter.submitList(it.followings)
        }
        myPageViewModel.loadAccount()
    }

    private fun logout() {
        myPageViewModel.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private inner class FollowsAdapter : ListAdapter<String, ViewHolder>(ItemDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(ItemFollowingBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private inner class ViewHolder(
        private val binding: ItemFollowingBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(nickname: String) {
            binding.nickName = nickname
        }
    }

    private inner class ItemDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
