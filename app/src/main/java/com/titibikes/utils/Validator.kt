package com.titibikes.utils

object Validator {

    fun validateEmail(email: String): Boolean {
        return !(email.isEmpty() || email.isBlank())
    }

    fun validateSenha(senha: String): Boolean {
        return !(senha.isEmpty() || senha.isBlank())
    }
}