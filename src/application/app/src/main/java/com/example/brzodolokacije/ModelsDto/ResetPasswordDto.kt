package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class ResetPasswordDto (
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
        )