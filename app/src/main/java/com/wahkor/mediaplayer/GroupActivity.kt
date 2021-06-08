package com.wahkor.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.`interface`.GroupActivityInterface
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.GroupAdapter
import com.wahkor.mediaplayer.adapter.GroupTouchHelperCallback
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
    private lateinit var recyclerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        recyclerView = findViewById(R.id.groupRecyclerView)
        title = findViewById(R.id.groupViewTitle)
        pass = findViewById(R.id.groupTvPass)
        due = findViewById(R.id.groupTvDue)
        seekbar = findViewById(R.id.groupSeekbar)
        prevBtn = findViewById(R.id.groupPrevBTN)
        playBtn = findViewById(R.id.groupPlayBTN)
        nextBtn = findViewById(R.id.groupNextBTN)
        db = PlayListDB(this)
        allSongs = db.getData("allSong")
        allSongs.sortBy { it.folderPath }
        adapter = GroupAdapter(this, allSongs) { song,returnList,action ->

            allSongs=returnList
            current = song
            when(action){
                "ItemClick" -> {
                    mediaPlayer(this, song, "play")
                }
            }
        }
        val callback=GroupTouchHelperCallback(adapter)
        val itemTouchHelperCallback=ItemTouchHelper(callback)
        itemTouchHelperCallback.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        initial()
        nextBtn.setOnClickListener {
            nextClick()
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

    override fun nextClick() {
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
}