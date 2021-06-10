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
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"test receive",Toast.LENGTH_SHORT).show()
        Log.e("test","test")
        if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {

        }

    }
    fun handleIntent(mediaSession:MediaSession?, intent:Intent?){

    }


}