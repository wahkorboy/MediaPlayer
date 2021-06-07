package com.wahkor.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.`interface`.GroupActivityInterface
import com.wahkor.mediaplayer.adapter.GroupAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song

class GroupActivity : AppCompatActivity(),GroupActivityInterface {
    private lateinit var runnable: Runnable
    private var handler=Handler()
    private lateinit var current:Song
    private lateinit var db:PlayListDB
    private lateinit var allSongs:MutableList<Song>
    private lateinit var adapter:GroupAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        db= PlayListDB(this)
        allSongs= db.getData("allSong")
        allSongs.sortBy { it.folderPath }
        adapter= GroupAdapter(this,allSongs ){song->
            mediaPlayer(this,song,"play")
            current=song
        }
        val listView=findViewById<RecyclerView>(R.id.groupRecyclerView)
        listView.layoutManager=LinearLayoutManager(this)
        listView.adapter=adapter
        initial()


    }

    private fun initial() {
        val mp=MusicPlayer()
        val title=findViewById<TextView>(R.id.groupViewTitle)
        val pass=findViewById<TextView>(R.id.groupTvPass)
        val due=findViewById<TextView>(R.id.groupTvDue)
        val seekbar=findViewById<SeekBar>(R.id.groupSeekbar)
        seekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    if(mp.isPlaying){
                        mp.seekTo(progress)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        for(i in 0 until allSongs.size-1){
            if (allSongs[i].is_playing){
                current=allSongs[i]
            }
        }
        mediaPlayer(this,current,"")
        runnable= Runnable {
            seekbar.max=mp.duration
            title.text=current.title
            seekbar.progress=mp.currentPosition
            pass.text=mp.passString
            due.text=mp.dueString
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
    }
}