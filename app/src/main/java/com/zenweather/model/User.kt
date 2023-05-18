package com.zenweather.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = 0,
    val fcmToken: String = "",
    val googleAccount: Boolean = false
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readLong(),
        source.readString()!!,
        source.readBoolean()!!
    )

    override fun describeContents() = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
        writeBoolean(googleAccount)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}