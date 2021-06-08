package com.wahkor.mediaplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.`interface`.CustomItemTouchHelperListener
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.model.Song
import java.util.*

class GroupAdapter(val toastContent: Context, allSong:MutableList<Song> ,
                   var callback:(song:Song,returnList:MutableList<Song>,action:String)->Unit)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
CustomItemTouchHelperListener{
    private var playlistQuery = allSong
    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> 1
            playlistQuery[position - 1].folderPath != playlistQuery[position].folderPath -> 1
            else -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                val itemView = layoutInflater.inflate(R.layout.group_playlist_folder, parent, false)
                FolderVH(itemView)


            }
            else -> {
                val itemView = layoutInflater.inflate(R.layout.group_playlist_song, parent, false)
                SongVH(itemView)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        when {
            position == 0 -> {
                val myHolder = holder as SongVH
                myHolder.binding()
            }
            playlistQuery[position - 1].folderPath != playlistQuery[position].folderPath -> {
                val myHolder = holder as SongVH
                myHolder.binding()
            }
            else -> {
                val myHolder = holder as SongVH
                myHolder.binding()

            }
        }

    override fun getItemCount(): Int = playlistQuery.size
    inner class FolderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderName = itemView.findViewById<TextView>(R.id.groupFolderName)
        private val songName = itemView.findViewById<TextView>(R.id.groupFolderTitle)
        fun binding() {
            folderName.text = playlistQuery[adapterPosition].folderName
            songName.text = playlistQuery[adapterPosition].title
            folderName.setOnClickListener {  }

        }

    }

    inner class SongVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName = itemView.findViewById<TextView>(R.id.groupSongTitle)
        fun binding() {
            val song=playlistQuery[adapterPosition]
                songName.text = song.title
            if(song.is_playing){
                songName.setBackgroundColor(getColor(itemView.context,R.color.selected_playlist))
            }else{
                songName.setBackgroundColor(getColor(itemView.context,R.color.unselected_playlist))

            }
                songName.setOnClickListener {
                    for(i in 0 until playlistQuery.size){
                        playlistQuery[i].is_playing=false
                    }
                    playlistQuery[adapterPosition].is_playing=true
                    callback(playlistQuery[adapterPosition],playlistQuery,"ItemClick")
                    notifyDataSetChanged() }

            }
        }

    override fun onItemMove(fromPosition: Int, ToPosition: Int): Boolean {
        Collections.swap(playlistQuery,fromPosition,ToPosition)
        for (i in 0 until playlistQuery.size){
            if (playlistQuery[i].is_playing){
                callback(playlistQuery[i],playlistQuery,"ItemMove")
            }
        }
        notifyItemMoved(fromPosition,ToPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }
}



