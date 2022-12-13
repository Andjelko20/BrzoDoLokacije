package com.example.brzodolokacije.Activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.brzodolokacije.Fragments2.DirectMessageFragment
import com.example.brzodolokacije.Fragments2.InboxFragment
import com.example.brzodolokacije.Managers.InboxChatCommunicator
import com.example.brzodolokacije.Managers.SessionManager
import com.example.brzodolokacije.Managers.SignalRListener
import com.example.brzodolokacije.R

class ChatActivity : AppCompatActivity(), InboxChatCommunicator {

    lateinit var signalRListener: SignalRListener
    lateinit var isInbox : String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val sessionManager = SessionManager(this)

        signalRListener = SignalRListener.getInstance()
        isInbox = "false"

        signalRListener.startConnection()
        signalRListener.registerMe(sessionManager.fetchUsername())

        val intent = getIntent()
        val username = intent.getStringExtra("messageUser")
        val directMessage= intent.getStringExtra("directMessage")
        val inbox = intent.getStringExtra("inbox")

        if(directMessage=="directMessage")
        {
            val bundle = Bundle()
            bundle.putString("username", username)

            val directMessageFragment = DirectMessageFragment()
            directMessageFragment.arguments = bundle

            bundle.putString("directMessage","direct message")
            replaceFragment(directMessageFragment)
        }
        else if(inbox=="inbox")
        {
            isInbox = "true"
            replaceFragment(InboxFragment())
        }
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

    override fun goToDirectMessage(user: String) {
        val bundle = Bundle()
        bundle.putString("username", user)
        bundle.putString("directMessage","")

        val directMessageFragment = DirectMessageFragment()
        directMessageFragment.arguments = bundle

        replaceFragment(directMessageFragment)
    }

    override fun backToInbox() {
        replaceFragment(InboxFragment())
    }
    override fun onBackPressed() {
        val instance = supportFragmentManager.findFragmentById(R.id.chatSectionFrameLayout)
        if(instance is DirectMessageFragment && isInbox == "true")
            replaceFragment(InboxFragment())
        else finish()
    }
}