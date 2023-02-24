package extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

fun <T> MediatorLiveData<T>.merge(vararg sources: LiveData<T>) {
    sources.forEach {
        addSource(it) { item ->
            value = item
        }
    }
}