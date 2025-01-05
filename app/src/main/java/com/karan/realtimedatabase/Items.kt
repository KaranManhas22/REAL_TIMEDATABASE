package com.karan.realtimedatabase

import com.google.firebase.database.Exclude

data class Items(
    var id:String?="",
    var name:String?="",
    var Etclass:String?="",
    var number:Int?=0,
    var ETimg:String?=""
){

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "class" to Etclass,
            "number" to number,


            )

    }
}
