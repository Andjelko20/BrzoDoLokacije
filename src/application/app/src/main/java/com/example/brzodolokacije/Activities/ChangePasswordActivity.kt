package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.brzodolokacije.R

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val cancelPasswordButton = findViewById<Button>(R.id.cancelPasswordButton)
        cancelPasswordButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }

        val buttonBackResetPassword = findViewById<Button>(R.id.backButtonResetPassword)
        buttonBackResetPassword.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }
    }
}