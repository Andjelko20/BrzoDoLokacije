package com.example.brzodolokacije.Posts

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("Id")
    val id : String,

    @SerializedName("Content")
    val content : String,

    @SerializedName("Owner")
    val owner : String

)
