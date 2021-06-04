package com.wahkor.mediaplayer.time

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime

class TimeManager {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinuteDifferent(hours:Int, minutes:Int, callback:(MinuteDiff:Int)->Unit){
        var timeDiff: Int  // in minutes
        val local=LocalTime.now()
        val localHour=local.hour
        val localMinute=local.minute
        timeDiff=(hours*60+minutes)-(localHour*60+localMinute)
        if(timeDiff<0){
            timeDiff+=24*60
        }
        callback(timeDiff)
    }
    fun convertFromMinute(diffMinute:Int,callback:(hours:Int,minutes:Int) -> Unit){
        var minutes=diffMinute
        val hours=minutes/60
        minutes -= hours * 60
        callback(hours,minutes)
    }
}