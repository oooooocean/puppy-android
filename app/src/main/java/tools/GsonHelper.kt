package tools

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberStrategy
import com.google.gson.internal.bind.NumberTypeAdapter
import com.google.gson.internal.bind.ObjectTypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken


object GsonHelper {
    val gson: Gson by lazy {
        GsonBuilder()
            .setObjectToNumberStrategy { `in` ->
                `in`?.nextString()?.run {
                    if (this.contains(Regex("[.eE]"))) this.toDouble() else this.toInt()
                }
            }
            .create()
    }
}