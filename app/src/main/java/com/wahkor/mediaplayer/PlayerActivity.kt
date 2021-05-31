package com.wahkor.mediaplayer

import android.annotation.SuppressLint
import android.database.DataSetObserver
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.wahkor.mediaplayer.model.CurrentTrack
import com.wahkor.mediaplayer.model.TrackFile
import org.w3c.dom.Text

var currentTrack = CurrentTrack(TrackFile(), 0, false)
lateinit var mediaPlayer: MediaPlayer

class PlayerActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private lateinit var tv_pass: TextView
    private lateinit var tv_due: TextView
    private lateinit var playBTN: ImageView
    private lateinit var titleView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var playlistListView: ListView
    private var adapter= playListAdapter(TrackFileList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playBTN = findViewById(R.id.playerPlay)
        titleView = findViewById(R.id.playerTitle)
        tv_due = findViewById(R.id.tv_due)
        tv_pass = findViewById(R.id.tv_pass)
        seekBar = findViewById(R.id.playerSeekbar)
        playlistListView=findViewById(R.id.playerListView)
        playlistListView.adapter=adapter
        adapter.notifyDataSetInvalidated()
        initial()
        //SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    var duration = mediaPlayer.duration
                    duration = (duration * progress / 100).toInt()
                    mediaPlayer.seekTo(duration)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
        mediaPlayer.setOnCompletionListener {
            Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show()
            nextClick(playBTN)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initial() {
        if (currentTrack.isPlaying) {
            setRunnable()
        }
        playBTN = currentTrack.setPlayButton(this, playBTN)
        titleView.text = currentTrack.title
    }


    fun playClick(view: View) {

        setupPlayer()
    }

    fun prevClick(view: View) {
        currentTrack.position =
            if (currentTrack.position == 0) TrackFileList.size - 1 else currentTrack.position - 1
        preparePlayer()
    }

    fun nextClick(view: View) {
        currentTrack.position =
            if (currentTrack.position == TrackFileList.size - 1) 0 else currentTrack.position + 1
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.pause()
        mediaPlayer.reset()
        currentTrack =
            CurrentTrack(TrackFileList[currentTrack.position], currentTrack.position, false)
        mediaPlayer.setDataSource(currentTrack.track.Uri)
        mediaPlayer.prepare()
        mediaPlayer.isLooping = true
        seekBar.max = mediaPlayer.duration
        setupPlayer()
    }

    private fun getTimeInMinute(time: Int): String {
        var secs = time / 1000
        var minutes = secs / 60
        val hours = minutes / 60
        minutes -= hours * 60
        secs = secs - minutes * 60 - hours * 60 * 60
        return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupPlayer() {
        val toast = if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            "Pause"

        } else {
            mediaPlayer.start()
            "Play"
        }
        currentTrack.isPlaying = mediaPlayer.isPlaying
        playBTN = currentTrack.setPlayButton(this, playBTN)
        titleView.text = currentTrack.title
        setRunnable()
        Toast.makeText(this, "$toast...${currentTrack.title}", Toast.LENGTH_SHORT).show()


    }

    private fun setRunnable() {
        var string = currentTrack.title!!+"              "
        runnable = Runnable {
            seekBar.progress = mediaPlayer.currentPosition * 100 / mediaPlayer.duration
            tv_pass.text = getTimeInMinute(mediaPlayer.currentPosition)
            tv_due.text = getTimeInMinute(mediaPlayer.duration - mediaPlayer.currentPosition)
            string = string.substring(5, string.length) + string.substring(0, 5)
            titleView.text = string //if(string.length>70) string.substring(0,70) else string
            //title.setTextColor(resources.getColor(R.color.text_run_color))
            handler.postDelayed(runnable, 1000)

        }
        handler.postDelayed(runnable, 1000)

    }

    override fun onBackPressed() {
    }
    inner class playListAdapter(var playlistList:ArrayList<TrackFile>):BaseAdapter(){
        override fun getCount(): Int {
            return playlistList.size
        }

        override fun getItem(position: Int): Any {
            return playlistList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view=View.inflate(this@PlayerActivity,R.layout.play_list_layout,null)
            val title=view.findViewById<TextView>(R.id.playlistTitle)
            val song=playlistList[position]
            title.text=song.Title
            return view
        }

    }

}