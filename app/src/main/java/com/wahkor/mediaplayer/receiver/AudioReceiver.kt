package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.Toast
import com.wahkor.mediaplayer.service.AudioService

class AudioReceiver : BroadcastReceiver() {
    companion object {
        private lateinit var mp: AudioService
        private var lastClick = 0L
        private var currentClick = 0L
        private const val delayClick = 100L
        private var time=0

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        mp = AudioService()
            if (intent?.action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                val event = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                event?.let {
                    val action = it.action
                    if (action == KeyEvent.ACTION_DOWN) {
                        lastClick = currentClick
                        currentClick = System.currentTimeMillis()
                        if (currentClick < delayClick + lastClick) {
                            mp.nextPlay()
                            lastClick=0
                        } else {
                            if (mp.isPlaying()) {
                                mp.pause()
                            } else {
                                mp.start()
                            }
                        }
                    }
                }
            }

    }

    fun toast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}