package com.lumakernel.dimore

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val prefs = context.getSharedPreferences("dimore_settings", Context.MODE_PRIVATE)
        val autoEnable = prefs.getBoolean("auto_enable_on_boot", false)
        if (!autoEnable) return
        if (!Settings.canDrawOverlays(context)) return

        val serviceIntent =
            Intent(context, OverlayService::class.java).apply {
                action = OverlayService.ACTION_START
            }
        context.startForegroundService(serviceIntent)
    }
}
