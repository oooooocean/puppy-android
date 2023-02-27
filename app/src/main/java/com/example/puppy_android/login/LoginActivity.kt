package com.example.puppy_android.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.puppy_android.R
import com.example.puppy_android.databinding.ActivityLoginBinding
import com.example.puppy_android.extensions.textFlow
import com.example.puppy_android.extensions.valueFlow
import com.example.puppy_android.models.Pandora
import com.example.puppy_android.tools.Loading
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.SimpleLoadingHelper
import com.example.puppy_android.tools.d
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

        viewModel = ViewModelProvider(this)[LoginViewModel2::class.java]
        viewModel.setInput(
            phone = binding.phoneEditText.textFlow,
            code = binding.codeEditText.textFlow,
            protocol = binding.protocolButton.valueFlow
        )



        lifecycleScope.launch {
            launch {
                // 按钮是否可点击
                viewModel.loginEnableFlow.collect {
                    binding.loginButton.isEnabled = it
                    binding.loginButton.background =
                        AppCompatResources.getDrawable(this@LoginActivity, if (it) android.R.color.holo_orange_light else R.color.grey_border)
                    binding.loginButton.setTextColor(resources.getColor(if (it) android.R.color.white else R.color.black_primary, null))
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
                viewModel.codeCounterFlow.flowWithLifecycle(lifecycle).collect {
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
                Toast.makeText(this@LoginActivity, "登录成功", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Module.Login.d(e.toString())
                Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
            } finally {
                dismissLoading()
            }
        }
    }
}