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
import okhttp3.ResponseBody
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

            val retrofit = Client.buildService(Api::class.java)

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

            retrofit.checkIfEmailExists(email).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if(response.body()?.message.toString() == "true")
                    {
                        Log.d("Postoji email", response.body()?.message.toString())
                        editEmail.error = "This email is already linked to another account"
                        editEmail.requestFocus()
                    }

                    else {
                        Log.d("Ne postoji email", response.body()?.message.toString())
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e("Failed email", "")
                }
            })

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

            retrofit.checkIfUsernemeExists(username).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>){
                    if(response.body()?.message.toString() == "true")
                    {
                        Log.d("Postoji username", response.body()?.message.toString())
                        editUsername.error = "This username is already taken"
                        editUsername.requestFocus()
                    }
                    else
                    {
                        Log.d("Ne postoji username", response.body()?.message.toString())
                    }
                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e("Failed username", "")
                }
            })


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