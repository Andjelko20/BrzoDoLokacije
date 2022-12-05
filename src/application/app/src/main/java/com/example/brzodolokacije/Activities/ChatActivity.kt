package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments2.DirectMessageFragment
import com.example.brzodolokacije.R

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        replaceFragment(DirectMessageFragment())

        

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chatSectionFrameLayout, fragment)
        fragmentTransaction.commit()
    }
}