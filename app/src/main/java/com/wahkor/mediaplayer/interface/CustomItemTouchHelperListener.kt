package com.wahkor.mediaplayer.`interface`

interface CustomItemTouchHelperListener {
    fun onItemMove(fromPosition:Int,ToPosition:Int):Boolean
    fun onItemDismiss(position: Int)
}