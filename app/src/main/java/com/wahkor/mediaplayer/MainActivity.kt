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
import java.lang.String

class MainActivity : AppCompatActivity() {
    private var mCurrentState = 0
    private var mMediaBrowserCompat: MediaBrowserCompat? = null
    private var mMediaControllerCompat: MediaControllerCompat? = null
    private var mPlayPauseToggleButton: Button? = null
    private val mMediaBrowserCompatConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                mMediaControllerCompat =
                    MediaControllerCompat(this@MainActivity, mMediaBrowserCompat!!.sessionToken)
                mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                MediaControllerCompat.setMediaController(this@MainActivity, mMediaControllerCompat)
                //setSupportMediaController(mMediaControllerCompat);
                MediaControllerCompat.getMediaController(this@MainActivity).transportControls.playFromMediaId(
                    String.valueOf(R.raw.abandonedluna), null
                )
            }
        }
    private val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                if (state == null) {
                    return
                }
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = STATE_PLAYING
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = STATE_PAUSED
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPlayPauseToggleButton = findViewById<View>(R.id.button) as Button
        mMediaBrowserCompat = MediaBrowserCompat(
            this, ComponentName(
                this,
                AudioService::class.java
            ),
            mMediaBrowserCompatConnectionCallback, intent.extras
        )
        mMediaBrowserCompat!!.connect()
        mPlayPauseToggleButton!!.setOnClickListener {
            mCurrentState = if (mCurrentState == STATE_PAUSED) {
                MediaControllerCompat.getMediaController(this@MainActivity).transportControls.play()
                STATE_PLAYING
            } else {
                if (MediaControllerCompat.getMediaController(this@MainActivity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                    MediaControllerCompat.getMediaController(this@MainActivity).transportControls.pause()
                }
                STATE_PAUSED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MediaControllerCompat.getMediaController(this@MainActivity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(this@MainActivity).transportControls.pause()
        }
        mMediaBrowserCompat!!.disconnect()
    }

    companion object {
        private const val STATE_PAUSED = 0
        private const val STATE_PLAYING = 1
    }
}