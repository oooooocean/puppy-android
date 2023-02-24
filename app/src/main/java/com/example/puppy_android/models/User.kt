package com.example.puppy_android.models

import com.google.gson.annotations.SerializedName

enum class Gender {
    @SerializedName(value = "0")
    MALE,
    @SerializedName(value = "1")
    FEMALE
}

data class UserInfo(val nickname: String, val gender: Gender, val avatar: String, val introduction: String)

data class LoginUser(val id: Int, val info: UserInfo?, val petCount: Int)
