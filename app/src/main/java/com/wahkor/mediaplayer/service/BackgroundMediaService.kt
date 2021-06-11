package com.wahkor.mediaplayer.service

import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.text.TextUtils
import com.wahkor.mediaplayer.MusicPlayer
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.model.Song


class BackgroundMediaService(): MediaBrowserService(),MediaPlayer.OnCompletionListener {
    private val mediaPlayer=MusicPlayer()
    private lateinit var db:PlayListDB
    private lateinit var statusDb: PlaylistStatusDb
    private var playlist=ArrayList<Song>()
    private var tableName:String?=null
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        if (TextUtils.equals(clientPackageName, packageName)) {
            return BrowserRoot("MediaPlayer", null)
        }
        return null
    }

    override fun onCreate() {
        super.onCreate()
        db= PlayListDB(this)
        statusDb= PlaylistStatusDb(this)
        tableName=statusDb.getTableName
        if(tableName != null){
            playlist=db.getData(tableName!!)
            for(song in playlist){
                if(song.is_playing) mediaPlayer.create(song,this)
            }
        }
    }
    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
    }

    override fun onCompletion(mp: MediaPlayer?) {
        mediaPlayer.create(nextPlay(),this)
        mediaPlayer.action("play",this)
        db.setData(tableName!!,playlist)

    }
    private fun nextPlay():Song{
        var position=0
        for(i in 0 until playlist.size){
            if(playlist[i].is_playing){
                position=i
                playlist[i].is_playing=false
            }
        }
        position=if (position==playlist.size-1) 0 else position+1
        playlist[position].is_playing=true
        return playlist[position]
    }

    fun start() {
        mediaPlayer.action("play",this)
    }

}