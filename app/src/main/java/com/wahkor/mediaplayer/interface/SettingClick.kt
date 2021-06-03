package com.wahkor.mediaplayer.`interface`

import android.app.AlarmManager.RTC_WAKEUP
import android.content.Context
import android.content.Intent
import android.widget.PopupMenu
import android.widget.Toast
import com.wahkor.mediaplayer.R
import com.wahkor.mediaplayer.SleepTimeActivity

interface SettingClick {
    fun setOnSettingClick(context: Context,popupMenu: PopupMenu,callback:(Intent) -> Unit){
        popupMenu.inflate(R.menu.setting)
        popupMenu.setOnMenuItemClickListener {
            when(it.title.toString()){
                "SleepTime" ->{
                    val intent= Intent(context,SleepTimeActivity::class.java)
                    callback(intent)
                }
                else ->
                    Toast.makeText(context,"${it.title}",Toast.LENGTH_LONG).show()
            }
            true
        }
        popupMenu.show()
    }
}