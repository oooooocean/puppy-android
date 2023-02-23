package net

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tools.GsonHelper
import java.time.Duration

object Net {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://39.107.136.94/puppy/api/v1/")
        .client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create(GsonHelper.gson))
        .build()

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}


