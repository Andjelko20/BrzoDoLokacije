package com.example.brzodolokacije

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.RegisterDto
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        signupButton.setOnClickListener{

            var email = editEmail.text.toString().trim()
            var username = editUsername.text.toString().trim()
            var password = editPassword.text.toString().trim()
            var confpass = editConfirmPassword.text.toString().trim()

            if(email.isEmpty()){
                editEmail.error = "Email required"
                editEmail.requestFocus()
                return@setOnClickListener
            }
            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                editPassword.error = "Password required"
                editPassword.requestFocus()
                return@setOnClickListener
            }
            if(confpass != password){
                editConfirmPassword.error = "Not the same password"
                editConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            val userData = RegisterDto(username,email,password)
            Log.d("UserData", userData.toString())
            val retrofit = Client.buildService(Api::class.java)
            retrofit.createUser(userData).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    Toast.makeText(this@RegisterActivity, response.body()?.message.toString(),Toast.LENGTH_SHORT).show()
                    reset()
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity,t.toString(),Toast.LENGTH_SHORT).show()
                }

            })

        }


    }

    fun reset() {
        editEmail.text.clear()
        editPassword.text.clear()
        editConfirmPassword.text.clear()
        editUsername.text.clear()
    }
}