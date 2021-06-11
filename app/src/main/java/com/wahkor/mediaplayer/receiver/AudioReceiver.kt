package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSession
import android.util.Log
import android.widget.Toast
import com.wahkor.mediaplayer.MusicPlayer
import com.wahkor.mediaplayer.service.BackgroundService

class AudioReceiver:BroadcastReceiver() {
    companion object{
        private lateinit var mp:BackgroundService
        private var lastClick=0L
        private var currentClick=0L
        private const val delayClick=300L

    }
    override fun onReceive(context: Context?, intent: Intent?) {
        mp= BackgroundService()
        if(currentClick+10>System.currentTimeMillis()){
        }else{
            if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                lastClick=currentClick
                currentClick=System.currentTimeMillis()
                if (currentClick<delayClick+lastClick){
                    mp.nextPlay()
                }else{
                    if (mp.isPlaying()){
                        mp.stop()
                    }else{
                        mp.start()
                    }

                }
            }

        }

    }
    fun toast(context: Context?,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }


}