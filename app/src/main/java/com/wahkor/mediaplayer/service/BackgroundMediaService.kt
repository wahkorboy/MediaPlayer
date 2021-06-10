package com.tutsplus.backgroundaudio

import android.R
import android.app.PendingIntent
import android.content.*
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.PowerManager
import android.os.ResultReceiver
import android.service.media.MediaBrowserService
import android.service.media.MediaBrowserService.BrowserRoot
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wahkor.mediaplayer.MediaStyleHelper.from
import java.io.IOException

class BackgroundAudioService : MediaBrowserService(), OnCompletionListener,
    OnAudioFocusChangeListener {
    private var mMediaPlayer: MediaPlayer? = null
    private var mMediaSessionCompat: MediaSession? = null
    private val mNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
            }
        }
    }
    private val mMediaSessionCallback: MediaSession.Callback = object : MediaSession.Callback() {
        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return
            }
            mMediaSessionCompat.setActive(true)
            setMediaPlaybackState(PlaybackState.STATE_PLAYING)
            showPlayingNotification()
            mMediaPlayer!!.start()
        }

        override fun onPause() {
            super.onPause()
            if (mMediaPlayer!!.isPlaying) {
                mMediaPlayer!!.pause()
                setMediaPlaybackState(PlaybackState.STATE_PAUSED)
                showPausedNotification()
            }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            try {
                val afd: AssetFileDescriptor =
                    getResources().openRawResourceFd(Integer.valueOf(mediaId))
                        ?: return
                try {
                    mMediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                } catch (e: IllegalStateException) {
                    mMediaPlayer!!.release()
                    initMediaPlayer()
                    mMediaPlayer!!.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                }
                afd.close()
                initMediaSessionMetadata()
            } catch (e: IOException) {
                return
            }
            try {
                mMediaPlayer!!.prepare()
            } catch (e: IOException) {
            }

            //Work with extras here if you want
        }

        fun onCommand(command: String?, extras: Bundle?, cb: ResultReceiver?) {
            super.onCommand(command, extras, cb)
            if (COMMAND_EXAMPLE.equals(command, ignoreCase = true)) {
                //Custom command here
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
        initMediaSession()
        initNoisyReceiver()
    }

    private fun initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(mNoisyReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        val audioManager = ContextCompat.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager!!.abandonAudioFocus(this)
        unregisterReceiver(mNoisyReceiver)
        mMediaSessionCompat.release()
        NotificationManagerCompat.from(this).cancel(1)
    }

    private fun initMediaPlayer() {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer!!.setWakeMode(
            ApplicationProvider.getApplicationContext<Context>(),
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mMediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mMediaPlayer!!.setVolume(1.0f, 1.0f)
    }

    private fun showPlayingNotification() {
        val builder =
            from(this@BackgroundAudioService, mMediaSessionCompat)
                ?: return
        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_media_pause,
                "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )
        builder.setStyle(
            MediaStyle().setShowActionsInCompactView(0)
                .setMediaSession(mMediaSessionCompat.getSessionToken())
        )
        builder.setSmallIcon(R.mipmap.ic_launcher)
        NotificationManagerCompat.from(this@BackgroundAudioService).notify(1, builder.build())
    }

    private fun showPausedNotification() {
        val builder = from(this, mMediaSessionCompat)
            ?: return
        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_media_play,
                "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            )
        )
        builder.setStyle(
            MediaStyle().setShowActionsInCompactView(0)
                .setMediaSession(mMediaSessionCompat.getSessionToken())
        )
        builder.setSmallIcon(R.mipmap.ic_launcher)
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    private fun initMediaSession() {
        val mediaButtonReceiver = ComponentName(
            ApplicationProvider.getApplicationContext<Context>(),
            MediaButtonReceiver::class.java
        )
        mMediaSessionCompat = MediaSessionCompat(
            ApplicationProvider.getApplicationContext(),
            "Tag",
            mediaButtonReceiver,
            null
        )
        mMediaSessionCompat.setCallback(mMediaSessionCallback)
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, MediaButtonReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0)
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent)
        setSessionToken(mMediaSessionCompat.getSessionToken())
    }

    private fun setMediaPlaybackState(state: Int) {
        val playbackstateBuilder: PlaybackStateCompat.Builder = Builder()
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build())
    }

    private fun initMediaSessionMetadata() {
        val metadataBuilder: MediaMetadataCompat.Builder = Builder()
        //Notification icon in card
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,
            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        )
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        )

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ART,
            BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        )
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Display Title")
        metadataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
            "Display Subtitle"
        )
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1)
        mMediaSessionCompat.setMetadata(metadataBuilder.build())
    }

    private fun successfullyRetrievedAudioFocus(): Boolean {
        val audioManager = ContextCompat.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val result = audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        )
        return result == AudioManager.AUDIOFOCUS_GAIN
    }

    //Not important for general audio service, required for class
    @Nullable
    fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        @Nullable rootHints: Bundle?
    ): BrowserRoot? {
        return if (TextUtils.equals(clientPackageName, getPackageName())) {
            BrowserRoot(getString(R.string.app_name), null)
        } else null
    }

    //Not important for general audio service, required for class
    fun onLoadChildren(parentId: String, result: Result<List<MediaItem?>?>) {
        result.sendResult(null)
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (mMediaPlayer!!.isPlaying) {
                    mMediaPlayer!!.stop()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mMediaPlayer!!.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (mMediaPlayer != null) {
                    mMediaPlayer!!.setVolume(0.3f, 0.3f)
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (mMediaPlayer != null) {
                    if (!mMediaPlayer!!.isPlaying) {
                        mMediaPlayer!!.start()
                    }
                    mMediaPlayer!!.setVolume(1.0f, 1.0f)
                }
            }
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
        }
    }

    fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        const val COMMAND_EXAMPLE = "command_example"
    }
}