package com.wahkor.mediaplayer

import android.app.Activity
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import java.lang.String

class AudioControlHelper(context: Activity) {

    companion object {
        private const val STATE_PAUSED = 0
        private const val STATE_PLAYING = 1
    }
    var mCurrentState:Int=0
    var mMediaBrowserCompat: MediaBrowserCompat?=null
    var mMediaControllerCompat: MediaControllerCompat?=null
    val mMediaBrowserCompatConnectionCallback: MediaBrowserCompat.ConnectionCallback =
        object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                super.onConnected()
                mMediaControllerCompat =
                    MediaControllerCompat(context, mMediaBrowserCompat!!.sessionToken)
                mMediaControllerCompat!!.registerCallback(mMediaControllerCompatCallback)
                MediaControllerCompat.setMediaController(context, mMediaControllerCompat)
                //setSupportMediaController(mMediaControllerCompat);
                MediaControllerCompat.getMediaController(context).transportControls.playFromMediaId(
                    String.valueOf(R.raw.abandonedluna), null
                )
            }
        }

    val mMediaControllerCompatCallback: MediaControllerCompat.Callback =
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
    val getStatePlay get() = STATE_PLAYING
    val getStatePause get() = STATE_PAUSED
}