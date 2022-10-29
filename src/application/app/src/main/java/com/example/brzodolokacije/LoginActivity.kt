package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin=findViewById<Button>(R.id.loginBtn)
        btnLogin.setOnClickListener{
            Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
        }
    }
}