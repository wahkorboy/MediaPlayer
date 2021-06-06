package com.wahkor.mediaplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song

class GroupAdapter(val toastContent: Context, allSong:MutableList<Song>):RecyclerView.Adapter<GroupAdapter.VH>() {
    private var list:MutableList<Song> = allSong
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView=LayoutInflater.from(parent.context).inflate(R.layout.group_adapter_layout,parent,false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int =list.size
    inner class VH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val textView=itemView.findViewById<TextView>(R.id.groupAdapterText)
        fun bind() {
            val text=list[adapterPosition].folderName + "   -->    " + list[adapterPosition].title
            textView.text=text
            itemView.setOnClickListener {
                Toast.makeText(toastContent, list[adapterPosition].folderName,Toast.LENGTH_SHORT).show()
            }
        }

    }
}