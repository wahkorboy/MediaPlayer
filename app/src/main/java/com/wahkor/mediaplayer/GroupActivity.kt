package com.wahkor.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.adapter.GroupAdapter
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song

class GroupActivity : AppCompatActivity() {
    private lateinit var db:PlayListDB
    private lateinit var allSongs:MutableList<Song>
    private lateinit var adapter:GroupAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        db= PlayListDB(this)
        allSongs= db.getData("allSong")
        allSongs.sortBy { it.folderPath }
        adapter= GroupAdapter(this,allSongs )
        val listView=findViewById<RecyclerView>(R.id.groupRecyclerView)
        listView.layoutManager=LinearLayoutManager(this)
        listView.adapter=adapter


    }
}