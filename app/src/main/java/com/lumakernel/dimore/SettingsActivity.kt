package com.lumakernel.dimore

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

class SettingsActivity : ComponentActivity() {
    private val autoEnableOnBoot = mutableStateOf(false)
    private val offNotificationMode = mutableStateOf("always")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSettings()

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        autoEnableOnBoot = autoEnableOnBoot.value,
                        onAutoEnableChange = { value ->
                            autoEnableOnBoot.value = value
                            saveSetting("auto_enable_on_boot", value)
                        },
                        offNotificationMode = offNotificationMode.value,
                        onOffNotificationModeChange = { mode ->
                            offNotificationMode.value = mode
                            saveSetting("off_notification_mode", mode)
                        },
                        onOpenUrl = { url ->
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        },
                        onOpenLicenses = {
                            startActivity(Intent(this, LicensesActivity::class.java))
                        },
                    )
                }
            }
        }
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
        autoEnableOnBoot.value = prefs.getBoolean("auto_enable_on_boot", false)
        offNotificationMode.value = prefs.getString("off_notification_mode", "always") ?: "always"
    }

    private fun saveSetting(
        key: String,
        value: Boolean,
    ) {
        getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun saveSetting(
        key: String,
        value: String,
    ) {
        getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
            .edit()
            .putString(key, value)
            .apply()
    }
}

@Composable
private fun SettingsScreen(
    autoEnableOnBoot: Boolean,
    onAutoEnableChange: (Boolean) -> Unit,
    offNotificationMode: String,
    onOffNotificationModeChange: (String) -> Unit,
    onOpenUrl: (String) -> Unit,
    onOpenLicenses: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "設定",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "端末起動時にサービスを自動起動",
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = autoEnableOnBoot,
                onCheckedChange = onAutoEnableChange,
            )
        }

        if (autoEnableOnBoot && offNotificationMode != "always") {
            Text(
                text = "オフ時の通知が非表示設定のため、自動起動しても通知が表示されません",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
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

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(
            text = "アプリ情報",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        AboutItem(label = "バージョン", value = BuildConfig.VERSION_NAME)

        AboutItem(label = "ライセンス", value = "UNLICENSE (パブリックドメイン)")

        if (BuildConfig.REPOSITORY_URL.isNotEmpty()) {
            Text(
                text = "リポジトリ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = BuildConfig.REPOSITORY_URL,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier =
                    Modifier
                        .clickable { onOpenUrl(BuildConfig.REPOSITORY_URL) }
                        .padding(vertical = 4.dp),
            )
        }

        Text(
            text = "オープンソースライセンス",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier =
                Modifier
                    .clickable { onOpenLicenses() }
                    .padding(top = 8.dp, bottom = 4.dp),
        )
    }
}

@Composable
private fun AboutItem(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
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
        modifier =
            Modifier
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
