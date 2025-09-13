package com.ayaan.incompletion.presentation.auth.signup

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _signUpState = mutableStateOf<SignUpState>(SignUpState.Idle)
    val signUpState: State<SignUpState> get() = _signUpState

    suspend fun signUpWithEmailPassword(
        phone:String,
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        gender: String
    ) {
        if (!validateSignUpFields(fullName, email, password, confirmPassword)) {
            return
        }

        _signUpState.value = SignUpState.Loading
        try {
            // Create user in Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            // Update user profile with full name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build()

            user?.updateProfile(profileUpdates)?.await()

            if (user != null) {
                // Store user data in Firestore
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "phone" to phone,
                    "name" to fullName,
                    "email" to email,
                    "gender" to gender,
                    "password" to password
                )

                Log.d("SignUpViewModel", "User data stored in Firestore for user: ${user.uid}")
                _signUpState.value = SignUpState.Success
                Log.d("SignUpViewModel", "Sign-up successful for user: ${user.uid}")
            } else {
                _signUpState.value = SignUpState.Error("Failed to create user")
                Log.e("SignUpViewModel", "Sign-up failed: user is null")
            }
        } catch (e: Exception) {
            val errorMessage = when (e.message) {
                "The email address is already in use by another account." -> "Email already in use"
                "The email address is badly formatted." -> "Invalid email format"
                "The given password is invalid. [ Password should be at least 6 characters ]" ->
                    "Password must be at least 6 characters"
                else -> e.localizedMessage ?: "An unknown error occurred"
            }
            _signUpState.value = SignUpState.Error(errorMessage)
            Log.e("SignUpViewModel", "Sign-up failed: ${e.message}")
        }
    }

    private fun validateSignUpFields(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        when {
            fullName.isBlank() -> {
                _signUpState.value = SignUpState.Error("Full name is required")
                return false
            }
            email.isBlank() -> {
                _signUpState.value = SignUpState.Error("Email is required")
                return false
            }
            password.isBlank() -> {
                _signUpState.value = SignUpState.Error("Password is required")
                return false
            }
            password.length < 6 -> {
                _signUpState.value = SignUpState.Error("Password must be at least 6 characters")
                return false
            }
            password != confirmPassword -> {
                _signUpState.value = SignUpState.Error("Passwords do not match")
                return false
            }
        }
        return true
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}