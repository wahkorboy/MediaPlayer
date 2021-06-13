package com.wahkor.mediaplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private lateinit var backIntent:Intent
    private val view: ActivitySleepTimeBinding by lazy {
        ActivitySleepTimeBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        db = SleepDb(this)
        backIntent=Intent(this,MusicPlayerActivity::class.java)
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
                    view.oneTimeTextView.text=("$progress Min")
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        view.repeatTimeSeekBar.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    view.repeatTimeTextView.text=("$progress Min")
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
            sleep.isRepeat=true
            sleep.delayTime=view.repeatTimeSeekBar.progress
            db.setSleep(sleep)
            if(view.repeatSwitch.isChecked){
                setAlarm(0,true)
            }
            startActivity(backIntent)
        }
        view.cancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initialTimeSleepDB() {

        sleep=db.getSleep
        sleep.repeatTimeId= Random.nextLong(1,9999999999)
        sleep.oneTimeId=Random.nextLong(1,9999999999)
        setInitial()
    }

    private fun setInitial() {
        view.sleepTimeText.text = sleep.sleepToString
        view.wakeupTimeText.text = sleep.wakeupToString
        view.repeatSwitch.isChecked = sleep.isRepeat
        view.repeatTimeSeekBar.progress=sleep.delayTime
        view.repeatTimeTextView.text=("${sleep.delayTime} Min")
    }



    private fun setAlarm(minutes:Int,isRepeat:Boolean=false) {
        var delayTime=sleep.getRealDelay(minutes)
        var id=sleep.oneTimeId
        var name="oneTime"
        if (isRepeat){
            delayTime=sleep.getRepeatTimeDelay
            id=sleep.repeatTimeId
            name="repeatTime"
        }
            val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, SleepTimeReceiver::class.java)
        intent.putExtra("notificationID", id)
        intent.putExtra("notificationNAME", name)
            val pendingIntent =
                PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMGR.setExact(AlarmManager.RTC_WAKEUP,delayTime, pendingIntent)
        if(!isRepeat){
            toast("SleepTime in $minutes minutes")
        }

    }

}