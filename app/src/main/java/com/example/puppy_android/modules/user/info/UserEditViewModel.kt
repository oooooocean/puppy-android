package com.example.puppy_android.modules.user.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.puppy_android.models.LoginUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserEditViewModelFactory(private val user: LoginUser): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserEditViewModel(user) as T
    }
}

class UserEditViewModel(private val user: LoginUser): ViewModel() {
    lateinit var nextEnableFlow: Flow<Boolean>

    fun setInput(userInfoChangedFlow: Flow<Unit>) {
        nextEnableFlow = userInfoChangedFlow.map { user.isCompleted }
    }
}

val LoginUser.isCompleted: Boolean
    get() = info != null && info.nickname.length > 5 && info.introduction.length > 10