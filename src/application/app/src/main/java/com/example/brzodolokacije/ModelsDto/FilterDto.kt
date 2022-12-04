package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class FilterDto (
    @SerializedName("Location") val location: String?,
    @SerializedName("Filter") val filter: String?
)