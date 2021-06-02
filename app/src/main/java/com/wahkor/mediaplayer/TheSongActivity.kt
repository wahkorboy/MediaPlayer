package com.wahkor.mediaplayer

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.wahkor.mediaplayer.adapter.PlaylistBaseAdapter
import com.wahkor.mediaplayer.database.PlayerSQL
import com.wahkor.mediaplayer.databinding.ActivityTheSongBinding
import com.wahkor.mediaplayer.model.Song

class TheSongActivity : AppCompatActivity() {
    private lateinit var adapter:PlaylistBaseAdapter
    private var songlist=ArrayList<Song>()
    private val view:ActivityTheSongBinding by lazy {
        ActivityTheSongBinding.inflate(layoutInflater)
    }
    private lateinit var db: PlayerSQL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        db= PlayerSQL(this)
        songlist=db.allPlayList
        if(songlist.size==0 && SongList.size>0){
            db.addPlaylist(SongList)
            songlist=db.allPlayList
        }
        adapter= PlaylistBaseAdapter(this,songlist)
        view.thesongListView.adapter=adapter
        adapter.notifyDataSetChanged()
        toast("${songlist.size}")
    }
}

fun Activity.toast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}