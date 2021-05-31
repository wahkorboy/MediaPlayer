package com.wahkor.mediaplayer.model

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat.getDrawable
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.TrackFileList
import com.wahkor.mediaplayer.currentTrack

data class CurrentTrack(
    var track:TrackFile,
    var position:Int,
    var isPlaying:Boolean,
){
    fun setPlayButton(context: Context, playBTN:ImageView):ImageView{
        if(this.isPlaying){
            playBTN.setImageDrawable(getDrawable(context,R.drawable.ic_baseline_pause))

        }else{
            playBTN.setImageDrawable(getDrawable(context,R.drawable.ic_baseline_play))
        }
        return playBTN

    }
    val title get() =track.Title
}
