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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val overlayPermissionGranted = mutableStateOf(false)

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
                        onRequestPermission = ::requestOverlayPermission,
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
}

@Composable
private fun MainScreen(
    hasOverlayPermission: Boolean,
    onRequestPermission: () -> Unit,
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
            Text(
                text = "オーバーレイ権限: 許可済み",
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}
