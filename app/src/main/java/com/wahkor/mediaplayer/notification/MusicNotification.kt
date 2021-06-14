package com.wahkor.mediaplayer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.service.AudioService
/*
class MusicNotification: Service() {
    private lateinit var runnable: Runnable
    private val handler=Handler(Looper.getMainLooper())
    private var notificationManager: NotificationManager?=null
    private val channelId="music player"
    private val notificationId=1156
    private val playBTN="Play"
    private val prevBTN="Prev"
    private val nextBTN="next"
    private val mp=AudioService()
    private lateinit var notification:Notification
    private fun showNotification(){
        val notificationCompat=NotificationManagerCompat.from(this)
        notification=NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(mp.title)
            .setContentText(mp.artist)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationCompat.notify(notificationId,notification)
    }
    private fun createChannel(){
        val channel= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId,"MediaPlayer",NotificationManager.IMPORTANCE_LOW)
        } else {
            null
        }
        channel?.let {
            notificationManager= getSystemService(this,NotificationManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager?.createNotificationChannel(channel)
            }
        }

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createChannel()
        showNotification()
        runnable= Runnable {
            if(mp.isPlaying()){
                showNotification()
                handler.postDelayed(runnable,3000)
            }
            handler.postDelayed(runnable,3000)

        }
        return START_STICKY
        //return super.onStartCommand(intent, flags, startId)
    }
*/
}