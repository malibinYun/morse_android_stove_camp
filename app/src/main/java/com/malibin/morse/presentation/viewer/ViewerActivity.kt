package com.malibin.morse.presentation.viewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.malibin.morse.R
import com.malibin.morse.data.entity.ChatMessage
import com.malibin.morse.data.entity.Room
import com.malibin.morse.databinding.ActivityViewerLandscapeBinding
import com.malibin.morse.databinding.ActivityViewerPortraitBinding
import com.malibin.morse.presentation.chatting.ChatMessagesAdapter
import com.malibin.morse.presentation.chatting.RandomColorGenerator
import com.malibin.morse.presentation.utils.hideStatusBar
import com.malibin.morse.presentation.utils.isPortraitOrientation
import com.malibin.morse.presentation.utils.showToast
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.RendererCommon
import org.webrtc.VideoSink

@AndroidEntryPoint
class ViewerActivity : AppCompatActivity() {

    private var landscapeBinding: ActivityViewerLandscapeBinding? = null
    private var portraitBinding: ActivityViewerPortraitBinding? = null
    private var chatMessagesAdapter: ChatMessagesAdapter? = null

    private val viewerViewModel: ViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val chatMessagesAdapter = ChatMessagesAdapter(RandomColorGenerator())
        this.chatMessagesAdapter = chatMessagesAdapter

        val binding = if (isPortraitOrientation()) {
            ActivityViewerPortraitBinding.inflate(layoutInflater).apply { initView(this) }
        } else {
            ActivityViewerLandscapeBinding.inflate(layoutInflater).apply { initView(this) }
        }
        binding.lifecycleOwner = this
        setContentView(binding.root)

        viewerViewModel.connect()
        viewerViewModel.rtcState.observe(this) {
            if (it == WebRtcClientEvents.State.CONNECTED) attachRenderer()
            if (it == WebRtcClientEvents.State.ALREADY_CLOSED) onBroadCastAlreadyClosed()
            if (it == WebRtcClientEvents.State.FINISH_BROADCAST) onBroadCastClosed()
        }
    }

    private fun initView(binding: ActivityViewerLandscapeBinding) {
        landscapeBinding = binding
        hideStatusBar()
        binding.windowViewerSurface.apply {
            init(viewerViewModel.eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
        }
    }

    private fun initView(binding: ActivityViewerPortraitBinding) {
        portraitBinding = binding
        binding.windowViewerSurface.apply {
            init(viewerViewModel.eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
            setMirror(true)
        }
        binding.listChatting.adapter = chatMessagesAdapter
        binding.buttonSend.setOnClickListener {
            chatMessagesAdapter?.appendChatMessage(
                ChatMessage(binding.textInput.text.toString(), "말리빈")
            )
        }
    }

    private fun getRoom(): Room = intent.getSerializableExtra(KEY_ROOM) as Room

    override fun onResume() {
        super.onResume()
        if (viewerViewModel.isConnected()) attachRenderer()
    }

    private fun attachRenderer() {
        viewerViewModel.attachRenderer(getCurrentRenderer())
    }

    private fun detachCurrentRenderer() {
        viewerViewModel.detachRenderer(getCurrentRenderer())
    }

    private fun getCurrentRenderer(): VideoSink {
        return portraitBinding?.windowViewerSurface
            ?: landscapeBinding?.windowViewerSurface
            ?: error("cannot get video renderer")
    }

    private fun onBroadCastAlreadyClosed() {
        showToast(R.string.broadcast_already_closed)
        val intent = Intent().apply { putExtra(KEY_ROOM, getRoom()) }
        setResult(RESULT_ALREADY_CLOSED, intent)
        finish()
    }

    private fun onBroadCastClosed() {
        showToast(R.string.broadcast_closed)
        finish()
    }

    override fun onStop() {
        super.onStop()
        detachCurrentRenderer()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewerViewModel.rtcState.removeObservers(this)
        landscapeBinding?.windowViewerSurface?.release()
        landscapeBinding = null
        portraitBinding?.windowViewerSurface?.release()
        portraitBinding = null
    }

    companion object {
        const val KEY_ROOM = "KEY_ROOM"
        const val RESULT_ALREADY_CLOSED = 100
        const val REQUEST_CODE = 1000
    }
}
