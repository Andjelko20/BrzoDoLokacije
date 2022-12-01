package com.example.brzodolokacije.Posts

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Photo (
    @SerializedName("Id")
    val id : String,

    @SerializedName("Owner")
    val owner : String,

    @SerializedName("Date")
    val date : Long, //Long - sekunde da prebacim u datum

    @SerializedName("Location")
    val location : String, //za sad je string, mozda se menja

    @SerializedName("Caption")
    val caption : String,

    @SerializedName("NumberOfLikes")
    var numberOfLikes : Int,

    @SerializedName("NumberOfComments")
    var numberOfComments : Int,

    @SerializedName("LikedByMe")
    var likedByMe : Boolean
)