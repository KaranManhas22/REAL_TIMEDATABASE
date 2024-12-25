package com.karan.realtimedatabase

interface Recycler_btn {
    fun update_data(tems: Items,position:Int)
    fun delete_data(items: Items,position: Int)
    fun click(items: Items,position: Int)

}