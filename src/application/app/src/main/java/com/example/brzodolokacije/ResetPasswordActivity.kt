package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_resetpassword.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

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

            val retrofit = Client.buildService(Api::class.java)
            retrofit.checkIfEmailExists(email).enqueue(object : Callback<DefaultResponse>
            {
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    if(response.body()?.message.toString() == "true")
                    {
                        Toast.makeText(this@ResetPasswordActivity,"valid E-mail",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this@ResetPasswordActivity,"not valid E-mail",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity,"Unexpected error occured",Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}