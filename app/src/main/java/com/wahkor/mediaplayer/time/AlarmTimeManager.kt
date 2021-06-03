package com.wahkor.mediaplayer.time

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime

class TimeManager {
    @RequiresApi(Build.VERSION_CODES.O)
    fun testtime(){
        val now=System.currentTimeMillis()
        val local=LocalTime.now()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinuteDifferent(hours:Int, minutes:Int, callback:(MinuteDiff:Int)->Unit){
        var timeDiff: Int  // in minutes
        val local=LocalTime.now()
        val localHour=local.hour
        val localMinute=local.minute
        timeDiff=(localHour+localMinute*60) - (hours+60*minutes)
        if(timeDiff<0){
            timeDiff+=24*60
        }
        callback(timeDiff)
    }
}