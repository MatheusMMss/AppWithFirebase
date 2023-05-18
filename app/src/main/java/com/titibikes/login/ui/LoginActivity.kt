package com.titibikes.login.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.titibikes.databinding.ActivityLoginBinding
import com.titibikes.home.ui.MainActivity
import com.titibikes.login.ui.viewmodel.LoginViewModel
import com.titibikes.register.ui.RegisterActivity
import com.titibikes.utils.Validator

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
        binding.registerCreate.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        }
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) {
                // Login bem-sucedido, realizar a autenticação no Firebase
                val token = result.accessToken
                loginViewModel.loginWithFacebook(token)
            }

            override fun onCancel() {
                // Login cancelado pelo usuário
                Toast.makeText(applicationContext, "Login cancelado pelo usuário", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                // Erro durante o login do Facebook
                Toast.makeText(applicationContext, "Erro durante o login do Facebook", Toast.LENGTH_SHORT).show()
            }
        })

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.loginResult.observe(this) { loginResult ->
            if (loginResult.success) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                val errorMessage = loginResult.errorMessage
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupUi() = binding.apply {
        btnLogin.setOnClickListener {
            val email = emailEditLogin.text.toString()
            val password = passwordEditLogin.text.toString()

            if (!Validator.validateEmail(email)) {
                emailEditLogin.error = "Preencha o E-mail corretamente"
                emailEditLogin.requestFocus()
                return@setOnClickListener
            }
            if (!Validator.validateSenha(password)) {
                passwordEditLogin.error = "Preencha a senha corretamente"
                passwordEditLogin.requestFocus()
                return@setOnClickListener
            }
            if (!email.contains("@")) {
                emailEditLogin.error = "Formato de E-mail Inválido"
                emailEditLogin.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordEditLogin.error = "A senha deve conter no mínimo 6 dígitos"
                passwordEditLogin.requestFocus()
                return@setOnClickListener
            }
            progressBar.visibility = View.VISIBLE
            loginViewModel.loginUser(email, password)
        }
    }
}

