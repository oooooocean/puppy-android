package com.example.puppy_android.modules.user.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.puppy_android.models.Gender
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.net.Net
import com.example.puppy_android.net.UserService
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.set
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.d
import com.google.gson.Gson
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import retrofit2.await

class UserEditViewModelFactory(private val user: LoginUser, private val input: Flow<Any?>) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = UserEditViewModel(user, input) as T
}

class UserEditViewModel(private val user: LoginUser, input: Flow<Any?>) : ViewModel() {
    data class Info(
        var nickname: String? = null, var gender: Gender? = null,
        var avatar: LocalMedia? = null, var introduction: String? = null
    ) {
        /**
         * 是否完成资料填写
         */
        val isCompleted: Boolean
            get() = nickname != null && nickname!!.length >= 2 &&
                    gender != null &&
                    avatar != null &&
                    introduction != null && introduction!!.length >= 10

        val jsonMap: Map<String, Any>
            get() = mapOf("nickname" to nickname!!, "gender" to gender!!.ordinal, "introduction" to introduction!!)
    }

    var nextEnableFlow = input.map { info.isCompleted }.distinctUntilChanged()
    val info = Info()
    private val isEdit = user.info != null

    fun submitUserInfo(): Flow<Unit> =
        callbackFlow {
            val gson = Gson()
            val api = Net.create<UserService>().run {
                if (isEdit) editUser(user.id, info.jsonMap) else addUser(user.id, info.jsonMap)
            }
            user.info = api.await().data
            Store.LoginUser.set(gson.toJson(user))
            this.trySend(Unit)
            awaitClose()
        }

}