package com.example.brzodolokacije.Posts

object VisitUserProfile {
    var visitUser : String = ""
    var feed : String = ""
    var profileVisited : Int = 0

    fun setVisit(owner : String)
    {
        visitUser=owner
    }
    fun getVisit() : String
    {
        return visitUser
    }
    fun saveFeed(set : String)
    {
        feed=set
    }
    fun retreiveFeed() : String
    {
        return feed
    }

    fun profileVisit(yes : Int)
    {
        profileVisited = yes
    }

    fun isVisited() : Int
    {
        return profileVisited
    }
}