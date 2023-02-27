package com.example.puppy_android.extensions

import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

val EditText.textFlow: Flow<String>
    get() = callbackFlow {
        val watcher = addTextChangedListener {
            trySend(it.toString())
        }
        awaitClose {
            removeTextChangedListener(watcher)
        }
    }

val CheckBox.valueFlow: Flow<Boolean>
    get() = callbackFlow {
        setOnCheckedChangeListener { _, isChecked ->
            trySend(isChecked)
        }
        awaitClose()
    }