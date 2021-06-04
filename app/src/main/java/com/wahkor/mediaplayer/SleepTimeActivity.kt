package com.wahkor.mediaplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
                view.sleepTimeText.text = sleep.SleeptoString
            }
        }
        view.wakeupTimeText.setOnClickListener {
            picTime(this, "set WakeupTime ") { hours, minutes ->
                sleep.wakeupTime = (hours * 60 + minutes)
                view.wakeupTimeText.text = sleep.WakeuptoString
            }

        }
        view.delayNaver.setOnClickListener {
            setBG(it as Button)
            sleep.delayTime = 0
            view.repeatSwitch.isChecked = false
        }
        view.delay15.setOnClickListener {
            setBG(it as Button)
            sleep.delayTime = 15
        }
        view.delay30.setOnClickListener {
            setBG(it as Button)
            sleep.delayTime = 30
        }
        view.delay60.setOnClickListener {
            setBG(it as Button)
            sleep.delayTime = 60
        }
        view.repeatSwitch.setOnClickListener {
            sleep.isRepeat=view.repeatSwitch.isChecked
            if (view.repeatSwitch.isChecked){
                view.repeatSwitch.isChecked = sleep.delayTime > 0
                sleep.isRepeat=view.repeatSwitch.isChecked

            }
        }
        view.submit.setOnClickListener {
                db.setSleep(sleep)

            if (sleep.delayTime>0){
                setAlarm()
            }
            onBackPressed()
        }
        view.cancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initialTimeSleepDB() {

        sleep=db.getSleep
        sleep.id= Random.nextInt(1,999999)
        setInitial()
    }

    private fun setInitial() {
        view.sleepTimeText.text = sleep.SleeptoString
        view.wakeupTimeText.text = sleep.WakeuptoString
        view.repeatSwitch.isChecked = sleep.isRepeat
        when (sleep.delayTime) {
            0 -> setBG(view.delayNaver)
            15 -> setBG(view.delay15)
            30 -> setBG(view.delay30)
            60 -> setBG(view.delay60)
        }
    }


    private fun setBG(button: Button) {
        view.delayNaver.setBackgroundColor(getColor(R.color.unselected_btn))
        view.delay15.setBackgroundColor(getColor(R.color.unselected_btn))
        view.delay30.setBackgroundColor(getColor(R.color.unselected_btn))
        view.delay60.setBackgroundColor(getColor(R.color.unselected_btn))
        button.setBackgroundColor(getColor(R.color.selected_btn))
    }

    private fun setAlarm() {
            val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, SleepTimeReceiver::class.java)
        intent.putExtra("notificationID", "${sleep.id}")
        intent.putExtra("notificationNAME", "SleepTime")
            val pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMGR.setExact(AlarmManager.RTC_WAKEUP, sleep.delayMills, pendingIntent)
        toast("SleepTime in ${sleep.delayTime} minutes")

    }

}