package com.wahkor.mediaplayer.`interface`

import android.content.Context
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song

interface PlaylistInterface {
    fun getCurrentSong(context: Context):Song?{
        val db=PlayListDB(context)
        val tableName="allSong"
        val playlist=db.getData(tableName)
        for (i in 0 until  playlist.size){
            if (playlist[i].is_playing) return playlist[i]
        }
        return null
    }
}