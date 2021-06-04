package com.wahkor.mediaplayer

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class ExtensionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extention)
    }
}

fun Activity.picTime(context: Context,title:String="", callback:(hours:Int, minutes:Int)->Unit){
    Calendar.getInstance().apply {
        val timeSelect=TimePickerDialog(context,0,
            {_,hourOfDay,minute ->
                this.set(Calendar.HOUR_OF_DAY,hourOfDay)
                this.set(Calendar.MINUTE,minute)
                callback(hourOfDay,minute)
            },
            this.get(Calendar.HOUR_OF_DAY),
            this.get(Calendar.MINUTE),
            false)
        timeSelect.setTitle(title)
        timeSelect.show()

    }
}

fun Activity.picDate(context: Context, includeTime:Boolean=false, callback:(hours:Int, minutes:Int, dayOfMonth:Int, month:Int, year:Int)->Unit){

    Calendar.getInstance().apply {
        DatePickerDialog(context,0, { _, year, month, dayOfMonth ->
            this.set(Calendar.YEAR,year)
            this.set(Calendar.MONTH,month)
            this.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            if (includeTime){
                picTime(context){hours, minutes ->
                    callback(hours, minutes,dayOfMonth,month,year)
                }
            }else{
                callback(0, 0,dayOfMonth,month,year)
            }
        },
            this.get(Calendar.YEAR),
            this.get(Calendar.MONTH),
            this.get(Calendar.DAY_OF_MONTH)
            ).show()
    }
}