package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.brzodolokacije.R

class PostsByLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts_by_location)

        val nesto = intent.getStringExtra("location")

        Toast.makeText(this@PostsByLocationActivity,nesto,Toast.LENGTH_SHORT).show()

    }
}