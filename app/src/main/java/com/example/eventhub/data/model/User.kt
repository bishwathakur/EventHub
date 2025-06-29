package com.example.eventhub.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val userid: String = "",
    val userplace: String = "",
    val userphone: String = "",
    val pfp: String = ""
) : Parcelable