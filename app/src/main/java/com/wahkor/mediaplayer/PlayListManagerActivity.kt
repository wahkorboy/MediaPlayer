package com.wahkor.mediaplayer

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.PlaylistStatusDb
import com.wahkor.mediaplayer.databinding.ActivityPlaylistManagerBinding
import com.wahkor.mediaplayer.service.BackgroundService

class PlayListManagerActivity : AppCompatActivity() {
    private lateinit var adapter: SaveAsRecyclerAdapter
    private lateinit var db: PlayListDB
    private lateinit var statusDb: PlaylistStatusDb
    private var myList = ArrayList<MyList>()
    private var tableNameList = ArrayList<String>()
    private var openTable=""
    private lateinit var backIntent:Intent
    private val mp=BackgroundService()
    private val binding: ActivityPlaylistManagerBinding by lazy {
        ActivityPlaylistManagerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusDb=PlaylistStatusDb(this)
        backIntent = Intent(this, EmptyActivity::class.java)
        setContentView(binding.root)
        db = PlayListDB(this)
        tableNameList = db.getName
        for (i in 0 until tableNameList.size) {
            myList.add(MyList(false, tableNameList[i]))
        }
        adapter = SaveAsRecyclerAdapter(myList)
        binding.saveAsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.saveAsRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        val mode=intent.getStringExtra("saveAsMode")

        when(mode){
            "open" -> {
                binding.saveAsName.visibility=View.GONE
                binding.saveAsOkBTN.text="Open"
            }
        }
        binding.saveAsOkBTN.setOnClickListener {
            when(mode){
                "save" -> createTable()
                "open" -> setTable()
            }
        }
        binding.saveAsDelBTN.setOnClickListener {
            deleteClick()
        }
        binding.saveAsCancelBTN.setOnClickListener { onBackPressed() }
    }

    private fun deleteClick() {
        confirmDialog("Are you sure you want to Delete $openTable playlist"){confirm ->
            if(confirm){
                val tableName="playlist_$openTable"
                tableNameList=db.deleteTable(tableName)
                myList.clear()
                for(i in 0 until tableNameList.size){
                    myList.add(MyList(false,tableNameList[i]))
                }
                statusDb.setTableName("playlist_default")
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setTable() {
        if(tableNameList.contains(openTable)){
            val saveTable="playlist_$openTable"
            statusDb.setTableName(saveTable)
            mp.changePlaylist(saveTable)
            startActivity(backIntent)
        }else{
            toast("Please Select list")
        }
    }

    private fun createTable() {
        val name = binding.saveAsName.text.toString()
        if (name.length < 3 || name.length > 15) {
            toast("Playlist name needed 3-15 character")
        } else {
            if (tableNameList.contains(name)) {
                //alert for confirmation
                confirmDialog("Are you sure You want ot override $name playlist") { confirm ->
                    if (confirm) {
                        gotoSave(name)
                    }
                }
            } else {
                gotoSave(name)
            }
        }
    }

    private fun gotoSave(name: String) {
        val tableName="playlist_$name"
        val currentTable = statusDb.getTableName
            val list = db.getData(currentTable!!)
            if (tableNameList.contains(name)) {
                db.setData(tableName, list)
            } else {
                db.createTable(tableName, list)
            }
        statusDb.setTableName(tableName)
        mp.changePlaylist(tableName)
            startActivity(backIntent)

    }

    private fun confirmDialog(message: String, callback: (confirm: Boolean) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                run {
                    callback(true)
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                run {
                    callback(false)
                }
            }
        builder.create().show()
    }

    override fun onBackPressed() {
        val intent = Intent(this, PlayerActivity::class.java)
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    data class MyList(var isSelected: Boolean, val name: String)
    inner class SaveAsRecyclerAdapter(val list: ArrayList<MyList>) :
        RecyclerView.Adapter<SaveAsRecyclerAdapter.VH>() {
        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text = itemView.findViewById<TextView>(R.id.saveAsAdapterName)
            fun bind() {
                text.text = list[adapterPosition].name
                if (list[adapterPosition].isSelected) {
                    itemView.setBackgroundColor(getColor(itemView.context, R.color.selected_playlist))



                } else {
                    itemView.setBackgroundColor(getColor(itemView.context, R.color.unselected_playlist))


                }
                itemView.setOnClickListener {
                    list[adapterPosition].isSelected = !list[adapterPosition].isSelected
                    openTable = if (list[adapterPosition].isSelected) {
                        binding.saveAsName.setText(list[adapterPosition].name)
                        for(i in 0 until list.size){
                            list[i].isSelected=false
                        }
                        list[adapterPosition].isSelected=true
                        list[adapterPosition].name
                    }else{
                        ""
                    }
                    binding.saveAsDelBTN.visibility=View.GONE
                    if(openTable != "default" && openTable != ""){
                        binding.saveAsDelBTN.visibility=View.VISIBLE
                    }

                    notifyDataSetChanged()

                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val itemView: View =
                LayoutInflater.from(parent.context).inflate(R.layout.save_as_adapter_layout, null)
            return VH(itemView)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind()
        }

        override fun getItemCount(): Int = list.size

    }
}