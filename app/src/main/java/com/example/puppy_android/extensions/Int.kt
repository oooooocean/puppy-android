package com.example.puppy_android.extensions

import com.example.puppy_android.services.App

val Int.dp: Float
    get() = App.context.resources.displayMetrics.density * this.toFloat()