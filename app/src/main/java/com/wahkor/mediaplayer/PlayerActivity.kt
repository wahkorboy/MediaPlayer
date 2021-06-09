package com.wahkor.mediaplayer

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.mediaplayer.`interface`.MenuInterface
import com.wahkor.mediaplayer.`interface`.MusicInterface
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.PlaylistAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.databinding.ActivityPlayerBinding
import com.wahkor.mediaplayer.model.Song
import java.io.File


class PlayerActivity : AppCompatActivity(), MenuInterface, MusicInterface {
    private val delayClick:Long=500
    private var lastClick:Long=0
    private var currentClick:Long=0
    private val mp = MusicPlayer()
    private lateinit var runnable: Runnable
    private lateinit var tableName :String
    private var handles = Handler(Looper.getMainLooper())
    private lateinit var adapter: PlaylistAdapter
    private lateinit var songList: MutableList<Song>
    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayListDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db = PlayListDB(this)
        initial()
        adapter = PlaylistAdapter(songList) { newList, Action ->
            var position = 0
            for (i in 0 until newList.size) {
                if (newList[i].is_playing) {
                    position = i
                }
            }
            songList = newList
            when (Action) {
                "ItemClicked" -> {
                    newList[position].is_playing = true
                    mediaPlayer(this,newList[position], "play")

                }
            }
            db.setData(tableName, songList as ArrayList<Song>)

        }
        binding.ListView.layoutManager = LinearLayoutManager(this)
        binding.ListView.adapter = adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.ListView)

        binding.setting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this, binding.setting)) { intent ->
                startActivity(intent)
            }
        }
        binding.menu.setOnClickListener {
            setOnMenuClick(this, PopupMenu(this,binding.menu),tableName){ intent ->
                resultContract.launch(intent)
            }
        }
        binding.Prev.setOnClickListener { prevClick() }
        binding.Next.setOnClickListener { nextClick() }
        binding.Play.setOnClickListener {
            if (mp.isPlaying) mp.action("pause", this)
            else mp.action("play", this)
        }
        binding.ShowDetail.setOnClickListener { playListDropDown() }
        binding.Seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && mp.isPlaying) {
                    mp.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        adapter.notifyDataSetChanged()
    }

    private fun initial() {
        val statusDb=PlaylistStatusDb(this)
        val statusName=statusDb.getTableName
        if (statusName == null){
            songList = db.getData("allSong")
            db.createTable("playlist_default",songList as ArrayList<Song>)
            tableName="playlist_default"
            statusDb.setTableName(tableName)
        }else{
            tableName=statusName
        }
        songList=db.getData(tableName)
        // check if file Exists
        val newList = ArrayList<Song>()
        for (i in 0 until songList.size) {
            val file = File(songList[i].data)
            if (file.exists()) {
                newList.add(songList[i])
            }

        }
        songList = newList
        db.setData(tableName, songList as ArrayList<Song>)

        //setup player
        var position = 0
        for (i in 0 until songList.size) {
            if (songList[i].is_playing) position = i
        }
        songList[position].is_playing = true
        val song = songList[position]
        mediaPlayer(this, song, "Non")
        setRunnable()

    }

    private fun setRunnable() {
        runnable = Runnable {
            binding.Play.setImageDrawable(
                if (mp.isPlaying)
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_pause, null)
                else
                    ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_play, null)
            )
            if (mp.complete){
                nextClick()
            }
            "${tableName.substringAfter("_")} playlist".also { binding.playlistName.text = it }
            binding.Title.text = mp.title
            binding.Seekbar.max = mp.duration
            binding.tvDue.text = mp.dueString
            binding.tvPass.text = mp.passString
            binding.Seekbar.progress = mp.currentPosition
            handles.postDelayed(runnable, 1000)
        }
        handles.postDelayed(runnable, 1000)
    }


    private fun playListDropDown() {
        val playlistManagerLayout = binding.PlaylistManagerLayout
        val icon = binding.ShowDetail
        if (playlistManagerLayout.visibility == View.VISIBLE) {
            playlistManagerLayout.visibility = View.GONE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        } else {
            playlistManagerLayout.visibility = View.VISIBLE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        }

    }


    override fun nextClick() {
        var position = 0
        for (i in 0 until songList.size) {
            if (songList[i].is_playing) {
                position = if (i == songList.size - 1) 0 else i + 1
                songList[i].is_playing = false
            }
        }
        songList[position].is_playing = true
        mediaPlayer(this, songList[position], "play")
        adapter.notifyDataSetChanged()

    }

    private fun prevClick() {
        var position = 0
        for (i in 0 until songList.size) {
            if (songList[i].is_playing) {
                position = if (i == 0) songList.size - 1 else i - 1
                songList[i].is_playing = false
            }
        }
        songList[position].is_playing = true
        mediaPlayer(this, songList[position], "play")
        adapter.notifyDataSetChanged()
    }
    private val resultContract=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult?->
        when(result?.resultCode){
            Activity.RESULT_OK->{
                val data=result.data
                if (data != null){
                    val myResult=data.getStringExtra("result")
                    if(myResult=="Added"){
                        val intent = intent
                        finish()
                        startActivity(intent)
                    }
                }
            }
        }

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_HEADSETHOOK -> {
                lastClick=currentClick
                currentClick=System.currentTimeMillis()
                if(lastClick+delayClick>currentClick){
                    binding.Next.callOnClick()
                }else{
                    binding.Play.callOnClick()
                }
                return true
            }
        }
        //toast("$keyCode ${KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE}");
        return super.onKeyDown(keyCode, event)
    }
}
