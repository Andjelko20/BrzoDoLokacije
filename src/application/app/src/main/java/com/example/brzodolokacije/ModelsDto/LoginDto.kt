package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("usernameOrEmail") val usernameOrEmail: String?,
    @SerializedName("password") val password: String?
)
