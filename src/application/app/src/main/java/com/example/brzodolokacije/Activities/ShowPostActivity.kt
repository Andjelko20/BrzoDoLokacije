package com.example.brzodolokacije.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.brzodolokacije.Fragments2.HomeFragment
import com.example.brzodolokacije.Fragments2.ProfileFragment
import com.example.brzodolokacije.R
import kotlinx.android.synthetic.main.activity_main.*

class ShowPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)

        val intent = getIntent()
        val postId = intent.getStringExtra("showPost");
//        Log.d("clicked id", postId.toString())

        val backButtonPostDetails = findViewById<Button>(R.id.backButtonPostDetails)
        backButtonPostDetails.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("backToProfile", "returnToProfile");
            startActivity(intent)
        }
    }
}