package com.titibikes.login.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.*

class LoginViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    val loginResult: MutableLiveData<LoginResult> = MutableLiveData()

    fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            val exception = task.exception
            if (task.isSuccessful) {
                loginResult.value = LoginResult(success = true)
            } else {
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

    fun loginWithFacebook(accessToken: AccessToken?) {
        if (accessToken != null) {
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            val firebaseAuth = FirebaseAuth.getInstance()

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Autenticação bem-sucedida
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        loginResult.value = LoginResult(success = true, user = user)
                    } else {
                        // Autenticação falhou
                        val errorMessage = task.exception?.message ?: "Erro durante a autenticação"
                        loginResult.value = LoginResult(success = false, errorMessage = errorMessage)
                    }
                }
        } else {
            loginResult.value = LoginResult(success = false, errorMessage = "AccessToken inválido")
        }
    }
}

data class LoginResult(
    val success: Boolean, val errorMessage: String = "",
    val user: FirebaseUser? = null
)