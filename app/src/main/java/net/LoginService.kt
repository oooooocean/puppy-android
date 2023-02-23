package net

import models.Pandora
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginService {
    @FormUrlEncoded
    @POST("user/login/")
    fun login(@Field("phone") phone: String, @Field("code") code: String): Call<Pandora<Map<String, Any>>>
}