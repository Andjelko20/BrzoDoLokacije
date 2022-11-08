package com.example.brzodolokacije.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.Models.ResetPasswordDto
import com.example.brzodolokacije.R
import com.example.brzodolokacije.Models.Validation
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_resetpassword.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResetPasswordActivity : AppCompatActivity() {

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        val retrofit = Client(this).buildService(Api::class.java)
        val validation = Validation()
        resetToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        uri = intent.data



        if (uri != null) {

            val parameters = uri!!.pathSegments


            val param = parameters[parameters.size - 1]
            Log.d("Token",param)


            retrofit.checkIfTokenExists(param).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                    ) {
                    if(response.body()?.error.toString()=="false") {
                        saveChangesBtn.setOnClickListener {

                            var password = resetPassword.text.toString().trim()
                            var confpass = resetConfPassword.text.toString().trim()

                            if (password.isEmpty()) {
                                editPassword.error = "Password required"
                                editPassword.requestFocus()
                                return@setOnClickListener
                            }

                            if (confpass != password) {
                                editConfirmPassword.error = "Passwords don't match"
                                editConfirmPassword.requestFocus()
                                return@setOnClickListener
                            }
                            if (!validation.checkPassword(password)) {
                                editPassword.error =
                                    "Password must contain minimum 8 characters, at least one uppercase letter, one lowercase letter and one number"
                                editPassword.requestFocus()
                                return@setOnClickListener
                            }
                            //Log.d("Username",response.body()?.message.toString())
                            val changeData =
                                ResetPasswordDto(response.body()?.message.toString(), password)
                            retrofit.resetPassword(changeData)
                                .enqueue(object : Callback<DefaultResponse> {
                                    override fun onResponse(
                                        call: Call<DefaultResponse>,
                                        response: Response<DefaultResponse>
                                    ) {
                                        if (response.body()?.error.toString() == "false") {
                                            val intent = Intent(
                                                this@ResetPasswordActivity,
                                                LoginActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@ResetPasswordActivity,
                                                response.body()?.message.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<DefaultResponse>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(
                                            this@ResetPasswordActivity,
                                            t.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }
                    else
                    {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Token not valid",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(
                            this@ResetPasswordActivity,
                            LoginActivity::class.java
                        )
                        startActivity(intent)
                        finish()
                    }

                    }

                    override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                        Toast.makeText(this@ResetPasswordActivity,t.toString(),Toast.LENGTH_SHORT).show()
                    }


                })
            }
        }




}