package com.lumakernel.dimore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {
    private val overlayPermissionGranted = mutableStateOf(false)
    private val notificationPermissionGranted = mutableStateOf(false)
    private val shizukuAvailable = mutableStateOf(false)
    private val shizukuPermissionGranted = mutableStateOf(false)
    private val overlayActive = mutableStateOf(false)
    private val brightness = mutableFloatStateOf(OverlayService.DEFAULT_BRIGHTNESS)

    private val shizukuPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { _, grantResult ->
            shizukuPermissionGranted.value =
                grantResult == PackageManager.PERMISSION_GRANTED
        }

    private val overlayPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            overlayPermissionGranted.value = Settings.canDrawOverlays(this)
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            notificationPermissionGranted.value = granted
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overlayPermissionGranted.value = Settings.canDrawOverlays(this)
        notificationPermissionGranted.value = hasNotificationPermission()
        updateShizukuState()
        Shizuku.addRequestPermissionResultListener(shizukuPermissionResultListener)
        requestNotificationPermissionIfNeeded()

        if (savedInstanceState == null) {
            autoEnableIfConfigured()
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(
                        hasOverlayPermission = overlayPermissionGranted.value,
                        hasNotificationPermission = notificationPermissionGranted.value,
                        isOverlayActive = overlayActive.value,
                        brightness = brightness.floatValue,
                        isShizukuAvailable = shizukuAvailable.value,
                        hasShizukuPermission = shizukuPermissionGranted.value,
                        onRequestPermission = ::requestOverlayPermission,
                        onRequestNotificationPermission = ::requestNotificationPermission,
                        onRequestShizukuPermission = ::requestShizukuPermission,
                        onGrantPermissionViaShizuku = ::grantOverlayPermissionViaShizuku,
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
        notificationPermissionGranted.value = hasNotificationPermission()
        updateShizukuState()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionResultListener(shizukuPermissionResultListener)
    }

    private fun updateShizukuState() {
        shizukuAvailable.value = ShizukuHelper.isShizukuAvailable()
        shizukuPermissionGranted.value =
            shizukuAvailable.value && ShizukuHelper.hasShizukuPermission()
    }

    private fun requestShizukuPermission() {
        Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
    }

    private fun hasNotificationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    private fun requestNotificationPermissionIfNeeded() {
        if (!hasNotificationPermission()) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestNotificationPermission() {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun requestOverlayPermission() {
        val intent =
            Intent(
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

    private fun autoEnableIfConfigured() {
        val prefs = getSharedPreferences("dimore_settings", MODE_PRIVATE)
        val autoEnable = prefs.getBoolean("auto_enable_on_launch", false)
        if (autoEnable && Settings.canDrawOverlays(this) && !overlayActive.value) {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = OverlayService.ACTION_START
            intent.putExtra(OverlayService.EXTRA_BRIGHTNESS, brightness.floatValue)
            startForegroundService(intent)
            overlayActive.value = true
        }
    }

    private fun grantOverlayPermissionViaShizuku() {
        Thread {
            val granted = ShizukuHelper.grantOverlayPermission(packageName)
            if (granted) {
                runOnUiThread {
                    overlayPermissionGranted.value = Settings.canDrawOverlays(this)
                }
            }
        }.start()
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

    companion object {
        private const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
    }
}

@Composable
private fun MainScreen(
    hasOverlayPermission: Boolean,
    hasNotificationPermission: Boolean,
    isOverlayActive: Boolean,
    brightness: Float,
    isShizukuAvailable: Boolean,
    hasShizukuPermission: Boolean,
    onRequestPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onRequestShizukuPermission: () -> Unit,
    onGrantPermissionViaShizuku: () -> Unit,
    onToggleOverlay: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AndroidView(
            factory = { context ->
                android.widget.ImageView(context).apply {
                    setImageResource(R.mipmap.ic_launcher)
                    contentDescription = "dimore アイコン"
                }
            },
            modifier = Modifier.size(72.dp),
        )
        Text(
            text = "dimore",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            if (isShizukuAvailable && hasShizukuPermission) {
                Button(
                    onClick = onGrantPermissionViaShizuku,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Shizuku で権限を付与")
                }
            } else if (isShizukuAvailable && !hasShizukuPermission) {
                Button(
                    onClick = onRequestShizukuPermission,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Shizuku 権限を許可する")
                }
            } else {
                Text(
                    text = "Shizuku を導入すると PC なしで権限を付与できます",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        } else {
            Button(
                onClick = onToggleOverlay,
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text(if (isOverlayActive) "停止する" else "暗くする")
            }

            Text(
                text = "明度: ${(brightness * 100).toInt()}%",
                modifier = Modifier.padding(top = 16.dp),
            )
            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                valueRange = 0.0f..OverlayService.MAX_BRIGHTNESS,
                modifier =
                    Modifier
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

        if (!hasNotificationPermission) {
            Text(
                text = "通知権限がありません",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 24.dp),
            )
            Button(
                onClick = onRequestNotificationPermission,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("通知を許可する")
            }
        }
    }
}
