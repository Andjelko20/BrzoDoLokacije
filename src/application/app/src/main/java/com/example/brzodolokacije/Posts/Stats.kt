package com.example.brzodolokacije.Posts

import com.google.gson.annotations.SerializedName

data class Stats(
    @SerializedName("numOfLikes")
    val numOfLikes : String,

    @SerializedName("numOfComments")
    val numOfComments : String
)
