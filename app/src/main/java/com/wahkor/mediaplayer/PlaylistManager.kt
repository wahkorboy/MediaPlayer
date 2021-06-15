package com.wahkor.mediaplayer

import android.content.Context
import android.widget.Toast
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.model.Song

class PlaylistManager(val context: Context) {
    private val db=PlayListDB(context)
    private val statusDb=PlaylistStatusDb(context)
    private var playlist=ArrayList<Song>()
    private var tableName:String=""
    fun build(){
        val currentTable=statusDb.getTableName
        currentTable?.let {
            tableName=currentTable
            playlist=db.getData(tableName) }?:run{
                tableName="allSong"
            playlist=db.getData(tableName)
            tableName="playlist_default"
            db.setData(tableName,playlist)
            statusDb.setTableName(tableName)
        }
    }

    fun command(query: String?):Song? {
        when(query){
            "current" -> return getSong("current")
            "next" -> return getSong("next")
            "prev" -> return getSong("prev")
        }
    return null
    }

    private fun getSong(order:String):Song?{
        if(playlist.size>0){
            var position=0
            for (i in 0 until playlist.size){
                if (playlist[i].is_playing){
                    position=i
                }
                playlist[i].is_playing=false
            }
            when(order){
                "current" ->{
                    playlist[position].is_playing=true
                    return playlist[position]
                }
                "next" ->{
                    position = if (position==playlist.size-1){
                        0
                    }else{
                        position+1
                    }
                    playlist[position].is_playing=true
                    db.setData(tableName,playlist)
                    return playlist[position]
                }
                "prev" ->{
                    position= if(position==0) playlist.size-1 else position-1
                    playlist[position].is_playing=true
                    db.setData(tableName,playlist)
                    return playlist[position]
                }
            }



        }
        return null
    }
    val getPlaylist  get()= playlist
    val getTableName get()= tableName
}