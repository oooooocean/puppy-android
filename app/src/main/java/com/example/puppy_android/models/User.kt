package com.example.puppy_android.models

import androidx.versionedparcelable.VersionedParcelize
import com.google.gson.annotations.SerializedName

enum class Gender {
    @SerializedName(value = "0")
    MALE,
    @SerializedName(value = "1")
    FEMALE;

    val humanString: String
        get() = if (this == MALE) "男" else "女"

    val puppyString: String
        get() = if (this == MALE) "男宝" else "女宝"
}

@VersionedParcelize
data class UserInfo(val nickname: String, val gender: Gender, val avatar: String, val introduction: String)

@VersionedParcelize
data class LoginUser(val id: Int, val info: UserInfo?, val petCount: Int)