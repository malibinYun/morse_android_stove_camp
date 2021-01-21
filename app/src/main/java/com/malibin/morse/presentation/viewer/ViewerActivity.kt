package com.malibin.morse.presentation.viewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.malibin.morse.databinding.ActivityBroadCastBinding
import com.malibin.morse.databinding.ActivityViewerBinding
import com.malibin.morse.presentation.broadcast.BroadCastViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.VideoSink

@AndroidEntryPoint
class ViewerActivity : AppCompatActivity() {

    private val rootEgl = EglBase.create()
    private var binding: ActivityViewerBinding? = null
    private val viewerViewModel: ViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityViewerBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)

        initView(binding)
        startWatchBroadcast(binding.windowViewerSurface)
    }

    private fun initView(binding: ActivityViewerBinding) {
        binding.windowViewerSurface.apply {
            init(rootEgl.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
            setMirror(true)
        }
    }

    private fun startWatchBroadcast(renderer: VideoSink) {
//        viewerViewModel.connectPeer(rootEgl, renderer)
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewerViewModel.disconnect()
        binding?.windowViewerSurface?.release()
        binding = null
        rootEgl.release()
    }
}
