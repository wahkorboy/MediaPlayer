package com.wahkor.mediaplayer.`interface`

import android.widget.Button
import android.widget.ImageView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.mp

interface PlayerActivityInterface {
    fun onPlayBTNClick(isPlayEnable:Boolean,playBTN:ImageView){

        if (isPlayEnable) {
            if (mp.isPlaying) {
                playBTN.setImageResource(R.drawable.ic_baseline_play)
                mp.pause()
            } else {
                mp.start()
                playBTN.setImageResource(R.drawable.ic_baseline_pause)
            }
        }
    }
    fun onPrevBTNClick(isPlayEnable:Boolean,playBTN:ImageView){

        if (isPlayEnable) {
            if (mp.isPlaying) {
                playBTN.setImageResource(R.drawable.ic_baseline_play)
                mp.pause()
            } else {
                mp.start()
                playBTN.setImageResource(R.drawable.ic_baseline_pause)
            }
        }
    }
    fun onNextBTNClick(isPlayEnable:Boolean,playBTN:ImageView){

        if (isPlayEnable) {
            if (mp.isPlaying) {
                playBTN.setImageResource(R.drawable.ic_baseline_play)
                mp.pause()
            } else {
                mp.start()
                playBTN.setImageResource(R.drawable.ic_baseline_pause)
            }
        }
    }
}