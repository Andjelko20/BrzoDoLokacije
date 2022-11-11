package com.example.brzodolokacije.Models

data class UserProfile(
    val username: String,
    val name: String,
    val description: String,
    val profilePicture: String,
    val numOfFollowers: Int,
    val numOfFollowing: Int,
    val totalNumOfLikes: Int,
    val numOfPosts: Int
)
