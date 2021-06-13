package com.wahkor.mediaplayer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.service.AudioService

class MusicNotification {
    private val channelId="music"
    private val playBTN="Play"
    private val prevBTN="Prev"
    private val nextBTN="next"
    private val mp=AudioService()
    private lateinit var notification:Notification

    fun build(context: Context){
        val notificationCompat=NotificationManagerCompat.from(context)
        notification=NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(mp.title)
            .setContentText(mp.artist)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationCompat.notify(1,notification)
    }
}

class MusicNotificationChannel{
    private var notificationManager: NotificationManager?=null
    private val id="music"
    @RequiresApi(Build.VERSION_CODES.O)
    fun build(context: Context){
        val channel= NotificationChannel(id,"MediaPlayer",NotificationManager.IMPORTANCE_LOW)
        notificationManager= getSystemService(context,NotificationManager::class.java)
        notificationManager?.let { notificationManager ->
            notificationManager.createNotificationChannel(channel)

        }
    }
}