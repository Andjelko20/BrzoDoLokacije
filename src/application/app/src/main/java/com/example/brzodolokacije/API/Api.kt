package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.LoginDto
import com.example.brzodolokacije.Models.RegisterDto
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @POST("register")
    fun createUser(@Body userData:RegisterDto):Call<DefaultResponse>

    @POST("check-email/{email}")
    fun checkIfEmailExists(@Path("email") email:String):Call<DefaultResponse>

    @POST("check-username/{username}")
    fun checkIfUsernemeExists(@Path("username") email:String):Call<DefaultResponse>
    
    @POST("login")
    fun loginUser(@Body userData : LoginDto) : Call<DefaultResponse>

    @POST("reset-password/{email}")
    fun resetPassword(@Path("email") email:String):Call<DefaultResponse>

    @PUT("new-password/{email}")
    fun saveChanges(@Path("email") email:String):Call<DefaultResponse>

    @GET("check-session")
    fun authorization(@Header("Authorization") token: String?): Call<DefaultResponse>
}