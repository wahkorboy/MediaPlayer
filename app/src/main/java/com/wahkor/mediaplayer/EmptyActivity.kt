package com.wahkor.mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.`interface`.MenuInterface
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.PlaylistAdapter
import com.wahkor.mediaplayer.model.Song
import com.wahkor.mediaplayer.receiver.AudioReceiver
import com.wahkor.mediaplayer.service.BackgroundService

class EmptyActivity : AppCompatActivity(),MenuInterface {
    private lateinit var menuImageView: ImageView
    private lateinit var titleView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var prevBTN: ImageView
    private lateinit var playBTN:ImageView
    private lateinit var nextBTN:ImageView
    private lateinit var recyclerView:RecyclerView
    private lateinit var adapter:PlaylistAdapter
    private lateinit var playlistName:TextView
    private var songQuery=ArrayList<Song>()
    private var handler=Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private var mp=BackgroundService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        songQuery=mp.getSongQuery
        menuImageView=findViewById(R.id.empty_menu)
        titleView=findViewById(R.id.empty_Title)
        seekBar=findViewById(R.id.empty_Seekbar)
        prevBTN=findViewById(R.id.empty_Prev)
        playBTN=findViewById(R.id.empty_Play)
        nextBTN=findViewById(R.id.empty_Next)
        playlistName=findViewById(R.id.empty_playlistName)
        recyclerView=findViewById(R.id.empty_ListView)
        prevBTN.setOnClickListener { mp.prevPlay() }
        playBTN.setOnClickListener {
            if(mp.isPlaying()){
                mp.pause()
            }else{
                mp.start()
            }
        }
        nextBTN.setOnClickListener { mp.nextPlay() }
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
        menuImageView.setOnClickListener {
            setOnMenuClick(this, PopupMenu(this,it as ImageView),mp.getTableName!!){intent ->
            startActivity(intent)
            }
        }
        adapter= PlaylistAdapter(songQuery){ newList, action ->
            when(action){
                "ItemClicked"->if(mp.playlistAction(newList)){
                    mp.start()
                }
                "ItemMoved"->mp.playlistMoved(newList)
                "ItemRemoved"->mp.playlistRemoved(newList)
            }
        }
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter

        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        setRunnable()
        adapter.notifyDataSetChanged()
    }

    private fun setRunnable(){
        runnable= Runnable {
            mp.getTableName?.let {
                playlistName.text=it
            }
            if(mp.isUpdate){
                songQuery=mp.getSongQuery
                adapter.notifyDataSetChanged()
                mp.clearUpdate()
                for(i in 0 until songQuery.size){
                    if(songQuery[i].is_playing)
                        recyclerView.scrollToPosition(i)
                }
            }
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