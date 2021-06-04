package com.wahkor.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song

class PlaylistRecyclerAdapter(var list: ArrayList<Song>, var callback: (Int) -> Unit) :
    RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.playlistTitle)
        fun bind() {
            itemView.setOnClickListener {
                callback(adapterPosition)

            }
            val song=list[adapterPosition]
            title.text =song.title
            if (song.is_playing!!){
                itemView.setBackgroundColor(getColor(itemView.context,R.color.selected_playlist))
            }else{
                itemView.setBackgroundColor(getColor(itemView.context,R.color.unselected_playlist))


            }
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