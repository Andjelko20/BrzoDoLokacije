package com.example.brzodolokacije.ModelsDto

import com.google.gson.annotations.SerializedName

data class InboxDto(
    @SerializedName("ConvoWith")
    val convoWith : String,

    @SerializedName("MessagePreview")
    val MessagePreview : String
)
