package com.wahkor.mediaplayer.`interface`

import android.content.Context
import com.wahkor.mediaplayer.MusicPlayer
import com.wahkor.mediaplayer.model.Song
import java.io.File

interface MusicInterface {
    fun nextClick()
    fun mediaPlayer(context: Context, song: Song, action:String):Boolean{
        val file= File(song.data)
        return if(file.exists()){
            val mp=MusicPlayer()
                    if(mp.data != song.data){
                        mp.create(song,context)
                        mp.action(action,context)
                    }else{
                        mp.action(action,context)
                    }
        }else{
            nextClick()
            false
        }
    }
}