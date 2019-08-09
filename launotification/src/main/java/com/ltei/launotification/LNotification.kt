package com.ltei.launotification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class LNotification(
        val context: Context,
        val notificationId: Int,
        val contentTitle: String,
        val contentText: String,
        val ticker: String,
        val smallIconResId: Int,
        val backgroundColor: Int = Color.WHITE
) {

    companion object {

        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "channel_name"
        private const val CHANNEL_DESCRIPTION = "channel_description"

        fun getManager(context: Context): NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fun getCompatManager(context: Context): NotificationManagerCompat = NotificationManagerCompat.from(context)

        private fun getImportance(): Int = if (Build.VERSION.SDK_INT >= 24) {
            NotificationManager.IMPORTANCE_MAX
        } else {
            Notification.PRIORITY_MAX
        }

        private fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, getImportance())
                channel.description = CHANNEL_DESCRIPTION
                // Register the channel with the system; you can't change the importance or other notification behaviors after this
                getManager(context).createNotificationChannel(channel)
            }
        }

    }

    fun show(autoCancel: Boolean = true, ongoing: Boolean = false, onTapIntent: PendingIntent? = null) {
        createNotificationChannel(context)
        val notification = buildNotification(autoCancel, ongoing, onTapIntent)
        getCompatManager(context).notify(notificationId, notification)
    }

    fun hide() {
        getManager(context).cancel(notificationId)
    }

    private fun buildNotification(
            autoCancel: Boolean,
            ongoing: Boolean,
            onTapIntent: PendingIntent? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)

        builder.setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(contentTitle)                           // required
                .setColor(backgroundColor)
                .setSmallIcon(smallIconResId) // required
                .setContentText(contentText)  // required
                .setAutoCancel(autoCancel)
                .setTicker(ticker)
                .setOngoing(ongoing)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .priority = getImportance()
        if (onTapIntent != null)
            builder.setContentIntent(onTapIntent)

        return builder.build()
    }

}