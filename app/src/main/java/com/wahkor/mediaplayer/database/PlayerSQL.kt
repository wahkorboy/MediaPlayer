package com.wahkor.mediaplayer.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wahkor.mediaplayer.model.Song

class PlayerSQL(context: Context) : SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    companion object {
        const val databaseName = "player.db"
        const val databaseVersion = 1
        private const val tablePlaylist = "playlist"
        private const val col_order = "order"
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
        const val CreatePlaylistTable="CREATE TABLE $tablePlaylist(" +
                "$col_order INTEGER ,$col_isPlaying INTEGER ,$col__ID INTEGER,$col_TITLE TEXT," +
                "$col_ARTIST TEXT,$col_DURATION INTEGER,$col_DATA TEXT PRIMARY KEY,$col_ALBUM TEXT,$col_ALBUM_ID INTEGER," +
                "$col_TRACK INTEGER,$col_ARTIST_ID INTEGER,$col_DISPLAY_NAME TEXT" +
                ")"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(CreatePlaylistTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $tablePlaylist")
        onCreate(db)
    }
    fun addPlaylist(list:ArrayList<Song>){
        val db = this.writableDatabase
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                val values=getPlaylistValues(song)
                db.insert(tablePlaylist,null,values)
            }
        }
        db.close()
    }
    fun updatePlaylist(list:ArrayList<Song>){
        val db = this.writableDatabase
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                val values=getPlaylistValues(song)
                db.update(tablePlaylist,values,"$col_DATA=?", arrayOf(song.DATA))
            }
        }
        db.close()
    }
    fun deletePlaylist(list:ArrayList<Song>){
        val db = this.writableDatabase
        if (list.size>0){
            var time=0
            while (time<list.size){
                val song=list[time++]
                db.delete(tablePlaylist,"$col_DATA=?", arrayOf(song.DATA))
            }
        }
        db.close()
    }

    val allPlayList: ArrayList<Song>
        get() {
            val list=ArrayList<Song>()
            val db=this.writableDatabase
            val queryString="SELECT * FROM $tablePlaylist"
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
    fun singlePlayList(uri:String): ArrayList<Song>{
            val list=ArrayList<Song>()
            val db=this.writableDatabase
            val queryString="SELECT * FROM $tablePlaylist WHERE $col_DATA=$uri"
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
        values.put(col_order,song.order)
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
            cursor.getInt(cursor.getColumnIndex(col_order)),
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
}