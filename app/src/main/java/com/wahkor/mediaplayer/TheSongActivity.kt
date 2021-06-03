package com.wahkor.mediaplayer

import android.app.Activity
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wahkor.mediaplayer.adapter.PlaylistRecyclerAdapter
import com.wahkor.mediaplayer.database.PlayerSQL
import com.wahkor.mediaplayer.databinding.ActivityTheSongBinding
import com.wahkor.mediaplayer.model.Song

class TheSongActivity : AppCompatActivity(){
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
        view.thesongTitle.text=song.TITLE
    }
}
    private fun setItemClick(position: Int) {
        var time=0
        while (time<songList.size) songList[time++].isPlaying=false
        songList[position].isPlaying=true
        adapter.notifyDataSetChanged()
        playPosition=position
        initial()
        mp.start()
        view.thesongPlay.setImageResource(R.drawable.ic_baseline_pause)
        db.update(songList,tableName = "playlist")

    }


}
fun Activity.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}