package com.example.eventhub.models

import java.io.Serializable

data class Post(
    var eventKey: String = "",
    var eventname: String = "",
    var eventdate: String = "",
    var eventvenue: String = "",
    var eventpicUrl: String? = "",
    var userId: String? = "",
    var userName: String? = "",
    var userEmail: String? = "",
    var userImage: String? = "",
    var postTime: String = "",
    var postLikes: Int = 0,
    var postComments: Int = 0
) : Serializable

