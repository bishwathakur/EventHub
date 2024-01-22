package com.example.eventhub.models

import android.os.Parcel
import android.os.Parcelable

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
    var postLikes: Int = 0,
    var postRegistrations: Int = 0,
    var postComments: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(eventKey)
        parcel.writeString(eventname)
        parcel.writeString(eventdate)
        parcel.writeString(eventvenue)
        parcel.writeString(eventpicUrl)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userEmail)
        parcel.writeString(userImage)
        parcel.writeInt(postLikes)
        parcel.writeInt(postRegistrations)
        parcel.writeInt(postComments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
