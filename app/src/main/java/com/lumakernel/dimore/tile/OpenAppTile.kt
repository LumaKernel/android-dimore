package com.lumakernel.dimore.tile

import android.content.Intent
import android.service.quicksettings.TileService
import com.lumakernel.dimore.MainActivity

class OpenAppTile : TileService() {
    override fun onClick() {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        startActivityAndCollapse(intent)
    }
}
