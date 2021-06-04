package com.wahkor.mediaplayer

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import com.wahkor.mediaplayer.databinding.ActivityTestMainBinding
import com.wahkor.mediaplayer.time.TimeManager
import java.util.*

class TestMainActivity : AppCompatActivity() {
    private val timeManager=TimeManager()
    private val binding:ActivityTestMainBinding by lazy {
        ActivityTestMainBinding.inflate(layoutInflater)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.Submit.setOnClickListener {
            picTime(this){hours, minutes ->
               timeManager.getMinuteDifferent(hours,minutes){
                    binding.Submit.text=it.toString()
                }
            }

        }
    }
}