package com.titibikes.login.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.titibikes.databinding.ActivityLoginBinding
import com.titibikes.login.ui.viewmodel.LoginViewModel
import com.titibikes.utils.Validator

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.loginResult.observe(this) { loginResult ->
            if (loginResult.success) {
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
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

