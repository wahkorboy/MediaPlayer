package com.wahkor.mediaplayer

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wahkor.mediaplayer.database.PlayListDB
import com.wahkor.mediaplayer.database.SleepDb
import com.wahkor.mediaplayer.model.Sleep
import com.wahkor.mediaplayer.model.Song
import com.wahkor.mediaplayer.receiver.AudioReceiver
import com.wahkor.mediaplayer.receiver.SleepTimeReceiver
import com.wahkor.mediaplayer.service.BackgroundService
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var db:PlayListDB
    private val requestAskCode = 1159
    private val tableName="allSong"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db= PlayListDB(this)
        // check Permissions
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                requestAskCode
            )
        } else {
            loadMusic()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            requestAskCode -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadMusic()
            } else {
                checkPermissions()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    private fun loadMusic() {
        val columns = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TITLE,
        )

        var songList=ArrayList<Song>()
        val allMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC
        val cursor=contentResolver.query(allMusic,null,selection,null,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                var item=0
                songList.add(
                    Song(
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        false,
                        cursor.getString(cursor.getColumnIndex(columns[item])),
                    )
                )
            }
            cursor?.close()
            val currentSong= Random.nextInt(0, songList.size-1)
            songList[currentSong].is_playing=true
            songList.sortBy { it.folderPath }
            db.setData(tableName,songList)
            sleepTimeSetup()
        }

    }
    private fun sleepTimeSetup(){
        val sleepDb=SleepDb(this)
        val sleep=sleepDb.getSleep
        if (sleep.isRepeat){
            sleep.id=Random.nextInt(1,9999999)
            //update DB and broadcast receiver sleep.id
            sleepDb.setSleep(sleep)
            setAlarm(sleep)
        }
        // setup background music

        val mpService= Intent(this, BackgroundService::class.java)
        startService(mpService)

        val mAudioManager = getSystemService (Context.AUDIO_SERVICE) as AudioManager
        val mReceiverComponent = ComponentName( this, AudioReceiver::class.java)
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);

        val intent= Intent(this,EmptyActivity::class.java)
        //val intent= Intent(this,TestMainActivity::class.java)
          startActivity(intent)
    }

    private fun setAlarm(sleep:Sleep) {
      //  findViewById<TextView>(R.id.mainTextView).text=sleep.id.toString()
        val alarmMGR: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, SleepTimeReceiver::class.java)
        intent.putExtra("notificationID", "${sleep.id}")
        intent.putExtra("notificationNAME", "SleepTime")
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmMGR.setExact(AlarmManager.RTC_WAKEUP, sleep.getRepeatTimeDelay, pendingIntent)

    }
    override fun onBackPressed() {

    }
}


fun Activity.picTime(context: Context, title:String="", callback:(hours:Int, minutes:Int)->Unit){
    Calendar.getInstance().apply {
        val timeSelect= TimePickerDialog(context,0,
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

fun Activity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun getMinute(time: Int): CharSequence {
    var secs = time / 1000
    var minutes = secs / 60
    val hours = minutes / 60
    minutes -= hours * 60
    secs = secs - minutes * 60 - hours * 60 * 60
    return "${if (hours == 0) "" else "$hours:"}${if (minutes < 10) "0$minutes:" else "$minutes:"}${if (secs < 10) "0$secs" else "$secs"}"
}