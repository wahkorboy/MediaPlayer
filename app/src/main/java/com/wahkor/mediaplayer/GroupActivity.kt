package com.wahkor.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.`interface`.GroupActivityInterface
import com.wahkor.mediaplayer.adapter.GroupAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song

class GroupActivity : AppCompatActivity(), GroupActivityInterface {
    private lateinit var runnable: Runnable
    private lateinit var checkComplete:Runnable
    private var handler = Handler()
    private lateinit var current: Song
    private lateinit var db: PlayListDB
    private lateinit var allSongs: MutableList<Song>
    private lateinit var adapter: GroupAdapter
    val mp = MusicPlayer()

    private lateinit var title:TextView
    private lateinit var pass:TextView
    private lateinit var due:TextView
    private lateinit var seekbar:SeekBar
    private lateinit var prevBtn : ImageView
    private lateinit var playBtn :ImageView
    private lateinit var nextBtn :ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        title = findViewById<TextView>(R.id.groupViewTitle)
        pass = findViewById<TextView>(R.id.groupTvPass)
        due = findViewById<TextView>(R.id.groupTvDue)
        seekbar = findViewById<SeekBar>(R.id.groupSeekbar)
        prevBtn = findViewById<ImageView>(R.id.groupPrevBTN)
        playBtn = findViewById<ImageView>(R.id.groupPlayBTN)
        nextBtn = findViewById<ImageView>(R.id.groupNextBTN)
        db = PlayListDB(this)
        allSongs = db.getData("allSong")
        allSongs.sortBy { it.folderPath }
        adapter = GroupAdapter(this, allSongs) { song,returnList ->
            allSongs=returnList
            mediaPlayer(this, song, "play")
            current = song
        }
        val listView = findViewById<RecyclerView>(R.id.groupRecyclerView)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter
        initial()
        nextBtn.setOnClickListener {
            var position=0
            for (i in 0 until allSongs.size){
                if (allSongs[i].is_playing){
                    position=if (i==allSongs.size-1) 0 else i+1
                    allSongs[i].is_playing=false
                }
            }
            allSongs[position].is_playing=true
            mediaPlayer(this,allSongs[position],"play")
            adapter.notifyDataSetChanged()
        }

        prevBtn.setOnClickListener {
            var position=0
            for (i in 0 until allSongs.size){
                if (allSongs[i].is_playing){
                    position=if (i==0) (allSongs.size-1) else i-1
                    allSongs[i].is_playing=false
                }
            }
            allSongs[position].is_playing=true
            mediaPlayer(this,allSongs[position],"play")
            adapter.notifyDataSetChanged()
        }

    }

    private fun initial() {
        playBtn.setOnClickListener {
            if (mp.isPlaying) {
                mp.action("pause", this)
            } else {
                mp.action("play", this)
            }}
            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        if (mp.isPlaying) {
                            mp.seekTo(progress)
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
            for (i in 0 until allSongs.size ) {
                if (allSongs[i].is_playing) {
                    current = allSongs[i]
                }
            }
            mediaPlayer(this, current, "")
            runnable = Runnable {
                if(mp.complete){
                    prevBtn.callOnClick()
                }
                seekbar.max = mp.duration
                title.text = mp.title
                playBtn.setImageResource(if (!mp.isPlaying) R.drawable.ic_baseline_play else R.drawable.ic_baseline_pause)
                seekbar.progress = mp.currentPosition
                pass.text = mp.passString
                due.text = mp.dueString
                handler.postDelayed(runnable, 1000)
            }
            handler.postDelayed(runnable, 1000)
        }
    }