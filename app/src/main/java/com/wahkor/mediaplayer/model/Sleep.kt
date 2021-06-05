package com.wahkor.mediaplayer.model

import com.wahkor.mediaplayer.time.TimeManager

data class Sleep(
    var id: Int,
    var isRepeat: Boolean = false,
    var delayTime: Int = 0,
    var sleepTime: Int = 0,
    var wakeupTime: Int = 0
) {
    private val tm = TimeManager()
    val sleepToString: String
        get() {
            val hours = sleepTime / 60
            val minute = sleepTime - hours * 60
            var string = if (hours < 10) "0$hours:" else "$hours:"
            string += if (minute < 10) "0$minute" else "$minute"
            return string
        }
    val wakeupToString: String
        get() {
            val hours = wakeupTime / 60
            val minute = wakeupTime - hours * 60
            var string = if (hours < 10) "0$hours:" else "$hours:"
            string += if (minute < 10) "0$minute" else "$minute"
            return string
        }
    val delayMills: Long get() = System.currentTimeMillis() + (delayTime * 60 * 1000).toLong()
    val initialDelay: Long
        get() {
            var sleepWithIn = 0
            var wakeupWithIn = 0
            tm.convertFromMinute(sleepTime) { hours, minutes ->
                sleepWithIn = tm.getMinuteDifferent(hours, minutes)
            }
            tm.convertFromMinute(wakeupTime) { hours, minutes ->
                wakeupWithIn = tm.getMinuteDifferent(hours, minutes)
            }
            val minutes=if (sleepWithIn < wakeupWithIn) {
                sleepWithIn + delayTime
            } else {
                delayTime
            }
            return (minutes*1000).toLong()

        }
}

