package com.wahkor.mediaplayer.receiver

import BackgroundMediaService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.util.Log
import android.widget.Toast
import com.wahkor.mediaplayer.MusicPlayer

class AudioReceiver:BroadcastReceiver() {
    companion object{
        private lateinit var mp:MusicPlayer
        private var singleClick=false
        private var doubleClick=false
        private var lastClick=0L
        private var currentClick=0L
        private const val delayClick=300L

    }
    override fun onReceive(context: Context?, intent: Intent?) {
        mp=MusicPlayer()
        if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            lastClick=currentClick
            currentClick=System.currentTimeMillis()
            if (currentClick-delayClick<lastClick){
                doubleClick=true
                singleClick=false
            }else{
                doubleClick=false
                singleClick=true

            }
        }

    }
    fun handleIntent(mediaSession:MediaSession?, intent:Intent?){

    }
    val singleHook:Boolean get() = singleClick
    val doubleHook:Boolean get() = doubleClick
    fun resetHook(){
        singleClick=false
        doubleClick=false
    }


}