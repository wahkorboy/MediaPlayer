package com.wahkor.mediaplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.`interface`.CustomItemTouchHelperListener
import com.wahkor.mediaplayer.model.SelectedSong
import java.util.*

class AddSongToPlaylistAdapter(
    private var selectedSong: MutableList<SelectedSong>,
    var callback: (newList: MutableList<SelectedSong>) -> Unit
)
    :RecyclerView.Adapter<AddSongToPlaylistAdapter.SongVH>() ,
CustomItemTouchHelperListener{
    override fun getItemViewType(position: Int): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongVH {
        val layoutInflater = LayoutInflater.from(parent.context)
                val itemView = layoutInflater.inflate(R.layout.play_list_layout, parent, false)
                return SongVH(itemView)



    }


    override fun getItemCount(): Int = selectedSong.size

    inner class SongVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName = itemView.findViewById<TextView>(R.id.playlistTitle)
        fun binding() {
            val song=selectedSong[adapterPosition]
                songName.text = song.song.title
            if(song.isSelected){
                songName.setBackgroundColor(getColor(itemView.context,R.color.selected_playlist))
            }else{
                songName.setBackgroundColor(getColor(itemView.context,R.color.unselected_playlist))

            }
                songName.setOnClickListener {
                    selectedSong[adapterPosition].isSelected=!selectedSong[adapterPosition].isSelected
                    callback(selectedSong)
                    notifyDataSetChanged() }

            }
        }

    override fun onItemMove(fromPosition: Int, ToPosition: Int): Boolean {
        Collections.swap(selectedSong,fromPosition,ToPosition)
        callback(selectedSong)
        notifyItemMoved(fromPosition,ToPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
    }

    override fun onBindViewHolder(holder: SongVH, position: Int) {
        holder.binding()
    }


}



