package com.wahkor.mediaplayer.`interface`

import android.content.Context
import android.content.Intent
import android.widget.PopupMenu
import android.widget.Toast
import com.wahkor.mediaplayer.AddSongToPlaylistActivity
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.SaveAsActivity
import com.wahkor.mediaplayer.SleepTimeActivity

interface MenuInterface {
    fun setOnSettingClick(context: Context,popupMenu: PopupMenu,callback:(Intent) -> Unit){
        popupMenu.inflate(R.menu.setting)
        popupMenu.setOnMenuItemClickListener {
            when(it.title.toString()){
                "SleepTime" ->{
                    val intent= Intent(context,SleepTimeActivity::class.java)
                    callback(intent)
                }
                "AddPlaylist"->{
                    val intent=Intent(context,AddSongToPlaylistActivity::class.java)
                    callback(intent)
                }
                else ->
                    Toast.makeText(context,"${it.title}",Toast.LENGTH_LONG).show()
            }
            true
        }
        popupMenu.show()
    }
    fun setOnMenuClick(context: Context,popupMenu: PopupMenu,tableName:String,callback: (Intent) -> Unit){
        popupMenu.inflate(R.menu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.title.toString()){
                "Add Song to playlist" ->{
                    val intent=Intent(context,AddSongToPlaylistActivity::class.java)
                    intent.putExtra("tableName",tableName)
                    callback(intent)
                }
                "Open playlist"->{
                    Toast.makeText(context,"${it.title}",Toast.LENGTH_LONG).show()
                }
                "Save playlist as" -> {
                    val intent=Intent(context,SaveAsActivity::class.java)
                    intent.putExtra("tableName",tableName)
                    callback(intent)
                }
            }
            true
        }
        popupMenu.show()
    }
}
