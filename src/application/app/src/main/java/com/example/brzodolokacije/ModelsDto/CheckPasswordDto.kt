package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class CheckPasswordDto(
    @SerializedName("Password")
    var password: String
)
