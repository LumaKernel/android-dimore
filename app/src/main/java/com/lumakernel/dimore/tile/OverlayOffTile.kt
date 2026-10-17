package com.lumakernel.dimore.tile

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.lumakernel.dimore.OverlayService

class OverlayOffTile : TileService() {
    override fun onStartListening() {
        val prefs = getSharedPreferences("dimore_overlay_state", MODE_PRIVATE)
        val active = prefs.getBoolean("overlay_active", false)
        qsTile?.apply {
            state = if (active) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            updateTile()
        }
    }

    override fun onClick() {
        val intent =
            Intent(this, OverlayService::class.java).apply {
                action = OverlayService.ACTION_STOP
            }
        startService(intent)
        qsTile?.apply {
            state = Tile.STATE_INACTIVE
            updateTile()
        }
    }
}
