package com.example.puppy_android.tools

import com.google.gson.Gson
import com.google.gson.GsonBuilder


object GsonHelper {
    val gson: Gson by lazy {
        GsonBuilder()
            .setObjectToNumberStrategy { `in` ->
                `in`?.nextString()?.run {
                    if (this.contains("[.eE]".toRegex())) this.toDouble() else this.toInt()
                }
            }
            .create()
    }
}