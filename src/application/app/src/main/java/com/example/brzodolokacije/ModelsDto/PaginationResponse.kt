package com.example.brzodolokacije.ModelsDto

import com.example.brzodolokacije.Posts.Photo
import com.google.gson.annotations.SerializedName

data class PaginationResponse(
    @SerializedName("Posts")
    val posts : MutableList<Photo?>?,

    @SerializedName("currentPage")
    val currentPage : Int,

    @SerializedName("NumberOfPages")
    val numberOfPages : Int
    )