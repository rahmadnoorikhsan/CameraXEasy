package com.rts.cameraxeasy

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.rts.cameraxeasy.ui.theme.CameraXEasyTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private val _isCameraPermissionGranted = MutableStateFlow(false)
    private val isCameraPermissionGranted = _isCameraPermissionGranted

    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                _isCameraPermissionGranted.value = true
            } else {
                Toast.makeText(
                    this,
                    "Go to settings and enable camera permission to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            fun handleCameraPermission() {
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        _isCameraPermissionGranted.value = true
                    }

                    else -> {
                        cameraPermissionRequestLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            }

            val permissionGranted = isCameraPermissionGranted.collectAsState().value
            CameraXEasyTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    if (permissionGranted) {
                        CameraPreview()
                    } else {
                        Button (
                            onClick = {
                                handleCameraPermission()
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(text = "Start Preview")
                        }
                    }
                }
            }
        }
    }
}