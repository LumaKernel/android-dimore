package com.lumakernel.dimore

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val overlayPermissionGranted = mutableStateOf(false)
    private val overlayActive = mutableStateOf(false)
    private val brightness = mutableFloatStateOf(OverlayService.DEFAULT_BRIGHTNESS)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        overlayPermissionGranted.value = Settings.canDrawOverlays(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overlayPermissionGranted.value = Settings.canDrawOverlays(this)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        hasOverlayPermission = overlayPermissionGranted.value,
                        isOverlayActive = overlayActive.value,
                        brightness = brightness.floatValue,
                        onRequestPermission = ::requestOverlayPermission,
                        onToggleOverlay = ::toggleOverlay,
                        onBrightnessChange = ::updateBrightness,
                        onOpenSettings = ::openSettings,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        overlayPermissionGranted.value = Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"),
        )
        overlayPermissionLauncher.launch(intent)
    }

    private fun toggleOverlay() {
        if (overlayActive.value) {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = OverlayService.ACTION_STOP
            startService(intent)
            overlayActive.value = false
        } else {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = OverlayService.ACTION_START
            intent.putExtra(OverlayService.EXTRA_BRIGHTNESS, brightness.floatValue)
            startForegroundService(intent)
            overlayActive.value = true
        }
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun updateBrightness(value: Float) {
        brightness.floatValue = value
        if (overlayActive.value) {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = OverlayService.ACTION_SET_BRIGHTNESS
            intent.putExtra(OverlayService.EXTRA_BRIGHTNESS, value)
            startService(intent)
        }
    }
}

@Composable
private fun MainScreen(
    hasOverlayPermission: Boolean,
    isOverlayActive: Boolean,
    brightness: Float,
    onRequestPermission: () -> Unit,
    onToggleOverlay: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "dimore",
            style = MaterialTheme.typography.headlineLarge,
        )

        if (!hasOverlayPermission) {
            Text(
                text = "オーバーレイ権限が必要です",
                modifier = Modifier.padding(top = 24.dp),
            )
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("権限を許可する")
            }
        } else {
            Button(
                onClick = onToggleOverlay,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text(if (isOverlayActive) "オーバーレイ OFF" else "オーバーレイ ON")
            }

            Text(
                text = "明度: ${(brightness * 100).toInt()}%",
                modifier = Modifier.padding(top = 16.dp),
            )
            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                valueRange = 0.0f..OverlayService.MAX_BRIGHTNESS,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )

            Button(
                onClick = onOpenSettings,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text("設定")
            }
        }
    }
}
