package com.example.brzodolokacije.Posts

import android.util.Log

object HomeFragmentState {
//    var visitUser : String = ""
    private var feed : String = ""
    private var issaved : Boolean = false
    private var listFeed : MutableList<Photo?>? = null
    private var page : Int = 1
    private var maxPages : Int = 1
    var likesOpened = false
    var commentsOpened = false
    var lastPosition = 0
    var offset = 0

//    fun setVisit(owner : String)
//    {
//        visitUser=owner
//    }
//    fun getVisit() : String
//    {
//        return visitUser
//    }
    fun saveFeed(set : String)
    {
        feed=set
    }
    fun retreiveFeed() : String
    {
        return feed
    }

    fun shouldSave(yes : Boolean)
    {
        issaved = yes
    }

    fun isSaved() : Boolean
    {
        return issaved
    }

    fun list(feed : MutableList<Photo?>?)
    {
        listFeed = feed
//        Log.d("feed", listFeed.toString())
    }

    fun getList() : MutableList<Photo?>?
    {
        return listFeed
    }

    fun page(newPage : Int)
    {
        page = newPage
    }

    fun savedPage() : Int
    {
        return page
    }

    fun changeMaxPages(newMax : Int)
    {
        maxPages = newMax
    }

    fun returnMaxPages() : Int
    {
        return maxPages
    }
}