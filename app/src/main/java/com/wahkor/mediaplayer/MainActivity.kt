package com.wahkor.mediaplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.wahkor.mediaplayer.model.TrackFile
import kotlin.random.Random

var TrackFileList=ArrayList<TrackFile>()
class MainActivity : AppCompatActivity() {
    private val requestAskCode = 1159
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        TrackFileList.clear()
        val allMusic = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection=MediaStore.Audio.Media.IS_MUSIC
        val cursor=contentResolver.query(allMusic,null,selection,null,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                TrackFileList.add(
                    TrackFile(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)),

                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    )
                )
            }
        }
        cursor?.close()
        mediaPlayer = MediaPlayer()
        val position= Random.nextInt(0, TrackFileList.size-1)
        currentTrack.track= TrackFileList[position]
        mediaPlayer.setDataSource(currentTrack.track.Uri)
        mediaPlayer.prepare()
        val intent=Intent(this,PlayerActivity::class.java)
        startActivity(intent)

    }

    override fun onBackPressed() {

    }
}