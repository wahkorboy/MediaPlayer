package com.wahkor.mediaplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song

class BackgroundService: Service(),MediaPlayer.OnCompletionListener {
    companion object{
        private lateinit var db:PlayListDB
        private lateinit var mediaPlayer: MediaPlayer
        private var songQuery=ArrayList<Song>()
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        db= PlayListDB(this)
        songQuery=db.getData("allSong")
        mediaPlayer=MediaPlayer()
        var position=0
        for (i in 0 until songQuery.size){
            if (songQuery[i].is_playing){
                position=i
            }
        }
        mediaPlayer.setDataSource(songQuery[position].data)
        mediaPlayer.prepare()
        mediaPlayer.setVolume(0.5f,0.5f)
        return START_STICKY
    }
    fun isPlaying()= mediaPlayer.isPlaying
    fun start()=mediaPlayer.start()
    fun stop()=mediaPlayer.stop()
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextPlay()
    }
    fun nextPlay(){
        var position=0
        for (i in 0 until songQuery.size){
            if (songQuery[i].is_playing){
                songQuery[i].is_playing=false
                position=if (i== songQuery.size-1) 0 else i+1
            }
        }
        songQuery[position].is_playing=true
        mediaPlayer.reset()
        mediaPlayer.setDataSource(songQuery[position].data)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}