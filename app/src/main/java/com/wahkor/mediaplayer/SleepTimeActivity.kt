package com.wahkor.mediaplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.wahkor.mediaplayer.databinding.ActivitySleepTimeBinding
import com.wahkor.mediaplayer.receiver.SleepTimeReceiver
import java.util.*

class SleepTimeActivity : AppCompatActivity() {
    private var time:Long=0
    private val view:ActivitySleepTimeBinding by lazy{
        ActivitySleepTimeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        view.manualTime.setOnClickListener{
            setBG(it as Button)
            Calendar.getInstance().apply {
                        TimePickerDialog(
                            this@SleepTimeActivity,
                            0,
                            {_,hourOfDay,minute->
                                this.set(Calendar.HOUR_OF_DAY,hourOfDay)
                                this.set(Calendar.MINUTE,minute)
                                setManualTime(hourOfDay,minute)
                                it.text = setText(hourOfDay,minute)

                            },
                            this.get(Calendar.HOUR_OF_DAY),
                            this.get(Calendar.MINUTE),
                            false

                        ).show()
            }
        }
        view.delay15.setOnClickListener {
            setBG(it as Button)
            setSelectedTime(15)
        }
        view.delay30.setOnClickListener {
            setBG(it as Button)
            setSelectedTime(30)
        }
        view.delay60.setOnClickListener {
            setBG(it as Button)
            setSelectedTime(60)
        }
        view.repeatTime.setOnClickListener {
            view.repeatTimeAfter.isChecked= ! view.repeatTime.isChecked
        }
        view.repeatTimeAfter.setOnClickListener {
            view.repeatTime.isChecked= ! view.repeatTimeAfter.isChecked
        }
    }

    private fun setText(hourOfDay: Int, minute: Int): String {
        var text=""
        text+=if(hourOfDay<10) "0$hourOfDay" else "$hourOfDay"
        text+=":"+ if(minute<10) "0$minute" else "$minute"
        return text
    }

    private fun setBG(button: Button){
        view.delay15.setBackgroundColor(getColor(R.color.unselected_btn))
        view.delay30.setBackgroundColor(getColor(R.color.unselected_btn))
        view.delay60.setBackgroundColor(getColor(R.color.unselected_btn))
        view.manualTime.setBackgroundColor(getColor(R.color.unselected_btn))
        button.setBackgroundColor(getColor(R.color.selected_btn))
    }
    private fun setSelectedTime(minutes:Int) {
        val setTime=System.currentTimeMillis()+minutes*60*1000
        time=setTime
        setRepeatMode("time")

    }
    private fun setManualTime(hours:Int,minutes:Int) {
        val setTime=System.currentTimeMillis()
        time=setTime
        setRepeatMode("day")

    }
    private fun setRepeatMode(mode:String){
        view.everydayLayout.visibility= View.GONE
        view.everytimeLayout.visibility=View.GONE
        view.everyTimeAfterLayout.visibility=View.GONE
        when(mode){
            "time" -> {
                view.repeatEveryday.isChecked=false
                view.everytimeLayout.visibility=View.VISIBLE
                view.everyTimeAfterLayout.visibility=View.VISIBLE
            }
                "day" -> {
                    view.everytimeLayout.visibility=View.VISIBLE
                    view.repeatTime.isChecked=false
                    view.repeatTimeAfter.isChecked=false
                }
        }
    }
    private fun setAlarm(timeInMills:Long){
        val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,SleepTimeReceiver::class.java)
        intent.putExtra("notificationID","SleepTime")
        intent.putExtra("notificationContent","SleepTime")
        val pendingIntent= PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        alarmMGR.setExact(AlarmManager.RTC_WAKEUP,timeInMills,pendingIntent)
    }
}