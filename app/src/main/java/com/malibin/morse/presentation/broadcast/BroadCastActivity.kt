package com.malibin.morse.presentation.broadcast

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.malibin.morse.R
import com.malibin.morse.databinding.ActivityBroadCastBinding
import com.malibin.morse.presentation.utils.showToast

class BroadCastActivity : AppCompatActivity() {

    private var binding: ActivityBroadCastBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityBroadCastBinding.inflate(layoutInflater)
        this.binding = binding
        setContentView(binding.root)

        if (isPermissionsGranted()) showToast("이미 권한 부여함")
        else askPermissions()
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
            if(isAllPermissionGranted) showToast("권한부여됨")
            else showPermissionRejected()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun requireBinding() = binding ?: error("activity not inflated yet")

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1000
        private const val CAMERA_PERMISSION = android.Manifest.permission.CAMERA
        private const val MIC_PERMISSION = android.Manifest.permission.RECORD_AUDIO
        private const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    }
}
