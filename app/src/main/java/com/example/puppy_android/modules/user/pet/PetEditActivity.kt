package com.example.puppy_android.modules.user.pet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.puppy_android.databinding.ActivityPetEditBinding
import com.example.puppy_android.extensions.clickFlow
import com.example.puppy_android.extensions.setCorner
import com.example.puppy_android.tools.Loading
import com.example.puppy_android.tools.PicturePicker
import com.example.puppy_android.tools.SimpleLoadingHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class PetEditActivity : AppCompatActivity(), Loading by SimpleLoadingHelper() {
    private lateinit var binding: ActivityPetEditBinding
    private lateinit var viewModel: PetEditViewModel
    private val picturePicker by lazy { PicturePicker(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.infoPlane.setCorner(5F)

        binding.avatar.clickFlow
            .onEach { picturePicker.launch() }
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
    }
}