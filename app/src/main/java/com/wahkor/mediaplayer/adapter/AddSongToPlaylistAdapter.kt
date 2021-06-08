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
import com.wahkor.mediaplayer.model.SelectedSong
import com.wahkor.mediaplayer.model.Song
import java.util.*

class AddSongToPlaylistAdapter(val toastContent: Context, private var selectedSong:MutableList<SelectedSong>,
                               var callback:(newList:MutableList<SelectedSong>)->Unit)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() ,
CustomItemTouchHelperListener{
    override fun getItemViewType(position: Int): Int {
        return 1
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
        when (position) {
            0 -> {
                val myHolder = holder as SongVH
                myHolder.binding()
            }
            else -> {
                val myHolder = holder as SongVH
                myHolder.binding()

            }
        }

    override fun getItemCount(): Int = selectedSong.size
    inner class FolderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val folderName = itemView.findViewById<TextView>(R.id.groupFolderName)
        private val songName = itemView.findViewById<TextView>(R.id.groupFolderTitle)
        fun binding() {
            folderName.text = selectedSong[adapterPosition].song.folderName
            songName.text = selectedSong[adapterPosition].song.title
            folderName.setOnClickListener {  }

        }

    }

    inner class SongVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val songName = itemView.findViewById<TextView>(R.id.groupSongTitle)
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

}



