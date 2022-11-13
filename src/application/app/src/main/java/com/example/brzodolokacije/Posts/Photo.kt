package com.example.brzodolokacije.Posts

import java.util.Date

data class Photo (
    val postID : String,
    val path : String,
    val owner : String,
    val dateTime : String, //Long - sekunde da prebacim u datum
    val location : String, //za sad je string, mozda se menja
    val caption : String,
    val numOfLikes : Int,
    val numOfComments : Int
)