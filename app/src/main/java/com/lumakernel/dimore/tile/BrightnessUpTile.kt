package com.lumakernel.dimore.tile

import android.content.Intent
import android.service.quicksettings.TileService
import com.lumakernel.dimore.OverlayService

class BrightnessUpTile : TileService() {
    override fun onClick() {
        val intent =
            Intent(this, OverlayService::class.java).apply {
                action = OverlayService.ACTION_BRIGHTNESS_UP
            }
        startService(intent)
    }
}
