package com.malibin.morse.presentation.broadcast

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.malibin.morse.R
import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.databinding.ActivityBroadCastBinding
import com.malibin.morse.presentation.chatting.ChatMessagesAdapter
import com.malibin.morse.presentation.chatting.RandomColorGenerator
import com.malibin.morse.presentation.utils.hideStatusBar
import com.malibin.morse.presentation.utils.showToast
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.RendererCommon
import org.webrtc.VideoSink

@AndroidEntryPoint
class BroadCastActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private var binding: ActivityBroadCastBinding? = null
    private val broadCastViewModel: BroadCastViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (hasNoCamera()) {
            showToast(R.string.cannot_broadcast_no_camera)
            finish()
            return
        }
        val binding = ActivityBroadCastBinding.inflate(layoutInflater).also { this.binding = it }
        setContentView(binding.root)
        initView(binding)

        if (isPermissionsGranted()) startBroadCast()
        else askPermissions()

        broadCastViewModel.rtcState.observe(this) {
            if (it == WebRtcClientEvents.State.CONNECTED) attachRenderer()
        }

        val chatMessagesAdapter = ChatMessagesAdapter(RandomColorGenerator())
        chatMessagesAdapter.chatMessageColor = ChatMessage.Color.WHITE

        binding.listChatting.adapter = chatMessagesAdapter
        binding.buttonSend.setOnClickListener { sendChatMessage() }
        broadCastViewModel.chatMessages.observe(this) {
            chatMessagesAdapter.submitList(it) { scrollBottomOfChatting() }
        }
    }

    private fun hasNoCamera(): Boolean =
        !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun initView(binding: ActivityBroadCastBinding) {
        hideStatusBar()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.viewModel = broadCastViewModel
        binding.lifecycleOwner = this
        binding.windowBroadcastSurface.apply {
            init(broadCastViewModel.eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
        }
        binding.textInput.setOnEditorActionListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            val isAllPermissionGranted =
                grantResults.isNotEmpty() && grantResults.all { it == PERMISSION_GRANTED }
            if (isAllPermissionGranted) startBroadCast()
            else showPermissionRejected()
        }
    }

    private fun startBroadCast() {
        broadCastViewModel.createBroadcastRoom(getRoomTitle(), getRoomContent())
    }

    private fun showPermissionRejected() {
        showToast(R.string.permission_rejected)
        finish()
    }

    private fun askPermissions() {
        val permissions = arrayOf(CAMERA_PERMISSION, MIC_PERMISSION)
        requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
    }

    private fun isPermissionsGranted(): Boolean {
        val isCameraGranted = ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)
        val isMicGranted = ActivityCompat.checkSelfPermission(this, MIC_PERMISSION)
        return isCameraGranted == PERMISSION_GRANTED && isMicGranted == PERMISSION_GRANTED
    }

    private fun attachRenderer() {
        broadCastViewModel.attachRenderer(getCurrentRenderer())
    }

    private fun detachCurrentRenderer() {
        broadCastViewModel.detachRenderer(getCurrentRenderer())
    }

    private fun getCurrentRenderer(): VideoSink {
        return requireBinding().windowBroadcastSurface
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendChatMessage()
            return true
        }
        return false
    }

    private fun sendChatMessage() {
        val inputView = requireBinding().textInput
        val message = inputView.text.toString()
        if (message.isBlank()) return
        broadCastViewModel.sendChatMessage(message)
        inputView.setText("")
    }

    private fun scrollBottomOfChatting() {
        val listChatting = requireBinding().listChatting
        if (!listChatting.canScrollVertically(SCROLL_DOWN)) {
            val adapter = listChatting.adapter as? ChatMessagesAdapter ?: return
            listChatting.scrollToPosition(adapter.getLastPosition())
        }
    }

    private fun getRoomTitle(): String = intent.getStringExtra(KEY_ROOM_TITLE)
        ?: error("room title cannot be null")

    private fun getRoomContent(): String = intent.getStringExtra(KEY_ROOM_CONTENT)
        ?: error("room content cannot be null")

    private fun requireBinding() = binding ?: error("activity not inflated yet")

    override fun onDestroy() {
        super.onDestroy()

        detachCurrentRenderer()
        broadCastViewModel.disconnect()
        binding?.windowBroadcastSurface?.release()
        binding = null
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 2000
        private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
        private const val MIC_PERMISSION = android.Manifest.permission.RECORD_AUDIO
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
        private const val SCROLL_DOWN = 1

        const val KEY_ROOM_TITLE = "KEY_ROOM_TITLE"
        const val KEY_ROOM_CONTENT = "KEY_ROOM_CONTENT"
    }
}
