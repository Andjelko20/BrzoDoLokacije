package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.RegisterDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {

    @POST("register")
    fun createUser(@Body userData:RegisterDto):Call<DefaultResponse>

    @GET("check-email/{email}")
    fun checkIfEmailExists(@Path("email") email:String):Call<Boolean>
}