package com.wahkor.mediaplayer

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.wahkor.mediaplayer.service.AudioService
import java.lang.String

class AudioControlHelper(val activity: Activity,val callback:(state:Int)-> Unit) {

    fun build(){
        mMediaBrowserCompat = MediaBrowserCompat(
            activity, ComponentName(
                activity,
                AudioService::class.java
            ),
            mMediaBrowserCompatConnectionCallback, activity.intent.extras
        )
        mMediaBrowserCompat!!.connect()
    }
    companion object {
        private const val STATE_PAUSED = 0
        private const val STATE_PLAYING = 1

    }
    var mCurrentState:Int=0
    var mMediaBrowserCompat: MediaBrowserCompat?=null
    var mMediaControllerCompat: MediaControllerCompat?=null
    private val mMediaBrowserCompatConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                mMediaControllerCompat =
                    MediaControllerCompat(activity, mMediaBrowserCompat!!.sessionToken)
                mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                MediaControllerCompat.setMediaController(activity, mMediaControllerCompat)
                MediaControllerCompat.getMediaController(activity).transportControls.playFromMediaId(
                    String.valueOf(R.raw.abandonedluna), null
                )
            }
        }

    val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
        object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
                super.onPlaybackStateChanged(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        mCurrentState = STATE_PLAYING
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        mCurrentState = STATE_PAUSED
                    }
                    else -> {}
                }
                callback(mCurrentState)
            }
        }
    fun playBTN(){
        mCurrentState = if (mCurrentState == STATE_PAUSED) {
            MediaControllerCompat.getMediaController(activity).transportControls.play()
            STATE_PLAYING
        } else {
            if (MediaControllerCompat.getMediaController(activity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                MediaControllerCompat.getMediaController(activity).transportControls.pause()
            }
            STATE_PAUSED
        }
    }

    fun onDestroy() {
        if (MediaControllerCompat.getMediaController(activity).playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(activity).transportControls.pause()
        }
        mMediaBrowserCompat!!.disconnect()
    }
}