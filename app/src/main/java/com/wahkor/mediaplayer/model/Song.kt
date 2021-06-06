package com.wahkor.mediaplayer.model

data class Song(
    var album :String,
    var artist:String,
    var data:String,
    var duration:Long,
    var is_playing:Boolean,
    var title:String
)
{
    val folderPath:String
    get() {
        return data.substringBeforeLast("/")
    }
    val folderName:String
        get() {
            val folder = data.substringBeforeLast("/")
            return folder.substringAfterLast("/")
        }
}