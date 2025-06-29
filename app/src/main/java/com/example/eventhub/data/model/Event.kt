package com.example.eventhub.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val eventKey: String = "",
    val eventname: String = "",
    val eventdate: String = "",
    val eventvenue: String = "",
    val eventpicUrl: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userImage: String = "",
    val postLikes: Int = 0,
    val postRegistrations: Int = 0,
    val postComments: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = ""
) : Parcelable