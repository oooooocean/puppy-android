package tools

import android.content.Context

interface Loading {
    fun showLoading(context: Context, cancelable: Boolean = false)
    fun dismissLoading()
}