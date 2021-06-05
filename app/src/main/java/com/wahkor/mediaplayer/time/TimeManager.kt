package com.wahkor.mediaplayer.time

import java.util.*

class TimeManager {
    fun getMinuteDifferent(hours:Int, minutes:Int):Int{
        var timeDiff: Int  // in minutes
        val calendar= Calendar.getInstance()
        val localHour=calendar.get(Calendar.HOUR_OF_DAY)
        val localMinute=calendar.get(Calendar.MINUTE)
        timeDiff=(hours*60+minutes)-(localHour*60+localMinute)
        if(timeDiff<0){
            timeDiff+=24*60
        }
        return timeDiff
    }
    fun convertFromMinute(diffMinute:Int,callback:(hours:Int,minutes:Int) -> Unit){
        var minutes=diffMinute
        val hours=minutes/60
        minutes -= hours * 60
        callback(hours,minutes)
    }
    fun localDate(callback:(year:Int,month:Int,day:Int,hour:Int,minute:Int)->Unit){

        val calendar= Calendar.getInstance()
        val year=calendar.get(Calendar.YEAR)
        val month=calendar.get(Calendar.MONTH)
        val day=calendar.get(Calendar.DAY_OF_MONTH)
        val hour=calendar.get(Calendar.HOUR_OF_DAY)
        val minute=calendar.get(Calendar.MINUTE)
        callback(year, month, day, hour, minute)
    }
}