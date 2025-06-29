package com.example.eventhub.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val messageId: String = "",
    val eventId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderImage: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable