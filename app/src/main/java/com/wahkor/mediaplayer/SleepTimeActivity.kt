package com.wahkor.mediaplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.*
import androidx.annotation.RequiresApi
import com.wahkor.mediaplayer.database.SleepDb
import com.wahkor.mediaplayer.databinding.ActivitySleepTimeBinding
import com.wahkor.mediaplayer.model.Sleep
import com.wahkor.mediaplayer.receiver.SleepTimeReceiver
import kotlin.random.Random

class SleepTimeActivity : AppCompatActivity() {
    private lateinit var db: SleepDb
    private lateinit var sleep :Sleep
    private val view: ActivitySleepTimeBinding by lazy {
        ActivitySleepTimeBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        db = SleepDb(this)
        initialTimeSleepDB()
        view.sleepTimeText.setOnClickListener {
            picTime(this, "set BedTime ") { hours, minutes ->
                sleep.sleepTime = (hours * 60 + minutes)
                view.sleepTimeText.text = sleep.sleepToString
            }
        }
        view.wakeupTimeText.setOnClickListener {
            picTime(this, "set WakeupTime ") { hours, minutes ->
                sleep.wakeupTime = (hours * 60 + minutes)
                view.wakeupTimeText.text = sleep.wakeupToString
            }

        }
        view.oneTimeSeekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    view.oneTimeTextView.text=(" Sleep after $progress minutes").toString()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        view.submit.setOnClickListener {
            if(view.oneTimeSwitch.isChecked){
                setAlarm(view.oneTimeSeekBar.progress)
            }
        }
        view.cancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initialTimeSleepDB() {

        sleep=db.getSleep
        sleep.repeatTimeId= Random.nextLong(1,999999)
        sleep.oneTimeId=Random.nextLong(1,999999)
        setInitial()
    }

    private fun setInitial() {
        view.sleepTimeText.text = sleep.sleepToString
        view.wakeupTimeText.text = sleep.wakeupToString
        view.repeatSwitch.isChecked = sleep.isRepeat
    }



    private fun setAlarm(minutes:Int) {
            val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, SleepTimeReceiver::class.java)
        intent.putExtra("notificationID", "${sleep.oneTimeId}")
        intent.putExtra("notificationNAME", "SleepTime")
            val pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMGR.setExact(AlarmManager.RTC_WAKEUP, sleep.getOneTimeDelay(minutes), pendingIntent)
        toast("SleepTime in $minutes minutes")

    }

}