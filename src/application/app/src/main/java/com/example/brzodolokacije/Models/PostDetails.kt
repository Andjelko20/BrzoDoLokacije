package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class PostDetails(
    @SerializedName("Id")
    var id: Int,

    @SerializedName("Owner")
    var owner: String,

    @SerializedName("Date")
    var date: Long,

    @SerializedName("Location")
    var location: String,

    @SerializedName("Caption")
    var caption: String,

    @SerializedName("Longitude")
    var longitude: String,

    @SerializedName("Latitude")
    var latitude: String,

    @SerializedName("NumberOfLikes")
    var numberOfLikes: Int,

    @SerializedName("NumberOfComments")
    var numberOfComments: Int,

    @SerializedName("LikedByMe")
    var likedByMe: Boolean
)
