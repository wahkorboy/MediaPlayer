package com.wahkor.mediaplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wahkor.mediaplayer.database.PlayerSQL
import com.wahkor.mediaplayer.model.Song
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var db:PlayerSQL
    private val requestAskCode = 1159
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db= PlayerSQL(this)
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
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DISPLAY_NAME
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
                        false,
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item++])),
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        cursor.getInt(cursor.getColumnIndex(columns[item++])),
                        cursor.getLong(cursor.getColumnIndex(columns[item++])),
                        cursor.getString(cursor.getColumnIndex(columns[item]))
                    )
                )
            }
            cursor?.close()
            currentSong= Random.nextInt(0, songList.size-1)
            songList[currentSong].isPlaying=true
            db.setAllSong(songList)
            val intent= Intent(this,TheSongActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onBackPressed() {

    }
}