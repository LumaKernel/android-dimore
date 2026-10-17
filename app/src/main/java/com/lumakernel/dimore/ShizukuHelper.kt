package com.lumakernel.dimore

import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku

object ShizukuHelper {
    fun isShizukuAvailable(): Boolean =
        try {
            Shizuku.pingBinder()
        } catch (_: Exception) {
            false
        }

    fun hasShizukuPermission(): Boolean =
        try {
            Shizuku.checkSelfPermission() == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (_: Exception) {
            false
        }

    fun grantOverlayPermission(packageName: String): Boolean =
        try {
            val binder = Shizuku.getBinder()
            val service = IShizukuService.Stub.asInterface(binder)
            val remoteProcess =
                service.newProcess(
                    arrayOf("appops", "set", packageName, "SYSTEM_ALERT_WINDOW", "allow"),
                    null,
                    null,
                )
            remoteProcess.waitFor() == 0
        } catch (_: Exception) {
            false
        }
}
