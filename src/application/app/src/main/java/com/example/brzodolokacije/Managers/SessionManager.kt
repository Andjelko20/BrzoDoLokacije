package com.example.brzodolokacije.Managers

import android.content.Context
import android.content.SharedPreferences
import com.example.brzodolokacije.Posts.Photo
import com.example.brzodolokacije.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USERNAME = "username"
        const val HOME_FEED = "home_feed"
        const val LAST_POSITION = "last_position"
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun deleteAuthToken()
    {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, null)
        editor.apply()
    }
    fun deleteUsername()
    {
        val editor = prefs.edit()
        editor.putString(USERNAME, null)
        editor.apply()
    }

    fun saveUsername(username: String) {
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    fun fetchUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    fun saveFeed(posts : String)
    {
        val editor = prefs.edit()
        editor.putString(HOME_FEED, posts)
        editor.apply()
    }

    fun fetchFeed() : String?
    {
        return prefs.getString(HOME_FEED, null)
    }

    fun deleteFeed()
    {
        val editor = prefs.edit()
        editor.putString(HOME_FEED, null)
        editor.apply()
    }

    fun saveLast(position : Int)
    {
        val editor = prefs.edit()
        editor.putInt(LAST_POSITION, position)
        editor.apply()
    }

    fun fetchLast() : Int
    {
        return prefs.getInt(LAST_POSITION, 0)
    }
}