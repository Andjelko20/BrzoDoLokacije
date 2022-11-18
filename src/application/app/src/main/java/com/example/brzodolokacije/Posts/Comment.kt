package com.example.brzodolokacije.Posts

data class Comment(
    val id : String,
    val owner : String,
    val ownerImage : String,
    val text : String,
    val postId : String
)
