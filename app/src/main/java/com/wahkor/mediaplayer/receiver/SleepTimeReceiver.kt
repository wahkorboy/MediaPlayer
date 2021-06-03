package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wahkor.mediaplayer.mp

class SleepTimeReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val repeatModeList= arrayListOf("Non","Day","Time","TimeAfter")
        val message=intent?.getStringExtra("notificationID")
        val content=intent?.getStringExtra("notificationContent")
        if (message != null && message=="SleepTime" && repeatModeList.contains(content)){
            mp.pause()
           Toast.makeText(context,"Time to Sleep with $content Mode",Toast.LENGTH_LONG).show()
        }

    }
}