package com.customgit.core.data_classes

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RemoteGithubUser(
    @SerializedName("login") val login: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("public_repos") val publicRepos: Int,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(login)
        parcel.writeString(avatarUrl)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(email)
        parcel.writeInt(publicRepos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RemoteGithubUser> {
        override fun createFromParcel(parcel: Parcel): RemoteGithubUser {
            return RemoteGithubUser(parcel)
        }

        override fun newArray(size: Int): Array<RemoteGithubUser?> {
            return arrayOfNulls(size)
        }
    }
}