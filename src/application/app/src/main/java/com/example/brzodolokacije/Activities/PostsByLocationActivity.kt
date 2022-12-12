package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.PostDetails
import com.example.brzodolokacije.ModelsDto.FilterDto
import com.example.brzodolokacije.ModelsDto.PinDto
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_addpost.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsByLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_by_location)

        val lokacija = intent.getStringExtra("location")
        val retrofit = Client(this).buildService(Api::class.java)

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }

        val filterDto = FilterDto(lokacija,"")

        retrofit.getByLocation(filterDto).enqueue(object : Callback<DefaultResponse>{
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    val listOfPosts: String = response.body()?.message.toString()
                    val typeToken = object : TypeToken<List<PostDetails>>() {}.type
                    val posts = Gson().fromJson<List<PostDetails>>(listOfPosts, typeToken)
                    Log.d("lista",posts.toString())
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}