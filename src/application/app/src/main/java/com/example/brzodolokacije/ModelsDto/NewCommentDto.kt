package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class NewCommentDto(
    @SerializedName("postId")
    val postId : String,

    @SerializedName("content")
    val content : String
)
