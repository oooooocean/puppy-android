package com.example.puppy_android.net

import com.moczul.ok2curl.Configuration
import com.moczul.ok2curl.CurlCommandGenerator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.puppy_android.tools.GsonHelper
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.d
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.get
import okio.Utf8
import java.time.Duration

/**
 * 网络模块
 */
object Net {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://39.107.136.94/puppy/api/v1/")
        .client(
            OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(30))
                .addNetworkInterceptor(RequestInterceptor)
                .addNetworkInterceptor(LoggerInterceptor)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create(GsonHelper.gson))
        .build()

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}

/**
 * 请求插件
 */
object RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request().newBuilder()
            .apply {
                addHeader("Sesame-Platform", "android")
                Store.Token.get<String>()?.let { addHeader("Authorization", it) }
            }
            .build()
        return chain.proceed(req)
    }
}

/**
 * 日志插件
 */
object LoggerInterceptor : Interceptor {
    private val curlGenerator = CurlCommandGenerator(Configuration())

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        Module.Net.d(curlGenerator.generate(req)) // curl
        val res = chain.proceed(req)
        res.body?.source()?.let {
            it.request(Long.MAX_VALUE)
            Module.Net.d(it.buffer.clone().readString(Charsets.UTF_8)) // response
        }
        return res
    }
}