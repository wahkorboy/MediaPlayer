package com.wahkor.mediaplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class PlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer:MediaPlayer
    private lateinit var playBTN:ImageView
    private lateinit var titleView:TextView
    var position=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        mediaPlayer= MediaPlayer()
        position= Random.nextInt(0, TrackFileList.size-1)
        mediaPlayer.setDataSource(TrackFileList[position].Uri)
        mediaPlayer.prepare()
        playBTN=findViewById(R.id.playerPlay)
        titleView=findViewById(R.id.playerTitle)
    }

    fun PlayBTN(view: View) {
        setupPlayer()
    }

    fun prevClick(view: View) {
        position=if (position==0) TrackFileList.size-1 else position-1
        preparePlayer()
    }
    fun nextClick(view: View) {
        position=if (position==TrackFileList.size-1) 0 else position+1
        preparePlayer()
    }
    private fun preparePlayer(){
        mediaPlayer.pause()
        mediaPlayer.reset()
        mediaPlayer.setDataSource(TrackFileList[position].Uri)
        mediaPlayer.prepare()
        mediaPlayer.isLooping=true
        setupPlayer()
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupPlayer(){
        val toast=if(mediaPlayer.isPlaying){
            playBTN.setImageDrawable(getDrawable(R.drawable.ic_baseline_play))
            mediaPlayer.pause()
            "Pause"

        }else{
            mediaPlayer.start()
            playBTN.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause))
            "Play"
        }
        titleView.text= TrackFileList[position].Title
        Toast.makeText(this,"$toast...${TrackFileList[position].Title}",Toast.LENGTH_SHORT).show()


    }

}