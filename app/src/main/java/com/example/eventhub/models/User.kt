package com.example.eventhub.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    var name: String? = null,
    var email: String? = null,
    var userid: String? = null,
    var userplace: String? = null,
    var userphone: String? = null,
    var pfp: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(userid)
        parcel.writeString(userplace)
        parcel.writeString(userphone)
        parcel.writeString(pfp)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
