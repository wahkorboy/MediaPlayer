package com.wahkor.mediaplayer

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

val songPlayer = MediaPlayer()

class PlayMusicByFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testgetfile)
        val data = intent.data
        if (data != null && intent.action != null &&
            intent.action.equals(Intent.ACTION_VIEW)
        ) {

            songPlayer.setDataSource(this, data)
            songPlayer.prepare()
            setAudioManager()
            songPlayer.start()

        }
    }
    private fun setAudioManager() {
        val mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener {
                        //Handle Focus Change
                        when (it) {
                            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                // Pause
                                songPlayer.pause()
                            }
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                // Resume
                                songPlayer.start()
                            }
                            AudioManager.AUDIOFOCUS_LOSS -> {
                                // Stop or pause depending on your need
                                songPlayer.stop()
                            }
                        }
                    }.build()
            )
        } else {
            mAudioManager.requestAudioFocus(
                { focusChange: Int ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                            // Pause
                            songPlayer.pause()
                        }
                        AudioManager.AUDIOFOCUS_GAIN -> {
                            // Resume
                            songPlayer.start()
                        }
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            // Stop or pause depending on your need
                            songPlayer.stop()
                        }
                    }

                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }
}

