package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wahkor.mediaplayer.receiver.AudioReceiver
import com.wahkor.mediaplayer.service.BackgroundService

class EmptyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        val mpService= Intent(this,BackgroundService::class.java)
        startService(mpService)

        val mAudioManager = getSystemService (Context.AUDIO_SERVICE) as AudioManager
        val mReceiverComponent = ComponentName( this, AudioReceiver::class.java)
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
    }
}