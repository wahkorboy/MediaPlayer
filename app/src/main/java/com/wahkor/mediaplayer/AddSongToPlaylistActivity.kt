package com.wahkor.mediaplayer

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.adapter.AddSongToPlaylistAdapter
import com.wahkor.mediaplayer.adapter.GroupTouchHelperCallback
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.SelectedSong
import com.wahkor.mediaplayer.model.Song

class AddSongToPlaylistActivity : AppCompatActivity() {
    private lateinit var db: PlayListDB
    private lateinit var allSongs: MutableList<Song>
    private lateinit var adapterList:MutableList<SelectedSong>
    private lateinit var adapter: AddSongToPlaylistAdapter
    private var selectedList:MutableList<Song> = ArrayList()

    private lateinit var recyclerView:RecyclerView
    private lateinit var addBTN:Button
    private lateinit var cancelBTN:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_song_to_playlist)
        val tableName=intent.getStringExtra("tableName")
        recyclerView = findViewById(R.id.addSongRecyclerView)
        addBTN=findViewById(R.id.addSongAddBtn)
        cancelBTN=findViewById(R.id.addSongCancelBtn)
        db = PlayListDB(this)
        allSongs = db.getData("allSong")
        allSongs.sortBy { it.folderPath }
        adapterList= ArrayList()
        for(i in 0 until allSongs.size){
            allSongs[i].is_playing=false
            adapterList.add(SelectedSong(false,allSongs[i]))
        }
        adapter = AddSongToPlaylistAdapter(this,adapterList) { newList ->
            selectedList=ArrayList()
            for(i in 0 until newList.size){
                if (newList[i].isSelected)
                    selectedList.add(newList[i].song)
            }

        }
        val callback=GroupTouchHelperCallback(adapter)
        val itemTouchHelper=ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        cancelBTN.setOnClickListener { onBackPressed() }
        addBTN.setOnClickListener {
            val allTable=db.getName
            if(allTable.contains(tableName) && tableName!= null){
                val playlist=db.getData(tableName)
                for (i in 0 until selectedList.size){
                    playlist.add(selectedList[i])
                }
                db.setData(tableName,playlist)
                val intent= Intent(this,PlayerActivity::class.java)
                intent.putExtra("result","Added")
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                onBackPressed()
            }
        }


    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

}