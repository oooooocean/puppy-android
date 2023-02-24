package com.example.puppy_android.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.puppy_android.R
import com.example.puppy_android.databinding.ActivityLoginBinding
import com.google.gson.Gson
import extensions.textChangeSignal
import extensions.valueChangeSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.puppy_android.models.LoginUser
import com.example.puppy_android.models.Pandora
import com.example.puppy_android.net.LoginService
import com.example.puppy_android.net.Net
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.puppy_android.tools.Loading
import com.example.puppy_android.tools.Module
import com.example.puppy_android.tools.SimpleLoadingHelper
import com.example.puppy_android.tools.d
import kotlinx.coroutines.async

class LoginActivity : AppCompatActivity(), Loading by SimpleLoadingHelper() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.setInput(
            phone = binding.phoneEditText.textChangeSignal,
            code = binding.codeEditText.textChangeSignal,
            protocol = binding.protocolButton.valueChangeSignal
        )

        // 按钮是否可点击
        viewModel.loginEnableSignal.observe(this) {
            binding.loginButton.isEnabled = it
            binding.loginButton.background = AppCompatResources.getDrawable(this, if (it) android.R.color.holo_orange_light else R.color.grey_border)
            binding.loginButton.setTextColor(resources.getColor(if (it) android.R.color.white else R.color.black_primary, null))
        }

        // 验证码是否可点击
        viewModel.codeEnableSignal.observe(this) {
            binding.codeButton.isEnabled = it
            binding.codeButton.setTextColor(resources.getColor(if (it) android.R.color.holo_orange_light else R.color.black_primary, null))
        }

        // 验证码文案
        viewModel.codeCounterSignal.observe(this) {
            binding.codeButton.text = it
        }

        // 验证码
        binding.codeButton.setOnClickListener(::fetchCode)

        // 登录按钮
        binding.loginButton.setOnClickListener(::login)
    }

    private fun fetchCode(v: View) {
        showLoading(this)
        lifecycleScope.launch {
            withContext(Dispatchers.Default) { viewModel.fetchCode() }
            dismissLoading()
        }
    }

    private fun login(v: View) {
        showLoading(this)
        lifecycleScope.launch {
            try {
                val loginUser = withContext(Dispatchers.Default) { viewModel.login() }
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