package com.example.puppy_android.modules.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.models.Pandora
import com.example.puppy_android.net.LoginService
import com.example.puppy_android.net.Net
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.set
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.d
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginViewModel2 : ViewModel() {
    lateinit var loginEnableFlow: Flow<Boolean>
    lateinit var codeEnableFlow: Flow<Boolean>
    var codeCounterFlow = MutableStateFlow(DEFAULT_CODE_TIP)
    private var _timer: Timer? = null

    companion object {
        const val DEFAULT_CODE_TIP = "获取验证码"
    }

    fun setInput(phone: Flow<String>, code: Flow<String>, protocol: Flow<Boolean>) {
        val phoneValidFlow = phone.map { it.length == 11 }
        codeEnableFlow = phoneValidFlow
            .combine(codeCounterFlow.map { it == DEFAULT_CODE_TIP }) { v1, v2 -> v1 and v2 }
            .distinctUntilChanged()
        loginEnableFlow = combine(phoneValidFlow, code.map { it.length == 6 }, protocol) { v1, v2, v3 ->
            v1 and v2 and v3
        }.distinctUntilChanged()
    }

    /**
     * 登录
     */
    suspend fun login(): LoginUser? =
        Net.create<LoginService>().login("18856931381", "123456").awaitResponse().body()?.data?.run {
            this["token"].let { token -> com.example.puppy_android.services.Store.Token.set(token) } // 保存 token
            val gson = Gson()
            val userJson = gson.toJson(this["user"])
            Store.LoginUser.set(userJson) // 缓存User
            gson.fromJson(userJson, LoginUser::class.java)
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