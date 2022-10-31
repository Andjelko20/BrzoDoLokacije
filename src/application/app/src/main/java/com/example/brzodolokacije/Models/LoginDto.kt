package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class LoginDto(
    @SerializedName("usernameOrEmail") val usernameOrEmail: String?,
    @SerializedName("password") val password: String?
)
