package com.example.eventhub.models

import java.io.Serializable

data class Post(
    var userId:String?="",
    var userName:String?="",
    var userEmail:String?="",
    var userImage:String?="",
    var eventname:String="",
    var eventdate:String="",
    var eventvenue:String="",
    var eventby:String="",
    var postId:String="",
    var postAttachment:String?="",
    var postTime:String="",
    var postLikes:Int=0,
    var postComments:Int=0,
    var languageCode:String="und",
    var translatedContent:String=""


): Serializable