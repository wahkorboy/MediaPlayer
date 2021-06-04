package com.wahkor.mediaplayer.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wahkor.mediaplayer.model.Sleep
import java.lang.Exception

class SleepDb(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VER) {
    companion object{
        const val DATABASE_NAME="MediaPlayer.db"
        const val DATABASE_VER=1
        const val tableName="sleepTable"
        private const val col_id="id"
        private const val col_isRepeat="isRepeat"
        private const val col_delayTime="delayTime"
        private const val col_sleepTime="sleepTime"
        private const val col_wakeupTime="wakeupTime"
        const val dataSet="$col_id Integer , $col_isRepeat integer,$col_delayTime Integer,$col_sleepTime integer,$col_wakeupTime integer"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("create table $tableName ( $dataSet ) ")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $tableName")
        onCreate(db)
    }
    val getSleep:Sleep
    get() {
        val db=this.writableDatabase
        try {   // create table if not exists
            db.execSQL("create table $tableName(${dataSet})")
        }catch (e: Exception){}

        val sleep=Sleep(0)
        val cursor=db.rawQuery("select * from $tableName",null)
        if (cursor != null){
            while (cursor.moveToNext()){
                sleep.id=cursor.getInt(cursor.getColumnIndex(col_id))
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
        try {   // create table if not exists
            db.execSQL("create table $tableName(${PlayListDB.dataSet})")
        }catch (e:Exception){}
        val values=ContentValues()
        values.put(col_id,sleep.id)
        values.put(col_isRepeat,if(sleep.isRepeat)1 else 0)
        values.put(col_delayTime,sleep.delayTime)
        values.put(col_sleepTime,sleep.sleepTime)
        values.put(col_wakeupTime,sleep.wakeupTime)
        db.delete(tableName,null,null)
        db.insert(tableName,null,values)
        db.close()

    }

}