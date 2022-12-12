package com.example.brzodolokacije.Managers

interface InboxChatCommunicator {

    fun goToDirectMessage(user : String)
    fun backToInbox()
}