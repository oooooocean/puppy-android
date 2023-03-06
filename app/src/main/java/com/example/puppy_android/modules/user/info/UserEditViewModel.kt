package com.example.puppy_android.modules.user.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.puppy_android.models.Gender
import com.example.puppy_android.models.LoginUser
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class UserEditViewModelFactory(private val user: LoginUser, private val input: Flow<Any?>): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = UserEditViewModel(user, input) as T
}

class UserEditViewModel(private val user: LoginUser, input: Flow<Any?>): ViewModel() {
    data class Info(var nickname: String? = null, var gender: Gender? = null,
                    var avatar: LocalMedia? = null, var introduction: String? = null) {
        /**
         * 是否完成资料填写
         */
        val isCompleted: Boolean
            get() = nickname != null && nickname!!.length >= 2 &&
                    gender != null &&
                    avatar != null &&
                    introduction != null && introduction!!.length >= 10
    }

    var nextEnableFlow = input.map { info.isCompleted }.distinctUntilChanged()
    val info = Info()
}