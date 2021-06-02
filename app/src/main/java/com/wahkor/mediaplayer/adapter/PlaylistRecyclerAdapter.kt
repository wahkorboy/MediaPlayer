package com.wahkor.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song

class PlaylistRecyclerAdapter(var list: ArrayList<Song>, var adapterOnClick: (Int) -> Unit) :
    RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titlePlaylist: TextView = itemView.findViewById(R.id.playlistTitle)
        fun bind() {
            itemView.setOnClickListener {
                adapterOnClick(adapterPosition)
            }
            titlePlaylist.text = list[adapterPosition].TITLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistRecyclerAdapter.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PlaylistRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind()
    }
}