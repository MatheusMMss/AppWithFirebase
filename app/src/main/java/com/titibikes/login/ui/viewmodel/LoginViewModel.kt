package com.titibikes.login.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    val loginResult: MutableLiveData<LoginResult> = MutableLiveData()

    fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val exception = task.exception
                if (exception is FirebaseAuthInvalidUserException) {
                    val errorCode = exception.errorCode
                    if (errorCode == "ERROR_USER_NOT_FOUND") {
                        loginResult.value =
                            LoginResult(success = false, errorMessage = "Esta conta não existe")
                        return@addOnCompleteListener
                    }
                }
                val errorMessage = exception?.message ?: "Erro desconhecido"
                loginResult.value = LoginResult(success = false, errorMessage = errorMessage)
            }
    }
}
data class LoginResult(
    val success: Boolean,
    val errorMessage: String = ""
)