package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class UserProfileVisit(
    @SerializedName("Username")
    val username: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Description")
    val description: String,

    @SerializedName("Avatar")
    val profilePicture: String,

    @SerializedName("Followers")
    val numOfFollowers: Int,

    @SerializedName("Following")
    val numOfFollowing: Int,

    @SerializedName("NumberOfLikes")
    val totalNumOfLikes: Int,

    @SerializedName("NumberOfPosts")
    val numOfPosts: Int,

    @SerializedName("IsFollowed")
    val isFollowed: Boolean
)
