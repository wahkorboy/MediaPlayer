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
    private val view:ActivityTheSongBinding by lazy {
        ActivityTheSongBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayerSQL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        mp= MediaPlayer()
        db= PlayerSQL(this)
        songList=db.allPlayList
        if(songList.size==0 && SongList.size>0){
            db.addPlaylist(SongList)
            songList=db.allPlayList
        }
        adapter= PlaylistRecyclerAdapter(songList) { position ->
            setItemClick(position)
        }
        view.thesongListView.layoutManager=LinearLayoutManager(this)
       view.thesongListView.adapter=adapter
       adapter.notifyDataSetChanged()
    }

    private fun setItemClick(position: Int) {
        mp.reset()
        mp.setDataSource(songList[position].DATA)
        mp.prepare()
        mp.start()

    }


}
fun Activity.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}