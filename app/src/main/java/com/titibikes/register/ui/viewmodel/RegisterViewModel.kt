package com.titibikes.register.ui.viewmodel

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {
    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> = _registrationResult

    fun registerUser(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                // Registration successful
                _registrationResult.postValue(RegistrationResult(success = true))
            } catch (e: FirebaseAuthException) {
                // Registration failed
                val errorMessage = "E-mail inválido ou mal formatado"
                _registrationResult.postValue(RegistrationResult(success = false, errorMessage = errorMessage))
            }
        }
    }
}

data class RegistrationResult(val success: Boolean, val errorMessage: String? = null)

class RegisterViewModelFactory(private val firebaseAuth: FirebaseAuth) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(firebaseAuth) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}