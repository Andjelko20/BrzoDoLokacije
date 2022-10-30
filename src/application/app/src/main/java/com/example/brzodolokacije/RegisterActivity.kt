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
        val validation = Validation()

        backToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener{

            var email = editEmail.text.toString().trim()
            var username = editUsername.text.toString().trim()
            var password = editPassword.text.toString().trim()
            var confpass = editConfirmPassword.text.toString().trim()


            //email check
            if(email.isEmpty()){
                editEmail.error = "Email required"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            if(!validation.checkEmail(email)){
                editEmail.error = "Please enter a valid email address"
                editEmail.requestFocus()
                return@setOnClickListener
            }


            //username check
            if(username.isEmpty()){
                editUsername.error = "Username required"
                editUsername.requestFocus()
                return@setOnClickListener
            }
            if(!validation.checkUsername(username)){
                editUsername.error = "Username must contain at least 6 characters (lowercase letters, numbers and _ only)"
                editUsername.requestFocus()
                return@setOnClickListener
            }


            //password check
            if(password.isEmpty()){
                editPassword.error = "Password required"
                editPassword.requestFocus()
                return@setOnClickListener
            }
            if(!validation.checkPassword(password)){
                editPassword.error = "Password must contain minimum 8 characters, at least one uppercase letter, one lowercase letter and one number"
                editPassword.requestFocus()
                return@setOnClickListener
            }

            //check if passwords match
            if(confpass != password){
                editConfirmPassword.error = "Passwords don't match"
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