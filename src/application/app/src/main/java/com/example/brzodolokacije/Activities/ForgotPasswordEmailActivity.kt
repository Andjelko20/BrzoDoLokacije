package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_forgotpasswordemail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordEmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpasswordemail)

        resetToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        resetPasswordBtn.setOnClickListener{
            var email=findViewById<EditText>(R.id.registeredEmail).text.toString().trim()

            if(email.isEmpty()){
                registeredEmail.error = "Email required"
                registeredEmail.requestFocus()
                return@setOnClickListener
            }

            val retrofit = Client(this).buildService(Api::class.java)
            retrofit.checkIfEmailExists(email).enqueue(object : Callback<DefaultResponse>
            {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.message.toString() == "true")
                    {
                        //Toast.makeText(this@ForgotPasswordEmailActivity,"valid E-mail",Toast.LENGTH_SHORT).show()
                        retrofit.sendEmailtoResetPassword(email).enqueue(object : Callback<DefaultResponse>
                        {
                            override fun onResponse(
                                call: Call<DefaultResponse>,
                                response: Response<DefaultResponse>
                            ) {
                                if(response.body()?.error.toString() == "false")
                                {
                                    Toast.makeText(this@ForgotPasswordEmailActivity,"E-mail sent, check your (spam) inbox",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(
                                        this@ForgotPasswordEmailActivity,
                                        LoginActivity::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                }
                                else
                                {
                                    Toast.makeText(this@ForgotPasswordEmailActivity,"E-mail wasn't sent",Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                Toast.makeText(this@ForgotPasswordEmailActivity,t.toString(),Toast.LENGTH_SHORT).show()
                            }

                        }
                        )
                    }
                    else
                    {
                        Toast.makeText(this@ForgotPasswordEmailActivity,"E-mail not valid",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordEmailActivity,t.toString(),Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}