package com.wahkor.mediaplayer.model

data class Song(
    var album :String="",
    var artist:String="",
    var data:String="",
    var duration:Long=0,
    var is_playing:Boolean=false,
    var title:String=""
)
