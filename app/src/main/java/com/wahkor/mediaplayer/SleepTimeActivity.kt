package com.wahkor.mediaplayer

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.wahkor.mediaplayer.database.PlayerSQL
import com.wahkor.mediaplayer.databinding.ActivitySleepTimeBinding
import com.wahkor.mediaplayer.model.Sleep
import com.wahkor.mediaplayer.receiver.SleepTimeReceiver
import java.util.*

class SleepTimeActivity : AppCompatActivity() {
    private val tableName="sleepTime"
    private val dataSet="SleepMode TEXT,TimeDelay INTEGER,TimeAfter INTEGER,TimeInDay INTEGER"
    private lateinit var db:PlayerSQL
    private val repeatMode= arrayListOf("Non","Day","Time","TimeAfter")
    private lateinit var sleep:Sleep
    private val view:ActivitySleepTimeBinding by lazy{
        ActivitySleepTimeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        db= PlayerSQL(this)
        initialTimeSleepDB()
        view.manualTime.setOnClickListener{
            setBG(it as Button)
            Calendar.getInstance().apply {
                        TimePickerDialog(
                            this@SleepTimeActivity,
                            0,
                            {_,hourOfDay,minute->
                                this.set(Calendar.HOUR_OF_DAY,hourOfDay)
                                this.set(Calendar.MINUTE,minute)
                                sleep.TimeInDay=(hourOfDay*60*60*100+minute*60*100).toLong()
                                sleep.TimeAfter=0
                                sleep.TimeDelay=0
                                it.text = setText(hourOfDay,minute)
                                if (sleep.SleepMode!=repeatMode[1]){
                                    sleep.SleepMode=repeatMode[0]
                                }
                                setRepeatMode("day")

                            },
                            this.get(Calendar.HOUR_OF_DAY),
                            this.get(Calendar.MINUTE),
                            false

                        ).show()
            }
        }
        view.delay15.setOnClickListener {
            setBG(it as Button)
            if (sleep.SleepMode==repeatMode[1]){
                sleep.SleepMode=repeatMode[0]
            }
            sleep.TimeInDay=0
            sleep.TimeAfter=0
            sleep.TimeDelay=15*60*1000
            setRepeatMode("time")
        }
        view.delay30.setOnClickListener {
            setBG(it as Button)
            if (sleep.SleepMode==repeatMode[1]){
                sleep.SleepMode=repeatMode[0]
            }
            sleep.TimeDelay=30*60*1000
            sleep.TimeInDay=0
            sleep.TimeAfter=0
            setRepeatMode("time")
        }
        view.delay60.setOnClickListener {
            setBG(it as Button)
            if (sleep.SleepMode==repeatMode[1]){
                sleep.SleepMode=repeatMode[0]
            }
            sleep.TimeDelay=60*60*1000
            sleep.TimeInDay=0
            sleep.TimeAfter=0
            setRepeatMode("time")
        }
        view.repeatTime.setOnClickListener {
            if (view.repeatTime.isChecked){
                view.repeatTimeAfter.isChecked=false
                sleep.SleepMode=repeatMode[2]
            }else{
                if(sleep.SleepMode==repeatMode[2]){
                    sleep.SleepMode=repeatMode[0]
                }
            }
        }
        view.repeatTimeAfter.setOnClickListener {
            if (view.repeatTimeAfter.isChecked){
                view.repeatTime.isChecked= false
                sleep.SleepMode=repeatMode[3]

            }else{
                if(sleep.SleepMode==repeatMode[3]){
                    sleep.SleepMode=repeatMode[0]
                }
            }
        }
        view.repeatEveryday.setOnClickListener {
            if(view.repeatEveryday.isChecked){
                sleep.SleepMode=repeatMode[1]
            }else{
                if (sleep.SleepMode==repeatMode[1]){
                    sleep.SleepMode=repeatMode[0]
                }
            }
        }
        view.submit.setOnClickListener {
            updateSleepTimeDB()
            toast("mode ${sleep.SleepMode}," +
                    "${sleep.TimeInDay} ,${sleep.TimeDelay} ,${sleep.TimeAfter} ")
        }
        initialLayout()
    }

    private fun initialLayout() {
        when(sleep.SleepMode){
            repeatMode[1]->{
                view.repeatEveryday.isChecked=true
                var minutes=(sleep.TimeInDay/1000).toInt()
                val hours=minutes/60
                minutes -= hours * 60
                view.manualTime.text=setText(hours,minutes)
                view.manualTime.setBackgroundColor(getColor(R.color.selected_btn))
                setRepeatMode("day")

            }
            repeatMode[2] ->{
                view.repeatTime.isChecked=true
                setRepeatMode("time")
                setBgBtn(sleep.TimeDelay)
            }
            repeatMode[3] ->{
                view.repeatTimeAfter.isChecked=true
                setRepeatMode("time")
                setBgBtn(sleep.TimeDelay)
            }
        }
    }
    private fun setBgBtn(timeInMills: Long){
        val minute=timeInMills/(1000*60)
        when(minute.toInt()){
            15->{
                view.delay15.setBackgroundColor(getColor(R.color.selected_btn))
            }
            30->{
                view.delay30.setBackgroundColor(getColor(R.color.selected_btn))

            }
            60->{
                view.delay60.setBackgroundColor(getColor(R.color.selected_btn))

            }
        }
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
                view.everydayLayout.visibility=View.VISIBLE
                view.repeatTime.isChecked=false
                view.repeatTimeAfter.isChecked=false
            }

        }
    }

    private fun initialTimeSleepDB(){
    val nameListTable=db.getTableName()
        sleep = if(nameListTable.contains(tableName)){
            db.getSleepTimeTable(tableName)
        }else{
            db.create(tableName,dataSet)
            db.getSleepTimeTable(tableName)
        }

}
    private fun updateSleepTimeDB(){
        val values=ContentValues()
        values.put("SleepMode",sleep.SleepMode)
        values.put("TimeInDay",sleep.TimeInDay)
        values.put("TimeDelay",sleep.TimeDelay)
        values.put("TimeAfter",sleep.TimeAfter)
        db.updateSleepTime(tableName,values)
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
    private fun setAlarm(timeInMills:Long){
        val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,SleepTimeReceiver::class.java)
        intent.putExtra("notificationID","SleepTime")
        intent.putExtra("notificationContent","$repeatMode")
        val pendingIntent= PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        alarmMGR.setExact(AlarmManager.RTC_WAKEUP,timeInMills,pendingIntent)
    }
}