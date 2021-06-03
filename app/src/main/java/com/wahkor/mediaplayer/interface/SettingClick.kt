package com.wahkor.mediaplayer.`interface`

import android.content.Context
import android.widget.PopupMenu
import android.widget.Toast
import com.wahkor.mediaplayer.R

interface SettingClick {
    fun setOnSettingClick(context: Context,popupMenu: PopupMenu){
        popupMenu.inflate(R.menu.setting)
        popupMenu.setOnMenuItemClickListener {
            val title=it.title.toString()
            when(title){
                "SleepTime" ->
                    Toast.makeText(context,"go to Sleep setting",Toast.LENGTH_LONG).show()
                else ->
                    Toast.makeText(context,"${it.title}",Toast.LENGTH_LONG).show()
            }
            true
        }
        popupMenu.show()
    }
}