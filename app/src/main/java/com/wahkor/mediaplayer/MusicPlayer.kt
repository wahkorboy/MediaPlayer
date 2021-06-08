package com.wahkor.mediaplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import com.wahkor.mediaplayer.model.Song

class MusicPlayer {
    companion object {
        private var mp = MediaPlayer()
        private var isComplete=false
        private var isReady = false
        private var current = Song("random", "", "", 0, false, "")
    }

    fun create(song: Song, context: Context): Boolean {
        current = song
        mp.reset()
        mp.setDataSource(current.data)
        mp.prepare()
        isReady = true
        isComplete=false
        mp.setOnCompletionListener { isComplete=true }
        return true
    }

    fun action(command: String, context: Context): Boolean {

        return if (isReady) {
            when (command) {
                "play" -> {
                    if (!mp.isPlaying) {
                        toast(context, "Play ${current.title}")
                        mp.start()
                    };true
                }
                "pause" -> {
                    toast(context, "Pause ${current.title}")
                    mp.pause();true
                }
                "stop" -> {
                    toast(context, "Stop ${current.title}")
                    isReady = false;
                    mp.stop();true
                }
                else -> false
            }

        } else {
            false
        }
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
                            else -> {
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

    private fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun seekTo(progress: Int) {
        mp.seekTo(progress)
    }

    val title: String get() = current.title
    val duration: Int get() = current.duration.toInt()
    val currentPosition: Int get() =mp.currentPosition
    val artist: String get() = current.artist
    val album: String get() = current.album
    val data: String get()=current.data
    val isPlaying: Boolean get()=mp.isPlaying
    val ready:Boolean get() = isReady
    val passString:String get() {
        var sec=mp.currentPosition/1000
        var minute=sec/60
        val hour=minute/60
        minute -= hour * 60
        sec=sec-minute*60-hour*60*60
        var text=when{
            hour==0->""
            hour in 1..9 ->"0$hour:"
            else -> "$hour:"
        }
        text+=when{
            minute in 0..9 ->"0$minute:"
            else -> "$minute:"
        }
        text+=when{
            sec in 0..9 ->"0$sec"
            else -> "$sec"
        }
        return text

    }
    val dueString:String get() {
        var sec=(mp.duration-mp.currentPosition)/1000
        var minute=sec/60
        val hour=minute/60
        minute -= hour * 60
        sec=sec-minute*60-hour*60*60
        var text=when{
            hour==0->""
            hour in 1..9 ->"0$hour:"
            else -> "$hour:"
        }
        text+=when{
            minute in 0..9 ->"0$minute:"
            else -> "$minute:"
        }
        text+=when{
            sec in 0..9 ->"0$sec"
            else -> "$sec"
        }
        return text

    }
    val complete:Boolean get() { return isComplete
    }
}
