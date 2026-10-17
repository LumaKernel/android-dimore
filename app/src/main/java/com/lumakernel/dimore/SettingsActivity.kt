package com.lumakernel.dimore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {

    private val autoEnableOnLaunch = mutableStateOf(false)
    private val offNotificationMode = mutableStateOf("always")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettings()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        autoEnableOnLaunch = autoEnableOnLaunch.value,
                        onAutoEnableChange = { value ->
                            autoEnableOnLaunch.value = value
                            saveSetting("auto_enable_on_launch", value)
                        },
                        offNotificationMode = offNotificationMode.value,
                        onOffNotificationModeChange = { mode ->
                            offNotificationMode.value = mode
                            saveSetting("off_notification_mode", mode)
                        },
                    )
                }
            }
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
        autoEnableOnLaunch.value = prefs.getBoolean("auto_enable_on_launch", false)
        offNotificationMode.value = prefs.getString("off_notification_mode", "always") ?: "always"
    }

    private fun saveSetting(key: String, value: Boolean) {
        getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun saveSetting(key: String, value: String) {
        getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }
}

@Composable
private fun SettingsScreen(
    autoEnableOnLaunch: Boolean,
    onAutoEnableChange: (Boolean) -> Unit,
    offNotificationMode: String,
    onOffNotificationModeChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "設定",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "起動時に自動でオンにする",
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = autoEnableOnLaunch,
                onCheckedChange = onAutoEnableChange,
            )
        }

        Text(
            text = "オフ時の通知",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        )

        NotificationModeOption(
            label = "常に表示",
            selected = offNotificationMode == "always",
            onClick = { onOffNotificationModeChange("always") },
        )
        NotificationModeOption(
            label = "即時非表示",
            selected = offNotificationMode == "dismiss_0s",
            onClick = { onOffNotificationModeChange("dismiss_0s") },
        )
        NotificationModeOption(
            label = "10秒後に非表示",
            selected = offNotificationMode == "dismiss_10s",
            onClick = { onOffNotificationModeChange("dismiss_10s") },
        )
    }
}

@Composable
private fun NotificationModeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
