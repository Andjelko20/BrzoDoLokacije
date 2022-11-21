package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class NewPostDto(
    @SerializedName("location")
    val location : String,

    @SerializedName("caption")
    val caption : String
)