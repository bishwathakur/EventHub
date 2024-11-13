package com.example.eventhub.models

import android.os.Parcel
import android.os.Parcelable
import java.security.Timestamp

data class Chat(
    val senderID: String = "",
    val receiverID: String = "",
    val message: String = "",
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",// Read serialized Timestamp
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderID)
        parcel.writeString(receiverID)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Chat> {
        override fun createFromParcel(parcel: Parcel): Chat {
            return Chat(parcel)
        }

        override fun newArray(size: Int): Array<Chat?> {
            return arrayOfNulls(size)
        }
    }
}