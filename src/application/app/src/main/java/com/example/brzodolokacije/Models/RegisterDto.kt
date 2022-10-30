package com.example.brzodolokacije.Models

import com.google.gson.annotations.SerializedName

data class RegisterDto (
    @SerializedName("username") val userName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
        )