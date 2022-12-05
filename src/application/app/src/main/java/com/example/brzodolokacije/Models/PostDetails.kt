package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class PostDetails(
    @SerializedName("Id")
    val id: Int,

    @SerializedName("Owner")
    val owner: String,

    @SerializedName("Date")
    val date: Long,

    @SerializedName("Location")
    val location: String,

    @SerializedName("Caption")
    val caption: String,

    @SerializedName("Longitude")
    val longitude: String,

    @SerializedName("Latitude")
    val latitude: String,

    @SerializedName("NumberOfLikes")
    val numberOfLikes: Int,

    @SerializedName("NumberOfComments")
    val numberOfComments: Int,

    @SerializedName("LikedByMe")
    val likedByMe: Boolean
)
