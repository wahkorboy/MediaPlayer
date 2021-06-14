package com.wahkor.mediaplayer

import android.content.ComponentName
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.mediaplayer.service.AudioService

class MainActivity : AppCompatActivity() {

    private var mPlayPauseToggleButton: Button?=null
    private lateinit var aCH:AudioControlHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        aCH=AudioControlHelper(this)
        mPlayPauseToggleButton = findViewById<View>(R.id.button) as Button
        aCH.mMediaBrowserCompat = MediaBrowserCompat(
            this, ComponentName(
                this,
                AudioService::class.java
            ),
            aCH.mMediaBrowserCompatConnectionCallback, intent.extras
        )
        aCH.mMediaBrowserCompat!!.connect()
        mPlayPauseToggleButton!!.setOnClickListener {
            aCH.mCurrentState = if (aCH.mCurrentState == aCH.getStatePause) {
                MediaControllerCompat.getMediaController(this@MainActivity).transportControls.play()
                aCH.getStatePlay
            } else {
                if (MediaControllerCompat.getMediaController(this@MainActivity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(this@MainActivity).transportControls.pause()
                }
                aCH.getStatePause
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MediaControllerCompat.getMediaController(this@MainActivity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(this@MainActivity).transportControls.pause()
        }
        aCH.mMediaBrowserCompat!!.disconnect()
    }


}