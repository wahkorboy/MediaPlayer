package com.wahkor.mediaplayer.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.model.Song


class PlaylistBaseAdapter(val context: Context, val list:ArrayList<Song>): BaseAdapter(){
    override fun getCount(): Int {return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view= View.inflate(context, R.layout.play_list_layout,null)
        val title=view.findViewById<TextView>(R.id.playlistTitle)
        title.text=list[position].TITLE
        return view
    }

}