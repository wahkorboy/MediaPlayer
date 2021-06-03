package com.wahkor.mediaplayer.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wahkor.mediaplayer.model.Sleep
import com.wahkor.mediaplayer.model.Song


class PlayerSQL(context: Context) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {


    companion object {
        const val databaseName = "player001.db"
        const val databaseVersion = 1
        private const val TABLE_NAME = "playlist"
        private const val col_isPlaying = "is_playing"
        private const val col__ID = "id"
        private const val col_TITLE = "title"
        private const val col_ARTIST = "artist"
        private const val col_DURATION = "duration"
        private const val col_DATA = "data"
        private const val col_ALBUM = "album"
        private const val col_ALBUM_ID = "album_id"
        private const val col_TRACK = "track"
        private const val col_ARTIST_ID = "artist_id"
        private const val col_DISPLAY_NAME = "display_name"
        private const val AllSong="AllSong"
        private const val playlist="$col_TITLE TEXT , $col_DATA TEXT"
        private const val songDb=" $col_isPlaying INTEGER , $col__ID INTEGER , $col_TITLE TEXT ,  $col_ARTIST TEXT , $col_DURATION INTEGER , $col_DATA TEXT , $col_ALBUM TEXT , $col_ALBUM_ID INTEGER , $col_TRACK INTEGER , $col_ARTIST_ID INTEGER , $col_DISPLAY_NAME TEXT "
        private const val CreatePlaylistTable="create table $TABLE_NAME($songDb)"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CreatePlaylistTable)
        db!!.execSQL("create table $AllSong($songDb)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db!!.execSQL("DROP TABLE IF EXISTS $AllSong")
        onCreate(db)
    }
    fun setAllSong(list:ArrayList<Song>){
        val nameList=getTableName()
        var isExist=false
        for (item in nameList){
            if (item== AllSong) isExist=true
        }
        if (!isExist) {
            val db=this.writableDatabase
            db.execSQL("create table $AllSong($songDb)")
            db.close()
        }
        val oldSong = getAll(AllSong)
        delete(oldSong, tableName = AllSong)
        add(list, tableName = AllSong)

    }

    fun getTableName():ArrayList<String>{
        val db=this.writableDatabase
        val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        val list=ArrayList<String>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursor.getString(cursor.getColumnIndex("name")))
            }
        }
        return list
    }
    fun create(tableName: String,dataSet:String,values:ContentValues?=null){
        val db=this.writableDatabase
        try {
            db.execSQL("create table $tableName($dataSet)")
            if (values != null){
                db.insert(tableName,null,values)
                db.close()
            }
        }catch (E:Exception) {
            E.toString()
        }
        db.close()
    }
    fun add(list:ArrayList<Song>, tableName: String= TABLE_NAME){

        val db = this.writableDatabase!!
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                val values=getPlaylistValues(song)
                db.insert(tableName,null,values)
            }
        }
        db.close()
    }
    fun update(list:ArrayList<Song>, tableName: String= TABLE_NAME){
        val db = this.writableDatabase
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                val values=getPlaylistValues(song)
                db.update(tableName,values,"$col_DATA=?", arrayOf(song.DATA))
            }
        }
        db.close()
    }
    fun delete(list:ArrayList<Song>, tableName:String= TABLE_NAME){
        val db = this.writableDatabase
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                db.delete(tableName,"$col_DATA=?", arrayOf(song.DATA))
            }
        }
        db.close()
    }
    fun getAll(tableName: String): ArrayList<Song>{
            val list=ArrayList<Song>()
            val db=this.writableDatabase
            val queryString="SELECT * FROM $tableName"
            val cursor=db.rawQuery(queryString,null)
            if(cursor != null){
                while (cursor.moveToNext()){
                    list.add(getSong(cursor))
                }
                cursor.close()

            }

            return list
        }
    fun singlePlayList(uri:String): ArrayList<Song>{
        val list=ArrayList<Song>()
        val db=this.writableDatabase
        val queryString="SELECT * FROM $TABLE_NAME WHERE $col_DATA=$uri"
        val cursor=db.rawQuery(queryString,null)
        if(cursor != null){
            while (cursor.moveToNext()){
                list.add(
                    getSong(cursor)
                )
            }
            cursor.close()

        }

        return list
    }
    private fun getPlaylistValues(song:Song):ContentValues{
        val values=ContentValues()
        values.put(col_isPlaying,if(song.isPlaying!!) 1 else 0)
        values.put(col__ID,song._ID)
        values.put(col_TITLE,song.TITLE)
        values.put(col_ARTIST,song.ARTIST)
        values.put(col_DURATION,song.DURATION)
        values.put(col_DATA,song.DATA)
        values.put(col_ALBUM,song.ALBUM)
        values.put(col_ALBUM_ID,song.ALBUM_ID)
        values.put(col_TRACK,song.TRACK)
        values.put(col_ARTIST_ID,song.ARTIST_ID)
        values.put(col_DISPLAY_NAME,song.DISPLAY_NAME)
        return values
    }
    private fun getSong(cursor:Cursor):Song{
        return Song(
            cursor.getInt(cursor.getColumnIndex(col_isPlaying))==1,
            cursor.getLong(cursor.getColumnIndex(col__ID)),
            cursor.getString(cursor.getColumnIndex(col_TITLE)),
            cursor.getString(cursor.getColumnIndex(col_ARTIST)),
            cursor.getLong(cursor.getColumnIndex(col_DURATION)),
            cursor.getString(cursor.getColumnIndex(col_DATA)),
            cursor.getString(cursor.getColumnIndex(col_ALBUM)),
            cursor.getLong(cursor.getColumnIndex(col_ALBUM_ID)),
            cursor.getInt(cursor.getColumnIndex(col_TRACK)),
            cursor.getLong(cursor.getColumnIndex(col_ARTIST_ID)),
            cursor.getString(cursor.getColumnIndex(col_DISPLAY_NAME)))

    }
    fun updateSleepTime(tableName: String,values: ContentValues){
        val db=this.writableDatabase
        db.delete(tableName,null,null)
        db.insert(tableName,null,values)
        db.close()

    }
    fun getSleepTimeTable(tableName:String): Sleep {
        var sleep= Sleep("Non",0,0,0)
        val db=this.writableDatabase
        val cursor=db.rawQuery("Select * from $tableName",null)
        if(cursor != null){
            while (cursor.moveToNext()){
                sleep= Sleep(
                    cursor.getString(cursor.getColumnIndex("SleepMode")),
                    cursor.getLong(cursor.getColumnIndex("TimeInDay")),
                    cursor.getLong(cursor.getColumnIndex("TimeDelay")),
                    cursor.getLong(cursor.getColumnIndex("TimeAfter"))
                )
            }
        }
        return sleep
    }
}