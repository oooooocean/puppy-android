package com.example.puppy_android.modules.user.info

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.versionedparcelable.ParcelUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.example.puppy_android.R
import com.example.puppy_android.databinding.ActivityUserEditBinding
import com.example.puppy_android.models.Gender
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.services.Store
import com.example.puppy_android.services.get
import com.example.puppy_android.tools.GlideEngine
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.d
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

class UserEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserEditBinding
    private lateinit var viewModel: UserEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserEditBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

//        val user = Gson().fromJson(intent.getStringExtra("user"), LoginUser::class.java)
        val user = Gson().fromJson(Store.LoginUser.get<String>(), LoginUser::class.java)

        viewModel = ViewModelProvider(this, UserEditViewModelFactory(user))[UserEditViewModel::class.java]

//        viewModel.setInput(flow {  })

//        lifecycleScope.launch {
//            viewModel.nextEnableFlow
//                .onStart { emit(true) }
//                .collect {
//                    binding.next.isEnabled = it
//                    binding.next.background =
//                        AppCompatResources.getDrawable(this@UserEditActivity, if (it) android.R.color.holo_orange_light else R.color.grey_border)
//                    binding.next.setTextColor(resources.getColor(if (it) android.R.color.white else R.color.black_primary, null))
//                }
//        }

        binding.avatar.setOnClickListener {
            Module.User.d("00000")
        }

        binding.man.setOnClickListener {
            Module.User.d("00000")
        }

        binding.next.setOnClickListener {
            Module.User.d("00000")
        }
    }

    private fun preparePickPhoto(v: View) {
        Module.User.d("00000: ${ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)}")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Module.User.d("1111: ${ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)}")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            return
        }
        pickPhoto()
    }

    private fun pickPhoto() {
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) return@registerForActivityResult
            PictureSelector.obtainMultipleResult(result.data).first()?.let {
                Glide.with(this).load(it.compressPath).into(binding.avatar)
            }
        }
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofImage())
            .imageEngine(GlideEngine)
            .forResult(launcher)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                Module.User.d("2222")
                if (!grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "请打开相册权限", Toast.LENGTH_SHORT).show()
                    return
                }
                pickPhoto()
            }
        }
    }

    private fun onClickAvatar(e: View) {

    }

    private fun onSelectGender(gender: Gender) {

    }

    private fun onInputCompletedNickname() {}

    private fun onInputCompletedIntroduction() {}
}