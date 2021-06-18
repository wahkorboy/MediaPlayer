package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.mediaplayer.service.AudioService

class MainActivity : AppCompatActivity() {

    private val STATE_PAUSED = 0
    private val STATE_PLAYING = 1
    private var mPlayPauseToggleButton: Button?=null
    private lateinit var aCH:AudioControlHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aCH=AudioControlHelper(){state: Int ->
            when(state){
                STATE_PAUSED -> mPlayPauseToggleButton?.text="Play"
                STATE_PLAYING -> mPlayPauseToggleButton?.text="Pause"
            }

        }
        aCH.build(this)

        mPlayPauseToggleButton = findViewById<View>(R.id.button) as Button
        mPlayPauseToggleButton!!.setOnClickListener {
            aCH.playBTN()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        aCH.onDestroy()
    }


}