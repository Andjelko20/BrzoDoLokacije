package com.example.brzodolokacije

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler(Looper.getMainLooper()).postDelayed({
            if(isLogedin())
            {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
            {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }
    private fun isLogedin() : Boolean
    {
        val sharedPreferences = getSharedPreferences("STORAGE", Context.MODE_PRIVATE)
        val token: String? = sharedPreferences.getString("token", "notFound")
        //Toast.makeText(this@SplashActivity,token, Toast.LENGTH_LONG).show()
        return token.toString()!="notFound"
    }
}
