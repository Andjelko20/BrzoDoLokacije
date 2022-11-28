package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class ChangePasswordDto(
    @SerializedName("CurrentPassword")
    val currentPassword: String,

    @SerializedName("NewPassword")
    val newPassword: String
)
