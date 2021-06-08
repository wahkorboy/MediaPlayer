package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wahkor.mediaplayer.MusicPlayer
import com.wahkor.mediaplayer.database.SleepDb

class SleepTimeReceiver:BroadcastReceiver() {
    private lateinit var db:SleepDb
    private val mp=MusicPlayer()
    override fun onReceive(context: Context?, intent: Intent?) {
        db= SleepDb(context!!)
        val receiverID=intent?.getStringExtra("notificationID")
        val receiverNAME=intent?.getStringExtra("notificationNAME")
        if (receiverNAME != null && receiverNAME=="SleepTime" ){
            val id=db.getSleep.id
            if (receiverID==id.toString()){
                mp.action("stop",context)
                Toast.makeText(context,"Time to Sleep",Toast.LENGTH_LONG).show()
            }
        }

    }
}