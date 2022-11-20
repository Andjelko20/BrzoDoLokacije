package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.ModelsDto.EditProfileDto
import com.example.brzodolokacije.ModelsDto.LoginDto
import com.example.brzodolokacije.ModelsDto.RegisterDto
import com.example.brzodolokacije.ModelsDto.ResetPasswordDto
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @POST("Auth/register")
    fun createUser(@Body userData: RegisterDto) : Call<DefaultResponse>

    @POST("Auth/check-email/{email}")
    fun checkIfEmailExists(@Path("email") email:String) : Call<DefaultResponse>

    @POST("Auth/check-username/{username}")
    fun checkIfUsernemeExists(@Path("username") email:String) : Call<DefaultResponse>
    
    @POST("Auth/login")
    fun loginUser(@Body userData : LoginDto) : Call<DefaultResponse>

    @POST("Auth/reset-password/{email}")
    fun sendEmailtoResetPassword(@Path("email") email:String) : Call<DefaultResponse>

    @PUT("Auth/reset-password")
    fun resetPassword(@Body changeData : ResetPasswordDto) : Call<DefaultResponse>

    @POST("Auth/check-token/{token}")
    fun checkIfTokenExists(@Path("token") token:String) : Call<DefaultResponse>

    @GET("Auth/check-session")
    fun authentication() : Call<DefaultResponse>

    @GET("User/profileInfo/{username}")
    fun fetchUserProfileInfo(@Path("username") username:String) : Call<DefaultResponse>

    @GET("Post/getAll")
    fun getAllPosts() : Call<DefaultResponse>

    @GET("Post/comments/{postId}")
    fun getComments(@Path("postId") postId : String) : Call<DefaultResponse>

    @POST("Post/addComment")
    fun addComment(@Body newComment : NewCommentDto) :  Call<DefaultResponse>

    @POST("Post/like/{postId}")
    fun likPost(@Path("postId") postId : String) : Call<DefaultResponse>

    @PUT("User/update")
    fun editUserInfo(@Body editProfile: EditProfileDto) : Call<DefaultResponse>

    @GET("Post/likes/{postId}")
    fun getLikes(@Path("postId") postId : String) :  Call<DefaultResponse>
}