package com.example.puppy_android.net

import com.example.puppy_android.models.Pandora
import com.example.puppy_android.models.UserInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("user/{id}/info/")
    fun addUser(@Path("id") id: Int, @Body params: Map<String, Any>): Call<Pandora<UserInfo>>
}