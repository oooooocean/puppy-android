package com.example.puppy_android.extensions

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.example.puppy_android.services.App

/**
 * 设置圆角
 */
fun View.setCorner(radius: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            val correct = radius * App.context.resources.displayMetrics.density
            outline?.setRoundRect(0, 0, width, height, correct)
        }
    }
}