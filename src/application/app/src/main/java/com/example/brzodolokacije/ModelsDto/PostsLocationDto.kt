package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class PostsLocationDto (
    @SerializedName("Location") val location: String,
    @SerializedName("Longitude") val longitude: String,
    @SerializedName("Latitude") val latitude: String
        )