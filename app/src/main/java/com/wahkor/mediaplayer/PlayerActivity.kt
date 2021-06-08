package com.wahkor.mediaplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.mediaplayer.`interface`.MenuInterface
import com.wahkor.mediaplayer.`interface`.MusicInterface
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.PlaylistAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.databinding.ActivityPlayerBinding
import com.wahkor.mediaplayer.model.Song
import java.io.File


class PlayerActivity : AppCompatActivity(), MenuInterface, MusicInterface {
    private val mp = MusicPlayer()
    private lateinit var runnable: Runnable
    private val tableName = "playlist_current"
    private var handles = Handler()
    private lateinit var adapter: PlaylistAdapter
    private lateinit var songList: ArrayList<Song>
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
            newList[position].is_playing = true
            songList = newList
            val song = newList[position]
            when (Action) {
                "ItemClicked" -> {
                    mediaPlayer(this, song, "play")

                }
            }
            db.setData(tableName, songList)

        }
        binding.ListView.layoutManager = LinearLayoutManager(this)
        binding.ListView.adapter = adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.ListView)



        adapter.notifyDataSetChanged()

        binding.setting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this, binding.setting)) { intent ->
                startActivity(intent)
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
        binding.addSongToList.setOnClickListener {
            val intent = Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 12345)
        }

        adapter.notifyDataSetChanged()
    }

    private fun initial() {
        songList = db.getData(tableName)
        if (songList.size == 0) {
            songList = db.getData("allSong")
        }
        // check if file Exists
        val newList = ArrayList<Song>()
        for (i in 0 until songList.size) {
            val file = File(songList[i].data)
            if (file.exists()) {
                newList.add(songList[i])
            }

        }
        songList = newList
        db.setData(tableName, songList)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12345 && resultCode == RESULT_OK && data != null) {
            if (data.data != null) {
                toast("single file")

            } else if (data.clipData != null) {
                for (i in 0 until data.clipData!!.itemCount) {

                    val song = data.clipData!!.getItemAt(i).uri
                }
            }
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

    fun prevClick() {
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

}
