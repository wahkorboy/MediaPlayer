package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import com.wahkor.mediaplayer.receiver.AudioReceiver
import com.wahkor.mediaplayer.service.BackgroundService

class EmptyActivity : AppCompatActivity() {
    private var handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var mp=BackgroundService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        val mpService= Intent(this,BackgroundService::class.java)
        startService(mpService)

        val mAudioManager = getSystemService (Context.AUDIO_SERVICE) as AudioManager
        val mReceiverComponent = ComponentName( this, AudioReceiver::class.java)
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);
        setRunnable()
    }
    private fun setRunnable(){
        val title=findViewById<TextView>(R.id.empty_Title)
        runnable= Runnable {
            title.text=mp.title
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }
}