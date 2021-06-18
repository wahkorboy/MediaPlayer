package com.wahkor.mediaplayer.service

import android.R
import android.app.PendingIntent
import android.content.*
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.PowerManager
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.wahkor.mediaplayer.MediaStyleHelper.from
import com.wahkor.mediaplayer.`interface`.PlaylistInterface
import java.io.IOException


class AudioService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener,PlaylistInterface {
    private lateinit var mediaSessionCompat: MediaSessionCompat

    companion object{
        private var mediaPlayer=MediaPlayer()
    }
    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaPlayer?.let { mediaPlayer -> if (mediaPlayer.isPlaying) mediaPlayer.pause()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }
    private val mediaSessionCompatCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            if (!successfullyRetrievedAudioFocus()) {
                return
            }
            mediaSessionCompat.isActive=true
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification()
            mediaPlayer?.start()
        }

        override fun onPause() {
            super.onPause()

            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            mediaPlayer?.pause()
            showPausedNotification()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)

            try {
                val afd = resources.openRawResourceFd(Integer.valueOf(mediaId)) ?: return
                try {
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                } catch (e: IllegalStateException) {
                    mediaPlayer.release()
                    initMediaPlayer()
                    mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                }
                afd.close()
                initMediaSessionMetadata()
            } catch (e: IOException) {
                return
            }

            try {
                mediaPlayer.prepare()
            } catch (e: IOException) {
            }

            //Work with extras here if you want
        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
            try {
                val song=getCurrentSong(this@AudioService)
                song?.let {
                    mediaPlayer.setDataSource(it.data)

                    initMediaSessionMetadata()
                    mediaPlayer.prepare()
                    mediaPlayer.setOnCompletionListener(this@AudioService)
                }
            }catch (e:Exception){}
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
Toast.makeText(this,"finish",Toast.LENGTH_SHORT).show()
        // when song complete Edit here
    }

    override fun onDestroy() {
        super.onDestroy()

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.abandonAudioFocus(this)
        unregisterReceiver(noisyReceiver)
        mediaSessionCompat.release()
        NotificationManagerCompat.from(this).cancel(1)
    }
    private fun initMediaSessionMetadata() {
        val metadataBuilder = MediaMetadataCompat.Builder()
        //Notification icon in card
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(
                resources, R.drawable.sym_def_app_icon
            )
        )
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(
                resources,  R.drawable.sym_def_app_icon
            )
        )

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(
            MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(
                resources,  R.drawable.sym_def_app_icon
            )
        )
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "Display Title")
        metadataBuilder.putString(
            MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
            "Display Subtitle"
        )
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1)
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1)
        mediaSessionCompat.setMetadata(metadataBuilder.build())
    }


    private fun showPausedNotification() {
        val builder = from(this, mediaSessionCompat)
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
            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                .setMediaSession(mediaSessionCompat.getSessionToken())
        )
        builder.setSmallIcon(R.drawable.sym_def_app_icon)
        NotificationManagerCompat.from(this).notify(1, builder.build())
    }

    private fun setMediaPlaybackState(statePlaying: Int) {
        val playbackStateBuilder=PlaybackStateCompat.Builder()
        when(statePlaying){
            PlaybackStateCompat.STATE_PLAYING ->{
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PAUSE)
            }
            else ->{
                playbackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY)
            }
        }

        playbackStateBuilder.setState(statePlaying,PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
            0F
        )
        mediaSessionCompat.setPlaybackState(playbackStateBuilder.build())
    }
    private fun showPlayingNotification() {
        val builder =
            from(this@AudioService, mediaSessionCompat)
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
            androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0)
                .setMediaSession(mediaSessionCompat.sessionToken)
        )
        builder.setSmallIcon(R.drawable.sym_def_app_icon)
        NotificationManagerCompat.from(this@AudioService).notify(1, builder.build())
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
        result.sendResult(null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent)
        mediaPlayer.setOnCompletionListener(this)
        val song=getCurrentSong(this)
        song?.let { mediaPlayer.setDataSource(it.data) ;mediaPlayer.prepare();mediaPlayer.start()
            Toast.makeText(this,"initial audioService ",Toast.LENGTH_SHORT).show()}
        //return START_STICKY
        return super.onStartCommand(intent, flags, startId)
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
                mediaPlayer.let {
                    if (!it.isPlaying) it.start()
                    it.setVolume(1.0f, 1.0f)
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer.let {
                    if (it.isPlaying) it.start()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer.let {
                    if (it.isPlaying) it.pause()
                }

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer.let {
                    if (it.isPlaying) it.setVolume(0.3f, 0.3f)
                }
            }
        }
    }
}



























