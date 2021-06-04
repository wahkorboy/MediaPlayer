package com.wahkor.mediaplayer.model

data class Sleep (
    var id:Int,
    var isRepeat:Boolean=false,
    var delayTime:Int=0,
    var sleepTime:Int=0,
    var wakeupTime:Int=0)
{
    val SleeptoString:String
    get(){
        val hours=sleepTime/60
        val minute=sleepTime-hours*60
        var string=if(hours<10)"0$hours:" else "$hours:"
               string+= if(minute<10)"0$minute" else "$minute"
        return string
    }
    val WakeuptoString:String
    get(){
        val hours=wakeupTime/60
        val minute=wakeupTime-hours*60
        var string=if(hours<10)"0$hours:" else "$hours:"
        string+= if(minute<10)"0$minute" else "$minute"
        return string
    }
    val delayMills:Long get() =System.currentTimeMillis()+(delayTime*60*1000).toLong()
}

