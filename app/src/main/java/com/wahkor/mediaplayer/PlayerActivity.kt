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
import com.wahkor.mediaplayer.`interface`.MenuInterface
import com.wahkor.mediaplayer.`interface`.PlayerActivityInterface
import com.wahkor.mediaplayer.adapter.CustomItemTouchHelperCallback
import com.wahkor.mediaplayer.adapter.PlaylistRecyclerAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.databinding.ActivityPlayerBinding
import com.wahkor.mediaplayer.model.Song

var mp = MediaPlayer()
private const val tableName = "playlist_current"

class PlayerActivity : AppCompatActivity(), MenuInterface,PlayerActivityInterface {
    private lateinit var runnable: Runnable
    private var handles = Handler()
    private lateinit var oldSong:Song
    private lateinit var adapter: PlaylistRecyclerAdapter
    private lateinit var songList: ArrayList<Song>
    private var playPosition = 0
    private var isPlayEnable = false
    private var mpComplete=false
    private val binding: ActivityPlayerBinding by lazy {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayListDB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
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
        binding.ListView.layoutManager = LinearLayoutManager(this)
        binding.ListView.adapter = adapter
        val callback = CustomItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.ListView)
        adapter.notifyDataSetChanged()
        binding.Play.setOnClickListener {
            onPlayBTNClick(isPlayEnable,it as ImageView)
        }
        binding.Prev.setOnClickListener {
            songList[playPosition].is_playing=false
            playPosition=if (playPosition == 0) songList.size - 1
            else --playPosition
            songList[playPosition].is_playing=true
            setItemClick()
        }
        binding.Next.setOnClickListener {
            songList[playPosition].is_playing=false
            playPosition=if (playPosition == songList.size-1) 0
            else ++playPosition
            songList[playPosition].is_playing=true
            setItemClick()
        }
        binding.setting.setOnClickListener {
            setOnSettingClick(this, PopupMenu(this, binding.setting)) { intent ->
                startActivity(intent)
            }
        }
        mp.setOnCompletionListener {
            if (isPlayEnable) {
                songList[playPosition].is_playing=false
                playPosition=if (playPosition == songList.size-1) 0
                else ++playPosition
                songList[playPosition].is_playing=true
                mpComplete=true
                setItemClick()
            }
        }
        binding.ShowDetail.setOnClickListener { playListDropDown() }
        binding.Seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        binding.addSongToList.setOnClickListener {
            val intent=Intent()
            intent.type = "audio/*"
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            startActivityForResult(intent,12345)
        }

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
        binding.Seekbar.max = mp.duration
        runnable = Runnable {
            binding.tvDue.text = getMinute(mp.duration - mp.currentPosition)
            binding.tvPass.text = getMinute(mp.currentPosition)
            binding.Seekbar.progress = mp.currentPosition
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
        if(currentState || mpComplete){
            mp.start()
            mpComplete=false
            binding.Play.setImageResource(R.drawable.ic_baseline_pause)
        }
        setRunnable()
        setDetail()
        db.setData(tableName, songList)
        adapter.notifyDataSetChanged()

    }

    private fun setDetail() {
        val song = songList[playPosition]
        binding.Title.text = song.title
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
        if (requestCode==12345 && resultCode== RESULT_OK && data != null ){
            if(data.data != null){
                toast("single file")

            }else if(data.clipData != null){
                for(i in 0 until data.clipData!!.itemCount){

                    val song=data.clipData!!.getItemAt(i).uri
                }
            }
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