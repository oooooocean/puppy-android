package com.example.puppy_android.modules.login

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.*
import com.example.puppy_android.R
import com.example.puppy_android.databinding.ActivityLoginBinding
import com.example.puppy_android.extensions.setCorner
import com.example.puppy_android.extensions.textFlow
import com.example.puppy_android.extensions.valueFlow
import com.example.puppy_android.models.Pandora
import com.example.puppy_android.modules.user.info.UserEditActivity
import com.example.puppy_android.tools.Loading
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.SimpleLoadingHelper
import com.example.puppy_android.tools.d
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

typealias LoginResult = Pandora<Map<String, Any>>

class LoginActivity : AppCompatActivity(), Loading by SimpleLoadingHelper() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setCorner(22F)
        binding.protocol.text = SpannableString("同意用户协议和隐私政策").apply {
            setSpan(StyleSpan(Typeface.BOLD), 2, 6, 0)
            setSpan(UnderlineSpan(), 2, 6, 0)
            setSpan(StyleSpan(Typeface.BOLD), 7, 11, 0)
            setSpan(UnderlineSpan(), 7, 11, 0)
        }

        viewModel = ViewModelProvider(this)[LoginViewModel2::class.java]
        viewModel.setInput(
            phone = binding.phoneEditText.textFlow,
            code = binding.codeEditText.textFlow,
            protocol = binding.protocolButton.valueFlow
        )

        lifecycleScope.launchWhenResumed {
            launch {
                // 按钮是否可点击
                viewModel.loginEnableFlow.collect {
                    binding.loginButton.isEnabled = it
                }
            }

            launch {
                // 验证码是否可点击
                viewModel.codeEnableFlow.collect {
                    binding.codeButton.isEnabled = it
                    binding.codeButton.setTextColor(resources.getColor(if (it) android.R.color.holo_orange_light else R.color.black_primary, null))
                }
            }

            launch {
                // 验证码文案
                viewModel.codeCounterFlow.collect {
                    binding.codeButton.text = it
                }
            }
        }

        // 验证码
        binding.codeButton.setOnClickListener(::fetchCode)

        // 登录按钮
        binding.loginButton.setOnClickListener(::login)
    }

    private fun fetchCode(v: View) {
        showLoading(this)
        lifecycleScope.launch {
            viewModel.fetchCode()
            dismissLoading()
        }
    }

    private fun login(v: View) {
        showLoading(this)
        lifecycleScope.launch {
            try {
                val loginUser = coroutineScope { viewModel.login() }
                Module.Login.d(loginUser.toString())
                val intent = Intent(this@LoginActivity, UserEditActivity::class.java).apply {
                    putExtra("user", Gson().toJson(loginUser))
                }
                startActivity(intent)
            } catch (e: Exception) {
                Module.Login.d(e.toString())
                Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
            } finally {
                dismissLoading()
            }
        }
    }
}