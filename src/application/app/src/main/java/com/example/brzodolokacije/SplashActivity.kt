package com.example.brzodolokacije

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler(Looper.getMainLooper()).postDelayed({

            isLoggedin()

        }, 3000)
    }


    private fun isLoggedin()
    {
        val sessionManager = SessionManager(this)
        val retrofit = Client.buildService(Api::class.java)
        retrofit.authorization(token = "Bearer ${sessionManager.fetchAuthToken()}").enqueue(object: Callback<DefaultResponse>
        {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                if(response.body()?.error.toString() == "false")
                {
                    var username=response.body()?.message.toString()
                    sessionManager.saveUsername(username)

                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Toast.makeText(this@SplashActivity,t.toString(),Toast.LENGTH_SHORT).show()
            }

        })
    }
}
