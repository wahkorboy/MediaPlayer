package com.wahkor.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song


class PlayListAdapter(private var playlistList: ArrayList<Song>): RecyclerView.Adapter<PlayListAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout,parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song=playlistList[position]
        holder.binding(song)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titlePlaylist: TextView =itemView.findViewById(R.id.playlistTitle)
        fun binding(track: Song){
            titlePlaylist.text=track.TITLE
        }

    }
}