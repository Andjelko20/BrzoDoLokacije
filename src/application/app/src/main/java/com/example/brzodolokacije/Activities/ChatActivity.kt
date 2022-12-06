package com.example.brzodolokacije.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments2.DirectMessageFragment
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Managers.SignalRListener
import com.example.brzodolokacije.R
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import java.lang.reflect.Member

class ChatActivity : AppCompatActivity() {

    lateinit var signalRListener: SignalRListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sessionManager = SessionManager(this)

        signalRListener = SignalRListener.getInstance()

        signalRListener.startConnection()
        signalRListener.registerMe(sessionManager.fetchUsername())



        val intent = getIntent()
        val username = intent.getStringExtra("messageUser")
//        Log.d("username", username.toString())

        val bundle = Bundle()
        bundle.putString("username", username)

        val directMessageFragment = DirectMessageFragment()
        directMessageFragment.arguments = bundle

        replaceFragment(directMessageFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chatSectionFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        signalRListener.stopConnection()
    }
}