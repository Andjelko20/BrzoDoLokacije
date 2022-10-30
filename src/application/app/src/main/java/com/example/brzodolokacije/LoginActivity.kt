package com.example.brzodolokacije

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin=findViewById<Button>(R.id.loginBtn)
        btnLogin.setOnClickListener{
            Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
        }

        val forgottenPassLink=findViewById<TextView>(R.id.forgotPasswordLink)
        forgottenPassLink.setOnClickListener{
            Toast.makeText(this,"Forgotten password",Toast.LENGTH_SHORT).show()
        }

        val goToRegister=findViewById<TextView>(R.id.createAccountLink)
        goToRegister.setOnClickListener{
            //Toast.makeText(this,"Go to register",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}