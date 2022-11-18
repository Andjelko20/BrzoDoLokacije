package com.example.brzodolokacije.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_editprofile.*

class ActivityEditProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        val buttonBack = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backPressed", "returnToProfile");
            startActivity(intent)
        }
    }
}