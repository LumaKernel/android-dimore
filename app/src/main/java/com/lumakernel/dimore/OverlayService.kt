package com.lumakernel.dimore

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class OverlayService : Service() {

    companion object {
        const val ACTION_START = "com.lumakernel.dimore.ACTION_START"
        const val ACTION_STOP = "com.lumakernel.dimore.ACTION_STOP"
        const val ACTION_SET_BRIGHTNESS = "com.lumakernel.dimore.ACTION_SET_BRIGHTNESS"
        const val ACTION_BRIGHTNESS_UP = "com.lumakernel.dimore.ACTION_BRIGHTNESS_UP"
        const val ACTION_BRIGHTNESS_DOWN = "com.lumakernel.dimore.ACTION_BRIGHTNESS_DOWN"
        const val EXTRA_BRIGHTNESS = "brightness"

        private const val NOTIFICATION_ID = 1
        private const val INACTIVE_NOTIFICATION_ID = 2
        private const val CHANNEL_ID = "dimore_overlay"

        const val DEFAULT_BRIGHTNESS = 0.5f
        const val MAX_BRIGHTNESS = 0.9f
        private const val BRIGHTNESS_STEP = 0.1f
    }

    private var overlayView: View? = null
    private var currentBrightness: Float = DEFAULT_BRIGHTNESS
    private val handler = Handler(Looper.getMainLooper())
    private val dismissNotificationRunnable = Runnable {
        val manager = getSystemService(NotificationManager::class.java)
        manager.cancel(INACTIVE_NOTIFICATION_ID)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                handler.removeCallbacks(dismissNotificationRunnable)
                val manager = getSystemService(NotificationManager::class.java)
                manager.cancel(INACTIVE_NOTIFICATION_ID)
                val brightness = intent.getFloatExtra(EXTRA_BRIGHTNESS, DEFAULT_BRIGHTNESS)
                startOverlay(brightness)
            }
            ACTION_STOP -> {
                removeOverlay()
                stopForeground(STOP_FOREGROUND_REMOVE)
                showInactiveNotification()
            }
            ACTION_SET_BRIGHTNESS -> {
                val brightness = intent.getFloatExtra(EXTRA_BRIGHTNESS, currentBrightness)
                setBrightness(brightness)
                updateNotification()
            }
            ACTION_BRIGHTNESS_UP -> {
                setBrightness(currentBrightness + BRIGHTNESS_STEP)
                updateNotification()
            }
            ACTION_BRIGHTNESS_DOWN -> {
                setBrightness(currentBrightness - BRIGHTNESS_STEP)
                updateNotification()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        removeOverlay()
        handler.removeCallbacks(dismissNotificationRunnable)
        super.onDestroy()
    }

    private fun startOverlay(brightness: Float) {
        startForeground(NOTIFICATION_ID, buildActiveNotification(brightness))

        if (overlayView != null) {
            setBrightness(brightness)
            return
        }

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        )
        params.gravity = Gravity.TOP or Gravity.START

        val view = View(this)
        val clamped = brightness.coerceIn(0.0f, MAX_BRIGHTNESS)
        currentBrightness = clamped
        view.setBackgroundColor(brightnessToColor(clamped))

        windowManager.addView(view, params)
        overlayView = view
    }

    private fun removeOverlay() {
        val view = overlayView ?: return
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(view)
        overlayView = null
    }

    private fun setBrightness(brightness: Float) {
        val view = overlayView ?: return
        val clamped = brightness.coerceIn(0.0f, MAX_BRIGHTNESS)
        view.setBackgroundColor(brightnessToColor(clamped))
        currentBrightness = clamped
    }

    private fun brightnessToColor(brightness: Float): Int {
        val alpha = (brightness * 255).toInt().coerceIn(0, 255)
        return Color.argb(alpha, 0, 0, 0)
    }

    private fun updateNotification() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildActiveNotification(currentBrightness))
    }

    private fun showInactiveNotification() {
        val prefs = getSharedPreferences("dimore_settings", MODE_PRIVATE)
        val offNotificationMode = prefs.getString("off_notification_mode", "always") ?: "always"

        val manager = getSystemService(NotificationManager::class.java)

        when (offNotificationMode) {
            "always" -> {
                manager.notify(INACTIVE_NOTIFICATION_ID, buildInactiveNotification())
                stopSelf()
            }
            "dismiss_0s" -> {
                // 即時非表示: 通知を出さない
                stopSelf()
            }
            "dismiss_10s" -> {
                manager.notify(INACTIVE_NOTIFICATION_ID, buildInactiveNotification())
                handler.postDelayed(dismissNotificationRunnable, 10_000L)
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "dimore オーバーレイ",
            NotificationManager.IMPORTANCE_LOW,
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildActiveNotification(brightness: Float): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )

        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, OverlayService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_IMMUTABLE,
        )

        val brightnessUpIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, OverlayService::class.java).apply { action = ACTION_BRIGHTNESS_UP },
            PendingIntent.FLAG_IMMUTABLE,
        )

        val brightnessDownIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, OverlayService::class.java).apply { action = ACTION_BRIGHTNESS_DOWN },
            PendingIntent.FLAG_IMMUTABLE,
        )

        val brightnessPercent = (brightness * 100).toInt()

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("dimore")
            .setContentText("明度: ${brightnessPercent}%")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(Notification.Action.Builder(null, "OFF", stopIntent).build())
            .addAction(Notification.Action.Builder(null, "暗く", brightnessUpIntent).build())
            .addAction(Notification.Action.Builder(null, "明るく", brightnessDownIntent).build())
            .build()
    }

    private fun buildInactiveNotification(): Notification {
        val startIntent = PendingIntent.getService(
            this,
            4,
            Intent(this, OverlayService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_BRIGHTNESS, currentBrightness)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("dimore")
            .setContentText("オーバーレイ停止中")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(contentIntent)
            .addAction(Notification.Action.Builder(null, "ON", startIntent).build())
            .build()
    }
}
