package com.example.puppy_android.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import extensions.merge
import kotlinx.coroutines.delay
import java.util.Timer
import java.util.TimerTask

class LoginViewModel : ViewModel() {
    inner class Data(
        var phone: String? = null, var code: String? = null, var protocol: Boolean = false
    ) : MediatorLiveData<Boolean>() {
        /**
         * 是否允许登录
         */
        val loginEnable
            get() = phone?.length == 11 && code?.length == 6 && protocol
    }

    private val _data = Data()
    val loginEnableSignal = _data
    lateinit var codeEnableSignal: LiveData<Boolean>
    var codeCounterSignal = MutableLiveData<String>()
    private var _timer: Timer? = null

    fun setInput(phone: LiveData<String>, code: LiveData<String>, protocol: LiveData<Boolean>) {
        val phoneSignal = Transformations.map(phone) {
            _data.phone = it
        }

        codeEnableSignal = MediatorLiveData<Boolean>().apply {
            merge(Transformations.map(phoneSignal) { codeEnable },
                Transformations.map(codeCounterSignal) { codeEnable })
        }

        _data.merge(Transformations.map(phoneSignal) {
            _data.loginEnable
        }, Transformations.map(code) {
            _data.code = it
            _data.loginEnable
        }, Transformations.map(protocol) {
            _data.protocol = it
            _data.loginEnable
        })
    }

    /**
     * 获取验证码
     */
    suspend fun fetchCode() {
        startTimer()
        delay(3_000)
    }

    /**
     * 是否可以获取验证码
     */
    val codeEnable: Boolean
        get() = _data.phone?.length == 11 && _timer == null

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
                        codeCounterSignal.postValue("获取验证码")
                        return
                    }
                    codeCounterSignal.postValue("${codeCount}s")
                }
            }, 0, 1_000)
        }
    }
}