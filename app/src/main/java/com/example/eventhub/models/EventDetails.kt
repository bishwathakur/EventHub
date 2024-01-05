package com.example.eventhub.models

import java.io.Serializable

data class EventDetails(
    var userId:String?="",
    var userName:String?="",
    var userEmail:String?="",
    var userImage:String?="",
    var postId:String="",
    var postTime:String="",
    var postLikes:Int=0,
    var postComments:Int=0,
    var postAttachment:String?=""


): Serializable

