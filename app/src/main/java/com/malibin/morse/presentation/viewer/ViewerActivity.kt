package com.malibin.morse.presentation.viewer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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
import com.malibin.morse.presentation.utils.printLog
import com.malibin.morse.presentation.utils.showToast
import com.malibin.morse.rtc.WebRtcClientEvents
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.RendererCommon
import org.webrtc.VideoSink

@AndroidEntryPoint
class ViewerActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private var landscapeBinding: ActivityViewerLandscapeBinding? = null
    private var portraitBinding: ActivityViewerPortraitBinding? = null
    private var chatMessagesAdapter: ChatMessagesAdapter? = null

    private val viewerViewModel: ViewerViewModel by viewModels()

    private val room: Room by lazy { intent.getSerializableExtra(KEY_ROOM) as Room }

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

        viewerViewModel.connect(room.id)
        viewerViewModel.rtcState.observe(this) {
            if (it == WebRtcClientEvents.State.CONNECTED) attachRenderer()
            if (it == WebRtcClientEvents.State.ALREADY_CLOSED) onBroadCastAlreadyClosed()
            if (it == WebRtcClientEvents.State.FINISH_BROADCAST) onBroadCastClosed()
        }
        viewerViewModel.chatMessages.observe(this) {
            chatMessagesAdapter.submitList(it) { scrollBottomOfChatting() }
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
        chatMessagesAdapter?.chatMessageColor = ChatMessage.Color.WHITE
        binding.listChatting.adapter = chatMessagesAdapter
        binding.listChatting.itemAnimator = null
        binding.buttonSend.setOnClickListener { sendChatMessage() }
        binding.textInput.setOnEditorActionListener(this)
    }

    private fun initView(binding: ActivityViewerPortraitBinding) {
        portraitBinding = binding
        binding.windowViewerSurface.apply {
            init(viewerViewModel.eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT)
            setEnableHardwareScaler(true)
            setMirror(true)
        }
        chatMessagesAdapter?.chatMessageColor = ChatMessage.Color.BLACK
        binding.listChatting.adapter = chatMessagesAdapter
        binding.listChatting.itemAnimator = null
        binding.buttonSend.setOnClickListener { sendChatMessage() }
        binding.textInput.setOnEditorActionListener(this)
    }

    private fun sendChatMessage() {
        val inputView = portraitBinding?.textInput
            ?: landscapeBinding?.textInput
            ?: error("cannot find textInput View")
        val message = inputView.text.toString()
        if (message.isBlank()) return
        viewerViewModel.sendChatMessage(message, room.id)
        inputView.setText("")
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendChatMessage()
            return true
        }
        return false
    }

    private fun scrollBottomOfChatting() {
        val listChatting = portraitBinding?.listChatting
            ?: landscapeBinding?.listChatting
            ?: error("cannot find recyclerView")
        if (!listChatting.canScrollVertically(SCROLL_DOWN)) {
            listChatting.scrollToPosition(chatMessagesAdapter?.getLastPosition() ?: return)
        }
    }

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
        val intent = Intent().apply { putExtra(KEY_ROOM, room) }
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

        private const val SCROLL_DOWN = 1
    }
}
