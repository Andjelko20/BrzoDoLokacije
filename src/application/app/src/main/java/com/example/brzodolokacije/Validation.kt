package com.example.brzodolokacije

class Validation {
    fun checkEmail(email: String): Boolean
    {
        val emailPattern = Regex("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})\$")
        if(email.matches(emailPattern))
            return true
        return false
    }

    fun checkUsername(username: String): Boolean
    {
        val usernamePattern = Regex("^[a-z0-9_]{6,28}\$")
        if(username.matches(usernamePattern))
            return true
        return false
    }

    fun checkPassword(password: String): Boolean
    {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$")
        if(password.matches(passwordPattern))
            return true
        return false
    }
}