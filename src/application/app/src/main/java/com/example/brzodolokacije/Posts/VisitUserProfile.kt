package com.example.brzodolokacije.Posts

object VisitUserProfile {
    var visitUser : String = ""

    fun setVisit(owner : String)
    {
        visitUser=owner
    }
    fun getVisit() : String
    {
        return visitUser
    }
}