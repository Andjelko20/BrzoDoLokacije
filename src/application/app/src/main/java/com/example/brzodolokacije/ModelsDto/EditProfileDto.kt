package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class EditProfileDto(
    @SerializedName("Name")
    val name: String?,

    @SerializedName("Username")
    val username: String?,

    @SerializedName("Description")
    val description: String?
)
