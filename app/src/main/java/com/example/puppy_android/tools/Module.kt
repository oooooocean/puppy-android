package com.example.puppy_android.tools

import android.util.Log

enum class Module {
    Login,
    Net;

    override fun toString(): String =
        when (this) {
            Login -> "\uD83D\uDC68\uD83C\uDFFB登录模块"
            Net -> "\uD83C\uDF0F网络模块"
        }

}

fun Module.d(msg: String) {
    Log.d(name, "$this:\n$msg\n")
}