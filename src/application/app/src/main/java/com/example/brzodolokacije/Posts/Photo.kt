package com.example.brzodolokacije.Posts

import java.util.Date

data class Photo (
    var postID : String,
    var owner : String,
    var ownerID : String,
    var dateTime : Date,
    var location : String, //za sad je string, mozda se menja
    var caption : String,
    var numOfLikes : Int,
    var numOfComments : Int
)