package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class PinDto (
    @SerializedName("Id") val id: Int,
    @SerializedName("Longitude") val longitude: String,
    @SerializedName("Latitude") val latitude: String

        )