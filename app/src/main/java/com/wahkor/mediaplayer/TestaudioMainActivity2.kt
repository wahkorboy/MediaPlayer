package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wahkor.mediaplayer.receiver.AudioReceiver

class TestAudioMainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testaudio_main2)
        val mAudioManager = getSystemService (Context.AUDIO_SERVICE) as AudioManager
        val mReceiverComponent = ComponentName( this, AudioReceiver::class.java)
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
        toast("test audio")
    }
}