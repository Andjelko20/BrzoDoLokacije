package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_resetpassword.*

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

            Toast.makeText(this,"reset password clicked",Toast.LENGTH_SHORT).show()
        }
    }
}