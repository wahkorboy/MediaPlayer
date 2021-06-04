package com.wahkor.mediaplayer

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wahkor.mediaplayer.databinding.ActivityPlayMusicByFileBinding
import com.wahkor.mediaplayer.model.Song


class PlayMusicByFileActivity : AppCompatActivity() {
    val mp=MediaPlayer()
   // private lateinit var db:PlayerSQL
    private lateinit var Playlist:ArrayList<Song>
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private  val  view:ActivityPlayMusicByFileBinding by lazy {
        ActivityPlayMusicByFileBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
       // db= PlayerSQL(this)
        /*Playlist=db.allPlayList
        if(Playlist.size ==0 ){
            Playlist= SongList
            db.addPlaylist(Playlist)

        }
        val data = intent.data
        if (data != null && intent.action != null &&
            intent.action.equals(Intent.ACTION_VIEW)
        ) {
            mp.setDataSource(this,data)
            mp.prepare()
           mp.start()
            setAudioManager()
            view.playerTitle.text=getRealPathFromURI(this,data)
            view.playerPlay.setOnClickListener {
                mp.start()
                // setAudioManager()

            }
            initial()
            view.playerSeekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser){
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

        }else{
        }*/
        Toast.makeText(this,"Playlist ${Playlist.size}",Toast.LENGTH_LONG).show()
    }

    private fun initial() {
        view.playerSeekbar.max = mp.duration
            setRunnable()

    }

    private fun getTimeInMinute(time: Int): String {
        var secs = time / 1000
        var minutes = secs / 60
        val hours = minutes / 60
        minutes -= hours * 60
        secs = secs - minutes * 60 - hours * 60 * 60
        return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
    }
    private fun setRunnable() {
        runnable = Runnable {
            view.playerSeekbar.progress = mp.currentPosition
            view.tvPass.text = getTimeInMinute(mp.currentPosition)
            view.tvDue.text = getTimeInMinute(mp.duration - mp.currentPosition)
            handler.postDelayed(runnable, 1000)

        }
        handler.postDelayed(runnable, 1000)

    }
    private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
            val proj = arrayOf(MediaStore.Audio.Media.TITLE)
            val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            return if (cursor != null){
                val column: Int = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                cursor.moveToFirst()
                cursor.getString(column)
            }else{"Invalid information"}

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

