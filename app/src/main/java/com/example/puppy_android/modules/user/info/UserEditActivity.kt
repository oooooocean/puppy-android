package com.example.puppy_android.modules.user.info

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.puppy_android.R
import com.example.puppy_android.databinding.ActivityUserEditBinding
import com.example.puppy_android.extensions.clickFlow
import com.example.puppy_android.extensions.dp
import com.example.puppy_android.extensions.setCorner
import com.example.puppy_android.extensions.textFlow
import com.example.puppy_android.models.Gender
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.get
import com.example.puppy_android.tools.*
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class UserEditActivity : AppCompatActivity(), Loading by SimpleLoadingHelper() {
    private lateinit var binding: ActivityUserEditBinding
    private lateinit var viewModel: UserEditViewModel
    private val picturePicker by lazy { PicturePicker(this) }

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        val genderFlow = merge(
            binding.man.clickFlow.map { Gender.MALE },
            binding.woman.clickFlow.map { Gender.FEMALE }
        ).shareIn(lifecycleScope, SharingStarted.WhileSubscribed())

        initViewModel(genderFlow)

        viewModel.nextEnableFlow
            .onEach {
                Module.User.d(it.toString())
                binding.next.isEnabled = it
                binding.next.background =
                    AppCompatResources.getDrawable(this@UserEditActivity, if (it) android.R.color.holo_orange_light else R.color.grey_border)
                binding.next.setTextColor(resources.getColor(if (it) android.R.color.white else R.color.black_primary, null))
            }
            .launchIn(lifecycleScope)

        binding.avatar.clickFlow
            .onEach { preparePickPhoto() }
            .launchIn(lifecycleScope)

        picturePicker.flow
            .mapNotNull { it?.first() }
            .onEach {
                binding.avatar.setPadding(0)
                Glide.with(this)
                    .load(it.path)
                    .into(binding.avatar)
            }
            .launchIn(lifecycleScope)

        genderFlow
            .onEach { onSelectGender(it) }
            .launchIn(lifecycleScope)

        binding.information.textFlow
            .map { it.length }
            .onEach { binding.informationCounter.text = "$it/50" }
            .launchIn(lifecycleScope)

        binding.next.clickFlow
            .flatMapConcat {
                showLoading(this)
                viewModel.submitUserInfo()
            }
            .catch {
                Toast.makeText(this@UserEditActivity, it.toString(), Toast.LENGTH_SHORT).show()
                dismissLoading()
            }
            .onEach {
                Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show()
                dismissLoading()
            }
            .launchIn(lifecycleScope)
    }

    private fun initUI() {
        setSupportActionBar(binding.toolbar)
        binding.avatar.setCorner(40.dp)
    }

    private fun initViewModel(genderFlow: Flow<Gender>) {
//        val user = Gson().fromJson(intent.getStringExtra("user"), LoginUser::class.java)
        val user = LoginUser.cached!!
        val input = merge(
            binding.information.textFlow.onEach { viewModel.info.introduction = it },
            binding.nickname.textFlow.onEach { viewModel.info.nickname = it },
            genderFlow.onEach { viewModel.info.gender = it },
            picturePicker.flow.filter { it != null }.onEach { viewModel.info.avatar = it?.first() }
        )

        viewModel = ViewModelProvider(this, UserEditViewModelFactory(user, input))[UserEditViewModel::class.java]
    }

    private fun preparePickPhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            return
        }
        picturePicker.launch()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (!grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "请打开相册权限", Toast.LENGTH_SHORT).show()
                    return
                }
                picturePicker.launch()
            }
        }
    }

    private fun onSelectGender(gender: Gender) {
        val selected = if (gender == Gender.MALE) binding.man else binding.woman
        val unselect = if (gender == Gender.MALE) binding.woman else binding.man
        val modify = { view: ImageView, color: Int ->
            view.run {
                background.setTint(color)
                drawable.setTint(color)
            }
        }
        modify(selected, resources.getColor(R.color.orange, null))
        modify(unselect, resources.getColor(R.color.grey_hint, null))
    }
}