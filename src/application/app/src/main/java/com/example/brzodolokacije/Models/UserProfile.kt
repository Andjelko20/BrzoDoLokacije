package com.example.brzodolokacije.Models

data class UserProfile(
    var userID: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val desctiption: String,
    val profilePictue: String,
    val numOfFollowers: Int,
    val numOfFollowing: Int,
    val totalNumOfLikes: Int
)
