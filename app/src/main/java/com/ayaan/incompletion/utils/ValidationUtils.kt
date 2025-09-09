package com.ayaan.incompletion.utils

//object ValidationUtils {
//    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$")
//    private val passwordSpecialCharRegex = Regex("[!@#\$%^&*(),.?\":{}|<>]")
//    private val passwordNumberRegex = Regex("[0-9]")
//    private const val MIN_PASSWORD_LENGTH = 8
//
//    fun validateEmail(email: String): String? {
//        return when {
//            email.isBlank() -> "Email cannot be empty."
//            !emailRegex.matches(email) -> "Invalid email format."
//            else -> null
//        }
//    }
//
//    fun validatePassword(password: String): String? {
//        return when {
//            password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters."
//            !passwordSpecialCharRegex.containsMatchIn(password) -> "Password must contain a special character."
//            !passwordNumberRegex.containsMatchIn(password) -> "Password must contain a number."
//            else -> null
//        }
//    }
//
//    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
//        return when {
//            confirmPassword.isBlank() -> "Please confirm your password."
//            password != confirmPassword -> "Passwords do not match."
//            else -> null
//        }
//    }
//}

