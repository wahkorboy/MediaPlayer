package com.wahkor.mediaplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wahkor.mediaplayer.service.AudioService


class AudioBecomingNoisyReceiver: BroadcastReceiver() {
    private var mp=AudioService()
    override fun onReceive(context: Context?, intent: Intent?) {
       /* mp.pause()*/
    }
}