package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.UserResponse
import com.example.brzodolokacije.ModelsDto.LoginDto
import com.example.brzodolokacije.ModelsDto.RegisterDto
import com.example.brzodolokacije.ModelsDto.ResetPasswordDto
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @POST("Auth/register")
    fun createUser(@Body userData: RegisterDto):Call<DefaultResponse>

    @POST("Auth/check-email/{email}")
    fun checkIfEmailExists(@Path("email") email:String):Call<DefaultResponse>

    @POST("Auth/check-username/{username}")
    fun checkIfUsernemeExists(@Path("username") email:String):Call<DefaultResponse>
    
    @POST("Auth/login")
    fun loginUser(@Body userData : LoginDto) : Call<DefaultResponse>

    @POST("Auth/reset-password/{email}")
    fun sendEmailtoResetPassword(@Path("email") email:String):Call<DefaultResponse>

    @PUT("Auth/reset-password")
    fun resetPassword(@Body changeData : ResetPasswordDto):Call<DefaultResponse>

    @POST("Auth/check-token/{token}")
    fun checkIfTokenExists(@Path("token") token:String):Call<DefaultResponse>

    @GET("Auth/check-session")
    fun authentication(): Call<DefaultResponse>

    @GET("User/profileInfo/{username}")
    fun fetchUserProfileInfo(@Path("username") username:String): Call<DefaultResponse>

}