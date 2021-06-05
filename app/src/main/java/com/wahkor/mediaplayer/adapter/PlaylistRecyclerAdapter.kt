package com.wahkor.mediaplayer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.`interface`.CustomItemTouchHelperListener
import com.wahkor.mediaplayer.model.Song
import java.util.*
import kotlin.collections.ArrayList

class PlaylistRecyclerAdapter(
    myList: ArrayList<Song>,
    var callback: ( newList: ArrayList<Song>) -> Unit
) :
    RecyclerView.Adapter<PlaylistRecyclerAdapter.ViewHolder>(), CustomItemTouchHelperListener {
    val list = myList as MutableList<Song>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.playlistTitle)
        fun bind() {
            itemView.setOnClickListener {
                callback( updateList(adapterPosition))

            }
            val song = list[adapterPosition]
            title.text = song.title
            if (song.is_playing) {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.selected_playlist))
            } else {
                itemView.setBackgroundColor(getColor(itemView.context, R.color.unselected_playlist))


            }
        }

    }
    fun updateList(position: Int):ArrayList<Song>{
        if(list.size==0) return ArrayList()
        var i=0
        while (i<list.size){
            list[i++].is_playing=false
        }
        if(position>list.size-1){
            list[list.size-1].is_playing=true
        }else{
            list[position].is_playing=true

        }
        return list as ArrayList<Song>
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistRecyclerAdapter.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.play_list_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PlaylistRecyclerAdapter.ViewHolder, position: Int) {
        holder.bind()
    }

    override fun onItemMove(fromPosition: Int, ToPosition: Int): Boolean {
        Collections.swap(list, fromPosition, ToPosition)
        notifyItemMoved(fromPosition, ToPosition)
        callback(list as ArrayList<Song>)
        return true

    }

    override fun onItemDismiss(position: Int) {
       // if (list.size > 1) {
            val isCall = list[position].is_playing
            list.removeAt(position)
            if (isCall) {
                callback(updateList(position))

           } //else {
           //     Log.d("last item","last item")
           //     callback(ArrayList())
           // }
       // }
        notifyItemRemoved(position)

    }
}