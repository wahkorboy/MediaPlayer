package com.wahkor.mediaplayer.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wahkor.mediaplayer.model.Sleep

class DATABASE(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    companion object{
        const val DATABASE_NAME="MediaPlayer.db"
        const val DATABASE_VER=1
        const val sleepTable="sleepTable"
        private const val col_isRepeat="isRepeat"
        private const val col_delayTime="delayTime"
        private const val col_sleepTime="sleepTime"
        private const val col_wakeupTime="wakeupTime"
        const val sleepTableDataSet="$col_isRepeat integer,$col_delayTime Integer,$col_sleepTime integer,$col_wakeupTime integer"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $sleepTable ( $sleepTableDataSet ) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $sleepTable")
    }
    val getSleep:Sleep
    get() {
        val sleep=Sleep()
        val db=this.writableDatabase
        val cursor=db.rawQuery("select * from $sleepTable",null)
        if (cursor != null){
            while (cursor.moveToNext()){
                sleep.isRepeat=cursor.getInt(cursor.getColumnIndex(col_isRepeat))==1
                sleep.delayTime=cursor.getInt(cursor.getColumnIndex(col_delayTime))
                sleep.sleepTime=cursor.getInt(cursor.getColumnIndex(col_sleepTime))
                sleep.wakeupTime=cursor.getInt(cursor.getColumnIndex(col_wakeupTime))
            }
        }
        cursor?.close()
        db.close()
        return sleep
    }
    fun setSleep(sleep:Sleep){
        val db=this.writableDatabase
    }

}