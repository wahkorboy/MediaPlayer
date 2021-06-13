package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.wahkor.mediaplayer.database.SleepDb
import com.wahkor.mediaplayer.service.BackgroundAudioService

class SleepTimeReceiver:BroadcastReceiver() {
    private lateinit var db:SleepDb
    private val mp=BackgroundAudioService()
    override fun onReceive(context: Context?, intent: Intent?) {
        db= SleepDb(context!!)
        val receiverID=intent?.getLongExtra("notificationID",0)
        val receiverNAME=intent?.getStringExtra("notificationNAME")
        receiverNAME?.let { mode ->
            val sleep=db.getSleep
            when(mode){
                "oneTime" -> {
                    receiverID?.let { receiverID->
                        if (receiverID==sleep.oneTimeId){
                            mp.stop()
                            Toast.makeText(context,"set to Sleep for single Time",Toast.LENGTH_LONG).show()
                        }
                    }
                }
                "repeatTime"->{
                    receiverID.let { receiverID->
                        if (receiverID==sleep.repeatTimeId){
                            mp.stop()
                            Toast.makeText(context,"set to Sleep when it rest time",Toast.LENGTH_LONG).show()

                        }
                    }

                }
                else ->{}
            }

        }

    }
}