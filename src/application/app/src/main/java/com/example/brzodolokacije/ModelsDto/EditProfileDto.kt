package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class EditProfileDto(
    @SerializedName("Username")
    val name: String?,

    @SerializedName("Name")
    val username: String?,

    @SerializedName("Description")
    val description: String?
)
