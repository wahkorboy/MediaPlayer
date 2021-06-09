package com.wahkor.mediaplayer.service

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.os.Bundle
import android.service.media.MediaBrowserService
import android.text.TextUtils


class BackgroundMediaService(context: Context): MediaBrowserService(), MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener {
    private val mMediaPlayer: MediaPlayer? = null
    private val mMediaSessionCompat: MediaSession? = null
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            BrowserRoot(applicationContext.getString(R.string.app_name), null)
        } else null

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowser.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        TODO("Not yet implemented")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        TODO("Not yet implemented")
    }

    private val mNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying) {
                mMediaPlayer.pause()
            }
        }
    }
    private val mMediaSessionCallback: MediaSession.Callback = object : MediaSession.Callback() {
        override fun onPlay() {
            super.onPlay()
        }

        override fun onPause() {
            super.onPause()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
        }
    }
}