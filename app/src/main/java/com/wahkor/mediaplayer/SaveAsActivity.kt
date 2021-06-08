package com.wahkor.mediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.databinding.ActivitySaveAsBinding

class SaveAsActivity : AppCompatActivity() {
    private lateinit var adapter:SaveAsRecyclerAdapter
    private lateinit var db:PlayListDB
    private var tableNameList=ArrayList<String>()
    private val binding:ActivitySaveAsBinding by lazy {
        ActivitySaveAsBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        db= PlayListDB(this)
        tableNameList=db.getName
        adapter= SaveAsRecyclerAdapter(tableNameList){ tableName:String ->
            toast(tableName)

        }
        binding.saveAsRecyclerView.layoutManager=LinearLayoutManager(this)
        binding.saveAsRecyclerView.adapter=adapter
        adapter.notifyDataSetChanged()


    }
    class SaveAsRecyclerAdapter(val list:ArrayList<String>,callback:(selected:String) ->Unit):RecyclerView.Adapter<SaveAsRecyclerAdapter.VH>(){
        inner class VH(itemView: View):RecyclerView.ViewHolder(itemView) {
            private val text= itemView.findViewById<TextView>(R.id.groupSongTitle)
            fun bind() {
                text.text=list[adapterPosition]
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.group_playlist_song,null)
            return VH(itemView)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int = list.size

    }
}