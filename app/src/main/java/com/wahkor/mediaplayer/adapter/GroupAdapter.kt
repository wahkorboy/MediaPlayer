package com.wahkor.mediaplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song

class GroupAdapter(val toastContent: Context,val allSong:MutableList<Song> ):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var playlistQuery = allSong
    private var colorSelect=1
    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> 0
            playlistQuery[position - 1].folderPath != playlistQuery[position].folderPath -> 0
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
                val myHolder = holder as FolderVH
                myHolder.binding()
            }
            playlistQuery[position - 1].folderPath != playlistQuery[position].folderPath -> {
                val myHolder = holder as FolderVH
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
            when(colorSelect){
                1 ->{
                    itemView.setBackgroundColor(getColor(itemView.context,R.color.playlist_group_2))
                    colorSelect=2
                }
                2 ->{
                    itemView.setBackgroundColor(getColor(itemView.context,R.color.playlist_group_1))
                    colorSelect=1
                }
            }

        }

    }

    inner class SongVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName = itemView.findViewById<TextView>(R.id.groupSongTitle)
        fun binding() {
                songName.text = playlistQuery[adapterPosition].title
            when(colorSelect){
                1 ->{
                    itemView.setBackgroundColor(getColor(itemView.context,R.color.playlist_group_1))
                }
                2 ->{
                    itemView.setBackgroundColor(getColor(itemView.context,R.color.playlist_group_2))
                }
            }
            }
        }
    }

