package com.malibin.morse.presentation.broadcast

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.malibin.morse.R
import com.malibin.morse.databinding.ActivityBroadCastBinding
import com.malibin.morse.presentation.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import com.malibin.morse.localSdp
import com.malibin.morse.rtc.WebRtcClientEvents
import org.webrtc.EglBase
import org.webrtc.RendererCommon
import org.webrtc.VideoSink

@AndroidEntryPoint
class BroadCastActivity : AppCompatActivity() {
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
    }

    private fun hasNoCamera(): Boolean =
        !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    private fun initView(binding: ActivityBroadCastBinding) {
        binding.windowBroadcastSurface.apply {
            init(broadCastViewModel.eglBase.eglBaseContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true)
            setMirror(true)
        }
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
        broadCastViewModel.connect()
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

    private fun requireBinding() = binding ?: error("activity not inflated yet")

    override fun onDestroy() {
        super.onDestroy()
        detachCurrentRenderer()
        broadCastViewModel.disconnect()
        binding?.windowBroadcastSurface?.release()
        binding = null
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1000
        private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
        private const val MIC_PERMISSION = android.Manifest.permission.RECORD_AUDIO
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED

        const val KEY_ROOM_TITLE = "KEY_ROOM_TITLE"
        const val KEY_ROOM_CONTENT = "KEY_ROOM_CONTENT"
    }
}
