package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.widget.Toast
import com.wahkor.mediaplayer.MusicPlayer
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.service.BackgroundMediaService

class AudioReceiver:BroadcastReceiver() {
    companion object{
        private lateinit var db:PlayListDB
        private lateinit var statusDb: PlaylistStatusDb
        private val bgSong=BackgroundMediaService()
        private lateinit var mp:MusicPlayer
        private var singleClick=false
        private var doubleClick=false
        private var lastClick=0L
        private var currentClick=0L
        private const val delayClick=300L
        private var isPlaying=false

    }
    override fun onReceive(context: Context?, intent: Intent?) {
        db= PlayListDB(context!!)
        statusDb= PlaylistStatusDb(context)
        val tableName= statusDb.getTableName
        var playlist=db.getData(tableName!!)
        mp=MusicPlayer()
        mp.create(playlist[3],context!!)
        if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            lastClick=currentClick
            currentClick=System.currentTimeMillis()
            if (currentClick-delayClick<lastClick){
                doubleClick=true
                singleClick=false
            }else{
                doubleClick=false
                singleClick=true
                isPlaying = if (isPlaying) {
                    mp.action("pause", context)
                    false
                } else {
                    mp.action("play", context)
                    true

                }

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