package com.example.brzodolokacije.API

import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.NewCommentDto
import com.example.brzodolokacije.ModelsDto.*
import okhttp3.MultipartBody
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

    @GET("Post/getAll/1")
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

    @GET("Post/refreshPost/{postId}")
    fun refrestPost(@Path("postId") postId : String) :  Call<DefaultResponse>

    @POST("Post/addNew")
    fun addNewPost(@Body newPost : NewPostDto) :  Call<DefaultResponse>

    @Multipart
    @PUT("User/updateAvatar")
    fun uploadNewAvatar(@Part picture: MultipartBody.Part): Call<DefaultResponse>

    @Multipart
    @PUT("Post/uploadPhoto/{postId}")
    fun uploadPostPhoto(@Part picture: MultipartBody.Part,@Path("postId") postId : String): Call<DefaultResponse>

    @GET("Post/profilePosts/{username}")
    fun getUserPosts(@Path("username") username: String): Call<DefaultResponse>

    @PUT("Auth/change-password")
    fun changePassword(@Body changePasswordDto: ChangePasswordDto): Call<DefaultResponse>

    @PUT("Post/getAll/{location}")
    fun getByLocation(@Path ("location") location :String): Call<DefaultResponse>
}