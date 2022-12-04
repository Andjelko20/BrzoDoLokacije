package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.brzodolokacije.API.Api
import com.example.brzodolokacije.Client.Client
import com.example.brzodolokacije.Models.DefaultResponse
import com.example.brzodolokacije.ModelsDto.ChangePasswordDto
import com.example.brzodolokacije.ModelsDto.CheckPasswordDto
import com.example.brzodolokacije.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val retrofit = Client(this).buildService(Api::class.java)

        val backButtonDeleteAccount = findViewById<Button>(R.id.backButtonDeleteAccount)
        backButtonDeleteAccount.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val cancelAccountDeletionButton = findViewById<Button>(R.id.cancelAccountDeletionButton)
        cancelAccountDeletionButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val confirmAccountDeletionButton = findViewById<Button>(R.id.confirmAccountDeletionButton)
        confirmAccountDeletionButton.setOnClickListener{
            val userPasswordPolje = findViewById<EditText>(R.id.userPassword)
            val userPassword = userPasswordPolje.text.toString().trim()

            //password check
            if(userPassword.isEmpty()){
                userPasswordPolje.error = "Please enter your password"
                userPasswordPolje.requestFocus()
                return@setOnClickListener
            }

            val password = CheckPasswordDto(userPassword)

            retrofit.checkPassword(password).enqueue(object : Callback<DefaultResponse>{
                override fun onResponse(
                    call: Call<DefaultResponse>,
                    response: Response<DefaultResponse>
                ) {
                    Log.d("response", "")

                }

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.d("failed", "")
                }

            })
        }
    }
}