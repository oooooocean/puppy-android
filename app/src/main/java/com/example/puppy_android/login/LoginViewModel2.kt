package com.example.puppy_android.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.models.Pandora
import com.example.puppy_android.net.LoginService
import com.example.puppy_android.net.Net
import com.example.puppy_android.services.set
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.d
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginViewModel2: ViewModel() {
    lateinit var loginEnableFlow: Flow<Boolean>
    lateinit var codeEnableFlow: Flow<Boolean>
    var codeCounterFlow = MutableStateFlow(DEFAULT_CODE_TIP)
    private var _timer: Timer? = null

    companion object {
        const val DEFAULT_CODE_TIP = "获取验证码"
    }

    fun setInput(phone: Flow<String>, code: Flow<String>, protocol: Flow<Boolean>) {
        val phoneValidFlow = phone.map { it.length == 11 }
        codeEnableFlow = phone.map { it.length == 11 }
            .combine(codeCounterFlow.map { it == DEFAULT_CODE_TIP }) { v1, v2 -> v1 and v2 }
            .distinctUntilChanged()
        loginEnableFlow = combine(phoneValidFlow, code.map { it.length == 6 }, protocol) { v1, v2, v3 ->
            v1 and v2 and v3
        }.distinctUntilChanged()
    }

    /**
     * 登录
     */
    suspend fun login(): LoginUser =
        suspendCoroutine { continuation ->
            val callback = object : Callback<LoginResult> {
                override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                    response.body()?.data?.let {
                        it["token"].let { token -> com.example.puppy_android.services.Store.Token.set(token) } // 保存 token
                        val user = Gson().run {
                            fromJson(this.toJson(it["user"]), LoginUser::class.java)
                        }
                        continuation.resume(user)
                    }
                }

                override fun onFailure(call: Call<Pandora<Map<String, Any>>>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            }

            Net.create<LoginService>().login("18856931381", "123456").enqueue(callback)
        }

    /**
     * 获取验证码
     */
    suspend fun fetchCode() {
        if (_timer != null) return
        coroutineScope {
            launch {
                delay(3_000)
                Module.Login.d("获取验证码成功: 123456")
            }

            launch {
                startTimer()
            }
        }
//        withContext(Dispatchers.IO) {
//            delay(3_000)
//            Module.Login.d("获取验证码成功: 123456")
//        }
//        Module.Login.d("--->")
//        withContext(Dispatchers.Main) {
//            Module.Login.d("--->1111")
//            startTimer()
//        }
    }

    /**
     * 开启定时器
     */
    private fun startTimer() {
        _timer?.cancel()
        var codeCount = 10
        _timer = Timer("code counter").apply {
            schedule(object : TimerTask() {
                override fun run() {
                    codeCount -= 1
                    if (codeCount == 0) {
                        _timer?.cancel()
                        _timer = null
                        codeCounterFlow.tryEmit("获取验证码")
                        return
                    }
                    codeCounterFlow.tryEmit("${codeCount}s")
                }
            }, 0, 1_000)
        }
    }
}