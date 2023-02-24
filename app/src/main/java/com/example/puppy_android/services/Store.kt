package com.example.puppy_android.services

import android.content.Context
import android.content.SharedPreferences

enum class Store {
    Token
}

val Store.sp: SharedPreferences
    get() = App.context.getSharedPreferences("store", Context.MODE_PRIVATE)


fun <T> Store.set(value: T) = sp.edit().let {
    when (value) {
        is Int -> it.putInt(name, value)
        is String -> it.putString(name, value)
        is Boolean -> it.putBoolean(name, value)
    }
    it.apply()
}

inline fun <reified T> Store.get(): T? = sp.run {
    if (!contains(name)) return null
    return when (T::class.java) {
        Int::class.java -> getInt(name, 0) as T
        String::class.java -> getString(name, "") as T
        Boolean::class.java -> getBoolean(name, false) as T
        else -> null
    }
}