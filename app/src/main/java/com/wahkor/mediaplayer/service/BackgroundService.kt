package com.wahkor.mediaplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Message
import android.provider.Settings
import android.widget.Toast
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.model.Song

class BackgroundService: Service() {
    companion object{
        private lateinit var db:PlayListDB
        private lateinit var statusDb: PlaylistStatusDb
        private lateinit var mediaPlayer: MediaPlayer
        private var songQuery=ArrayList<Song>()
        private var tableName:String?=null
        private lateinit var currentSong:Song
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        db= PlayListDB(this)
        statusDb= PlaylistStatusDb(this)
        tableName= statusDb.getTableName
        if (tableName != null) {
            songQuery=db.getData(tableName!!)
            toast(this,"load from status")
        }else{
            toast(this,"load from all song")
            tableName="playlist_default"
            songQuery=db.getData("allSong")
            db.createTable(tableName!!, songQuery)
            statusDb.setTableName(tableName!!)
        }
        mediaPlayer=MediaPlayer()
        var position=0
        for (i in 0 until songQuery.size){
            if (songQuery[i].is_playing){
                position=i
            }
            songQuery[i].is_playing=false
        }
        if (songQuery.size>0){
            songQuery[position].is_playing=true
            setupPlayer(songQuery[position])
        }else{
            mediaPlayer=MediaPlayer.create(this,Settings.System.DEFAULT_RINGTONE_URI)
        }
        mediaPlayer.setVolume(0.5f,0.5f)
        mediaPlayer.setOnCompletionListener { nextPlay() }
        return START_STICKY
    }
    fun toast(context: Context,message: String)=Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    fun isPlaying()= mediaPlayer.isPlaying
    fun start()=mediaPlayer.start()
    fun stop()=mediaPlayer.stop()
    override fun onDestroy() {
        super.onDestroy()
    }

    fun prevPlay(){
        var position=0
        for (i in 0 until songQuery.size){
            if (songQuery[i].is_playing){
                songQuery[i].is_playing=false
                position=if (i==0) songQuery.size-1 else i-1
            }
        }
        songQuery[position].is_playing=true
        setupPlayer(songQuery[position])
        mediaPlayer.start()
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
        setupPlayer(songQuery[position])
        mediaPlayer.start()
    }
    private fun setupPlayer(song: Song){
        mediaPlayer.reset()
        mediaPlayer.setDataSource(song.data)
        mediaPlayer.prepare()
        db.setData(tableName!!, songQuery)
        currentSong=song

    }

    fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    fun pause() =mediaPlayer.pause()
    val title:String get() = currentSong.title
    val duration:Int get() = mediaPlayer.duration
    val currentPosition:Int get() = mediaPlayer.currentPosition
}