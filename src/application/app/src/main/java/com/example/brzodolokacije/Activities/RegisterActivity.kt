package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.RegisterDto
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Models.Validation
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
        val sessionManager = SessionManager(this)

        backToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signupButton.setOnClickListener{

            var email = editEmail.text.toString().trim()
            var username = editUsername.text.toString().trim()
            var password = editPassword.text.toString().trim()
            var confpass = editConfirmPassword.text.toString().trim()

            val retrofit = Client(this).buildService(Api::class.java)

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

            retrofit.checkIfEmailExists(email).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if(response.body()?.message.toString() == "true")
                    {
                        editEmail.error = "This email is already linked to another account"
                        editEmail.requestFocus()
                    }

                    else {
                        retrofit.checkIfUsernemeExists(username).enqueue(object : Callback<DefaultResponse>{
                            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>){
                                if(response.body()?.message.toString() == "true")
                                {
                                    editUsername.error = "This username is already taken"
                                    editUsername.requestFocus()
                                }
                                else
                                {
                                    val userData = RegisterDto(username,email,password)
                                    retrofit.createUser(userData).enqueue(object : Callback<DefaultResponse>{
                                        override fun onResponse(
                                            call: Call<DefaultResponse>,
                                            response: Response<DefaultResponse>
                                        ) {
                                            var token=response.body()?.message.toString()
                                            sessionManager.saveAuthToken(token)

                                            retrofit.authentication().enqueue(object: Callback<DefaultResponse>
                                            {
                                                override fun onResponse(
                                                    call: Call<DefaultResponse>,
                                                    response: Response<DefaultResponse>
                                                ) {
                                                    if(response.body()?.error.toString() == "false") {
                                                        var username = response.body()?.message.toString()
                                                        sessionManager.saveUsername(username)

                                                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }

                                                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                                    Toast.makeText(this@RegisterActivity,t.toString(),Toast.LENGTH_SHORT).show()
                                                }

                                            })
                                            
//                                            reset()
                                        }

                                        override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                            Toast.makeText(this@RegisterActivity,t.toString(),Toast.LENGTH_SHORT).show()
                                        }

                                    })
                                }
                            }

                            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                                Log.e("Failed username", "")
                            }
                        })
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e("Failed email", "")
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