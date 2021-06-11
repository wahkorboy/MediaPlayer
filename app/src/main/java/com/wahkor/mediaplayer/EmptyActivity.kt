package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.wahkor.mediaplayer.receiver.AudioReceiver
import com.wahkor.mediaplayer.service.BackgroundService

class EmptyActivity : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var prevBTN: ImageView
    private lateinit var playBTN:ImageView
    private lateinit var nextBTN:ImageView
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


        titleView=findViewById(R.id.empty_Title)
        seekBar=findViewById(R.id.empty_Seekbar)
        prevBTN=findViewById(R.id.empty_Prev)
        playBTN=findViewById(R.id.empty_Play)
        nextBTN=findViewById(R.id.empty_Next)
        prevBTN.setOnClickListener { mp.prevPlay() }
        playBTN.setOnClickListener {
            if(mp.isPlaying()){
                mp.pause()
            }else{
                mp.start()
            }
        }
        nextBTN.setOnClickListener { mp.nextPlay() }
        setRunnable()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    mp.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun setRunnable(){
        runnable= Runnable {
                titleView.text=mp.title
                seekBar.max=mp.duration
                seekBar.progress=mp.currentPosition
                playBTN.setImageDrawable(
                    if (mp.isPlaying())
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause, null)
                    else
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play, null))

            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }
}