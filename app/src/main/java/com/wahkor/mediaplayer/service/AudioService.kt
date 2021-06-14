package com.wahkor.mediaplayer.service

import android.app.PendingIntent
import android.content.*
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver


class AudioService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener {

    private lateinit var mediaSessionCompat: MediaSessionCompat
    private var mediaPlayer: MediaPlayer? = null
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPlayer?.let { mediaPlayer -> if (mediaPlayer.isPlaying) mediaPlayer.pause() }
        }

        private val mediaSessionCompatCallbacks = object : MediaSessionCompat.Callback() {
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
    private val mediaSessionCompatCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }
        }
    }

    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )

        return result == AudioManager.AUDIOFOCUS_GAIN

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, packageName)) {
            BrowserRoot("Media Player", null)
        } else null

    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null);
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        //return START_STICKY
        return super.onStartCommand(intent, flags, startId);
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
        initMediaSession()
        initNoisyReceiver()
    }

    private fun initNoisyReceiver() {
        val filters = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(noisyReceiver, filters)
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mediaSessionCompat =
            MediaSessionCompat(applicationContext, "TAG", mediaButtonReceiver, null)
        mediaSessionCompat.setCallback(mediaSessionCompatCallback)
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mediaSessionCompat.setMediaButtonReceiver(pendingIntent)

        sessionToken = mediaSessionCompat.sessionToken

    }

    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer().also { mediaPlayer ->
            mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mediaPlayer.setVolume(1.0f, 1.0f)

        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer?.let {
                    if (!it.isPlaying) it.start()
                    it.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.let {
                    if (it.isPlaying) it.start()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer?.let {
                    if (it.isPlaying) it.pause()
                }

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.let {
                    if (it.isPlaying) it.setVolume(0.3f, 0.3f)
                }
            }
        }
    }
}



























