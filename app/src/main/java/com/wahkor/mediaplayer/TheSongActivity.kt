package com.wahkor.mediaplayer

import android.app.Activity
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.mediaplayer.`interface`.SettingClick
import com.wahkor.mediaplayer.adapter.PlaylistRecyclerAdapter
import com.wahkor.mediaplayer.database.PlayerSQL
import com.wahkor.mediaplayer.databinding.ActivityTheSongBinding
import com.wahkor.mediaplayer.model.Song

class TheSongActivity : AppCompatActivity(),SettingClick{
    private lateinit var runnable: Runnable
    private var handles=Handler()
    private lateinit var mp:MediaPlayer
    private lateinit var adapter: PlaylistRecyclerAdapter
    private var songList=ArrayList<Song>()
    private var playPosition=-1
    private var isPlayEnable=false
    private val view:ActivityTheSongBinding by lazy {
        ActivityTheSongBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayerSQL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        mp= MediaPlayer()
        db= PlayerSQL(this)
        songList=db.getAll("playlist")
        if (songList.size==0){
            songList=db.getAll("AllSong")
           db.add(songList,tableName = "playlist")
        }


        adapter= PlaylistRecyclerAdapter(songList) { position ->
            setItemClick(position)
        }
        view.thesongListView.layoutManager=LinearLayoutManager(this)
       view.thesongListView.adapter=adapter
       adapter.notifyDataSetChanged()
        var position=0
        while (position<songList.size){
            if (songList[position++].isPlaying!!){
                playPosition=position-1
            }
        }
        initial()
        view.thesongPlay.setOnClickListener {
            if (isPlayEnable){
                if (mp.isPlaying){
                    view.thesongPlay.setImageResource(R.drawable.ic_baseline_play)
                    mp.pause()
                }else{
                    mp.start()
                    view.thesongPlay.setImageResource(R.drawable.ic_baseline_pause)
                }
            }
        }
        view.thesongPrev.setOnClickListener {
            val item=if (playPosition==0) songList.size-1
            else --playPosition
            setItemClick(item)
        }
        view.thesongNext.setOnClickListener {
            val item=if(playPosition==songList.size-1) 0
            else ++playPosition
            setItemClick(item)
        }
        view.setting.setOnClickListener {setOnSettingClick(this,PopupMenu(this,view.setting)) }
        mp.setOnCompletionListener {
            if (isPlayEnable){
                val item=if(playPosition==songList.size-1) 0
                else ++playPosition
                setItemClick(item)
            }
        }
        view.thesongShowDetail.setOnClickListener { playListDropDown() }
        view.thesongSeekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(isPlayEnable && mp.isPlaying && fromUser){
                    mp.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
private fun initial(){
    if(playPosition>-1){
        val song=songList[playPosition]
        mp.reset()
        mp.setDataSource(songList[playPosition].DATA)
        mp.reset()
        mp.setDataSource(song.DATA)
        mp.prepare()
        isPlayEnable=true
        adapter.notifyDataSetChanged()
        setRunnable()
        setDetail()
    }
}

    private fun setRunnable() {
        view.thesongSeekbar.max=mp.duration
        runnable= Runnable {
            view.tvDue.text=getMinite(mp.duration-mp.currentPosition)
            view.tvPass.text=getMinite(mp.currentPosition)
            view.thesongSeekbar.progress=mp.currentPosition
            handles.postDelayed(runnable,1000)
        }
        handles.postDelayed(runnable,1000)
    }

    private fun getMinite(time: Int): CharSequence? {
        var secs = time / 1000
        var minutes = secs / 60
        val hours = minutes / 60
        minutes -= hours * 60
        secs = secs - minutes * 60 - hours * 60 * 60
        return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
    }

    private fun setItemClick(position: Int) {
        var time=0
        while (time<songList.size) songList[time++].isPlaying=false
        songList[position].isPlaying=true
        playPosition=position
        initial()
        mp.start()
        view.thesongPlay.setImageResource(R.drawable.ic_baseline_pause)
        db.update(songList,tableName = "playlist")

    }

    private fun setDetail(){
        val song=songList[playPosition]
        view.thesongTitle.text=song.TITLE
        view.thesongDetailName.text=song.TITLE
        view.thesongDetailArtist.text= song.ARTIST
        view.thesongDetailAlbum.text=song.ALBUM
        view.thesongDetailDuration.text= getMinite(mp.duration)
    }
    private fun playListDropDown() {
       val detail=view.thesongDetailLayout
        val icon=view.thesongShowDetail
        if (detail.visibility==View.VISIBLE){
            detail.visibility=View.GONE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }else{
            detail.visibility=View.VISIBLE
            icon.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        }

    }

}
fun Activity.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}