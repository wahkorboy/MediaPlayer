package com.wahkor.mediaplayer

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.mediaplayer.`interface`.SettingClick
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.PlaylistRecyclerAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.databinding.ActivityPlayerBinding
import com.wahkor.mediaplayer.model.Song

var mp = MediaPlayer()
private const val tableName = "playlist_current"

class PlayerActivity : AppCompatActivity(), SettingClick {
    private lateinit var runnable: Runnable
    private var handles = Handler()
    private lateinit var oldSong:Song
    private lateinit var adapter: PlaylistRecyclerAdapter
    private lateinit var songList: ArrayList<Song>
    private var playPosition = 0
    private var isPlayEnable = false
    private val view: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayListDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        mp = MediaPlayer()
        db = PlayListDB(this)
        initial()
        adapter = PlaylistRecyclerAdapter(songList) { newList ->
            if (newList.size==0){
                db.setData(tableName,ArrayList())
                playPosition=0
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)

            }else{
                db.setData(tableName,newList)
                songList = newList
                var i=0
                while (i<newList.size){
                    if(newList[i].is_playing){
                        playPosition=i
                    }
                    i++
                }
            }
            if (oldSong.data!=songList[playPosition].data){
                setItemClick()
            }

        }
        view.thesongListView.layoutManager = LinearLayoutManager(this)
        view.thesongListView.adapter = adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(view.thesongListView)
        adapter.notifyDataSetChanged()
        view.thesongPlay.setOnClickListener {
            if (isPlayEnable) {
                if (mp.isPlaying) {
                    view.thesongPlay.setImageResource(R.drawable.ic_baseline_play)
                    mp.pause()
                } else {
                    mp.start()
                    view.thesongPlay.setImageResource(R.drawable.ic_baseline_pause)
                }
            }
        }
        view.thesongPrev.setOnClickListener {
            if (playPosition == 0) songList.size - 1
            else --playPosition
            setItemClick()
        }
        view.thesongNext.setOnClickListener {
            val item = if (playPosition == songList.size - 1) 0
            else ++playPosition
            setItemClick()
        }
        view.setting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this, view.setting)) { intent ->
                startActivity(intent)
            }
        }
        mp.setOnCompletionListener {
            if (isPlayEnable) {
                val item = if (playPosition == songList.size - 1) 0
                else ++playPosition
                setItemClick()
            }
        }
        view.thesongShowDetail.setOnClickListener { playListDropDown() }
        view.thesongSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isPlayEnable && mp.isPlaying && fromUser) {
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
        songList = db.getData(tableName)
        if (songList.size == 0) {
            songList = db.getData("allSong")
            db.setData(tableName, songList)
        }
        var position = 0
        while (position < songList.size) {
            if (songList[position++].is_playing) {
                playPosition = position - 1
            }
        }
        songList[playPosition].is_playing=true
            val song = songList[playPosition]
            oldSong=song
            mp.reset()
            mp.setDataSource(song.data)
            mp.prepare()
            isPlayEnable = true
            setRunnable()
            setDetail()

    }

    private fun setRunnable() {
        view.thesongSeekbar.max = mp.duration
        runnable = Runnable {
            view.tvDue.text = getMinute(mp.duration - mp.currentPosition)
            view.tvPass.text = getMinute(mp.currentPosition)
            view.thesongSeekbar.progress = mp.currentPosition
            handles.postDelayed(runnable, 1000)
        }
        handles.postDelayed(runnable, 1000)
    }


    private fun setItemClick() {
        val currentState=mp.isPlaying
        val song = songList[playPosition]
        mp.reset()
        mp.setDataSource(song.data)
        mp.prepare()
        isPlayEnable = true
        if(currentState){
            mp.start()
            view.thesongPlay.setImageResource(R.drawable.ic_baseline_pause)
        }
        setRunnable()
        setDetail()
        db.setData(tableName, songList)
        adapter.notifyDataSetChanged()

    }

    private fun setDetail() {
        val song = songList[playPosition]
        view.thesongTitle.text = song.title
        view.thesongDetailName.text = song.title
        view.thesongDetailArtist.text = song.artist
        view.thesongDetailAlbum.text = song.album
        view.thesongDetailDuration.text = getMinute(mp.duration)
    }

    private fun playListDropDown() {
        val detail = view.thesongDetailLayout
        val icon = view.thesongShowDetail
        if (detail.visibility == View.VISIBLE) {
            detail.visibility = View.GONE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        } else {
            detail.visibility = View.VISIBLE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        }

    }

}

fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun getMinute(time: Int): CharSequence {
    var secs = time / 1000
    var minutes = secs / 60
    val hours = minutes / 60
    minutes -= hours * 60
    secs = secs - minutes * 60 - hours * 60 * 60
    return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
}