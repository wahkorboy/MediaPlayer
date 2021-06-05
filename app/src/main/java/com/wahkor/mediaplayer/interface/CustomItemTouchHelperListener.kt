package com.wahkor.mediaplayer.`interface`

import java.text.FieldPosition

interface CustomItemTouchHelperListener {
    fun onItemMove(fromPosition:Int,ToPosition:Int):Boolean
    fun onItemDismiss(position: Int)
}