package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.brzodolokacije.R
import kotlin.system.exitProcess

class AccountDeletedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_deleted)

        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)
        backToLoginButton.setOnClickListener{
            val intent = Intent(this@AccountDeletedActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val exitApplicationButton = findViewById<Button>(R.id.exitApplicationButton)
        exitApplicationButton.setOnClickListener{
            finishAffinity()
        }
    }
}