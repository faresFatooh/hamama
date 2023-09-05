package com.p1.uberfares.channel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.p1.uberfares.R

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    var manager: NotificationManager? = null
        get() {
            if (field == null) {
                field = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }
        private set

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lightColor = android.R.color.darker_gray
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification(
        title: String?,
        body: String?,
        intent: PendingIntent?,
        soundUri: Uri?
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(intent)
            .setSmallIcon(R.drawable.icon_car)
            .setStyle(
                Notification.BigTextStyle()
                    .bigText(body).setBigContentTitle(title)
            )
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotificationActions(
        title: String?, body: String?,
        soundUri: Uri?, acceptAction: Notification.Action?,
        cancelAction: Notification.Action?
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri) //.setContentIntent(intent)
            .setSmallIcon(R.drawable.icon_car)
            .addAction(acceptAction)
            .addAction(cancelAction)
            .setStyle(
                Notification.BigTextStyle()
                    .bigText(body).setBigContentTitle(title)
            )
    }

    fun getNotificationOldAPI(
        title: String?, body: String?,  /*PendingIntent intent,*/
        soundUri: Uri?
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri) // .setContentIntent(intent)
            .setSmallIcon(R.drawable.icon_car)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }

    fun getNotificationOldAPIActions(
        title: String?,
        body: String?,  /* PendingIntent intent,*/
        soundUri: Uri?,
        acceptAction: NotificationCompat.Action?,
        cancelAction: NotificationCompat.Action?
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(soundUri) //.setContentIntent(intent)
            .setSmallIcon(R.drawable.icon_car)
            .addAction(acceptAction)
            .addAction(cancelAction)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title))
    }

    companion object {
        private const val CHANNEL_ID = "com.p1.uber"
        private const val CHANNEL_NAME = "Hamama"
    }
}