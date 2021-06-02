package com.wahkor.mediaplayer.model

data class Song(
    var order:Int?=null,
    var isPlaying:Boolean?=null, // play 1 , 0 non

    var _ID:Long?=null,
    var TITLE:String?=null,
    var ARTIST:String?=null,
    var DURATION:Long?=null,
    var DATA:String?=null,
    var ALBUM:String?=null,
    var ALBUM_ID:Long?=null,
    var TRACK:Int?=null,
    var ARTIST_ID:Long?=null,
    var DISPLAY_NAME:String?=null,
)
