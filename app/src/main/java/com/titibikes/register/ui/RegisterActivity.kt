package com.titibikes.register.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.titibikes.R
import com.titibikes.databinding.ActivityRegisterBinding
import com.titibikes.register.ui.viewmodel.RegisterViewModel
import com.titibikes.register.ui.viewmodel.RegisterViewModelFactory
import com.titibikes.utils.Validator

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseAuth = FirebaseAuth.getInstance()
        registerViewModel = ViewModelProvider(
            this,
            RegisterViewModelFactory(firebaseAuth)
        )[RegisterViewModel::class.java]

        setupUi()

    }

    private fun setupUi() = binding.apply {
        btnRegister.setOnClickListener {
            val email = emailEditRegister.text.toString()
            val password = passwordEditRegister.text.toString()

            if (!Validator.validateEmail(email)) {
                emailEditRegister.error = "Preencha o E-mail corretamente"
                emailEditRegister.requestFocus()
                return@setOnClickListener
            }
            if (!Validator.validateSenha(password)) {
                passwordEditRegister.error = "Preencha a senha corretamente"
                passwordEditRegister.requestFocus()
                return@setOnClickListener
            }
            if (!email.contains("@")) {
                emailEditRegister.error = "Formato de E-mail Inválido"
                emailEditRegister.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordEditRegister.error = "A senha deve conter no mínimo 6 dígitos"
                passwordEditRegister.requestFocus()
                return@setOnClickListener
            }
            registerViewModel.registerUser(
                email = email,
                password = password,
            )
            registerViewModel.registrationResult.observe(this@RegisterActivity) { registrationResult ->
                if (registrationResult.success) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Conta registrada com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorMessage = registrationResult.errorMessage
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
            binding.progressBar.visibility = View.VISIBLE
        }
    }
}