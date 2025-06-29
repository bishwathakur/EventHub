package com.example.eventhub.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val commentId: String = "",
    val comment: String = "",
    val userId: String = "",
    val userImage: String = "",
    val userName: String = "",
    val uid: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable