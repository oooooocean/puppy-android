package extensions

import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

val EditText.textChangeSignal: LiveData<String>
    get() =
        MutableLiveData<String>().apply {
            addTextChangedListener {
                this.value = it.toString()
            }
        }

val CheckBox.valueChangeSignal: LiveData<Boolean>
    get() =
        MutableLiveData<Boolean>().apply {
            setOnCheckedChangeListener { _, selected ->
                this.value = selected
            }
        }
