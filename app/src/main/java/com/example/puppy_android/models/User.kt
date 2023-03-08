package com.example.puppy_android.models

import androidx.versionedparcelable.VersionedParcelize
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.get
import com.example.puppy_android.services.set
import com.google.gson.Gson
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
data class LoginUser(val id: Int, var info: UserInfo?, val petCount: Int) {
    companion object {
        fun save() {
            Store.LoginUser.set(Gson().toJson(this))
        }

        val cached: LoginUser?
            get() {
                val json = Store.LoginUser.get<String>()
                if (json == null || json.isEmpty()) return null;
                return Gson().fromJson(json, LoginUser::class.java)
            }
    }
}