package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.LoginDto
import com.example.brzodolokacije.Models.RegisterDto
import com.example.brzodolokacije.Models.ResetPasswordDto
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

    @POST("check-email/{email}")
    fun sendEmailtoResetPassword(@Path("email") email:String):Call<DefaultResponse>

    @PUT("reset-password/{username}")
    fun resetPassword(@Body changeData : ResetPasswordDto):Call<DefaultResponse>

    @POST("check-token/{token}")
    fun checkIfTokenExists(@Path("token") token:String):Call<DefaultResponse>

    @GET("check-session")
    fun authorization(@Header("Authorization") token: String?): Call<DefaultResponse>
}