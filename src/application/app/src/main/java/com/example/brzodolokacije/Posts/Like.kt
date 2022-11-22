package com.example.brzodolokacije.Posts

import com.google.gson.annotations.SerializedName

data class Like(
    @SerializedName("Owner")
    val owner : String
)
