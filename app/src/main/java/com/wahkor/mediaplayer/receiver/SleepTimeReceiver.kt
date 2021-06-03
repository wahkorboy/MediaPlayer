package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class SleepTimeReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message=intent?.getStringExtra("notificationID")
        if (message != null && message=="SleepTime"){
           Toast.makeText(context,"Time to Sleep",Toast.LENGTH_LONG).show()
        }

    }
}