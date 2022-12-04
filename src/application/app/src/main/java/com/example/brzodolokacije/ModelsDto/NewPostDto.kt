package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class NewPostDto(
    @SerializedName("Location")
    val location : String,

    @SerializedName("Caption")
    val caption : String,

    @SerializedName("Latitude")
    val latitude : String,

    @SerializedName("Longitude")
    val longitude : String
)