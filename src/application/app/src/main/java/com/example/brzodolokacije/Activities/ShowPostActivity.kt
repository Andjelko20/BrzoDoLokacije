package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)

        val intent = getIntent()
        val postIdStr = intent.getStringExtra("showPost");
//        Log.d("clicked id", postIdStr.toString())

        val postId = postIdStr?.toInt()

        val backButtonPostDetails = findViewById<Button>(R.id.backButtonPostDetails)
        backButtonPostDetails.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val retrofit = Client(this).buildService(Api::class.java)
        if (postId != null) {
            retrofit.getPostData(postId).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.error.toString() == "false")
                    {
                        val json = response.body()?.message.toString()
                        Log.d("data", json)
                    }
                    else
                    {
                        Log.d("error", response.body()?.error.toString())
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed", "")
                }

            })
        }
    }
}