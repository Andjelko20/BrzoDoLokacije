package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class MessageDto(
    @SerializedName("Sender")
    val sender : String,

    @SerializedName("Text")
    val message : String
)
