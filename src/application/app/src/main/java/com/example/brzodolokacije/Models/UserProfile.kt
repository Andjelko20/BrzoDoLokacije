package com.example.brzodolokacije.Models

data class UserProfile(
    val userID: Int,
    val username: String,
    val firstname: String,
    val lastname: String,
    val description: String,
    val profilePicture: String,
    val numOfFollowers: Int,
    val numOfFollowing: Int,
    val totalNumOfLikes: Int
)
