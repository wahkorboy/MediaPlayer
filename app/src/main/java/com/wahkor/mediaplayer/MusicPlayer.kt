package com.wahkor.mediaplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

class MusicPlayer {
    companion object{
        var mp=MediaPlayer()
    }
    fun create(uri:String,mAudioManager: AudioManager){
        mp.reset()
        mp.setDataSource(uri)
        setAudioManager(mAudioManager)
    }
    fun create(context: Context,uri:Uri,mAudioManager: AudioManager){
        mp.reset()
        mp.setDataSource(context,uri)
        setAudioManager(mAudioManager)
    }
    fun start() {
        mp.start()
    }
    fun stop() {
        mp.stop()
    }
    fun pause() {
        mp.pause()
    }
   private fun setAudioManager(mAudioManager: AudioManager) {
       mp.prepare()
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
                                mp.pause()
                            }
                            AudioManager.AUDIOFOCUS_GAIN -> {
                                // Resume
                                mp.start()
                            }
                            AudioManager.AUDIOFOCUS_LOSS -> {
                                // Stop or pause depending on your need
                                mp.pause()
                            }
                            else ->{
                                mp.start()
                            }
                        }
                    }.build()
            )
        } else {
            mAudioManager.requestAudioFocus(
                { focusChange: Int ->

                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }
}