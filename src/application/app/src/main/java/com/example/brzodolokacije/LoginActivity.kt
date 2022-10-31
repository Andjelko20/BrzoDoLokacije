package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.LoginDto
import com.example.brzodolokacije.Models.Token
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener{

            var usernameOrEmail = username.text.toString().trim()
            var userPassword = password.text.toString().trim()

            if(usernameOrEmail.isEmpty()){
                username.error = "Username or Email required"
                username.requestFocus()
                return@setOnClickListener
            }

            if(userPassword.isEmpty()){
                password.error = "Password required"
                password.requestFocus()
                return@setOnClickListener
            }

            var userData = LoginDto(usernameOrEmail,userPassword)

            val retrofit = Client.buildService(Api::class.java)
            retrofit.loginUser(userData).enqueue(object : Callback<DefaultResponse>
            {


                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    var token=Token(response.body()?.message.toString())
                    if(token.content=="null"){
                        Toast.makeText(this@LoginActivity,"Incorrect user or password",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity,t.toString(),Toast.LENGTH_SHORT).show()
                }

            }
            )
        }

        forgotPasswordLink.setOnClickListener{
            Toast.makeText(this,"Forgotten password",Toast.LENGTH_SHORT).show()
        }

        createAccountLink.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}