package com.example.eventhub.models

import android.os.Parcel
import android.os.Parcelable

data class Comment(
    var commentId:String="",
    var comment:String="",
    var userId:String="",
    var userImage:String="",
    var userName:String="",
    var uid: String=""

) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(commentId)
        parcel.writeString(comment)
        parcel.writeString(userId)
        parcel.writeString(userImage)
        parcel.writeString(userName)
        parcel.writeString(uid)
    }
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }
        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}

