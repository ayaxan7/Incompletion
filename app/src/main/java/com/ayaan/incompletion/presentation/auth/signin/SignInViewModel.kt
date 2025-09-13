package com.ayaan.incompletion.presentation.auth.signin

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _signInState = mutableStateOf<SignInState>(SignInState.Idle)
    val signInState: State<SignInState> get() = _signInState

    // Sign in with email/password
    suspend fun signInWithEmailPassword(email: String, password: String) {
        _signInState.value = SignInState.Loading
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user: FirebaseUser? = result.user
            if (user != null) {
                _signInState.value = SignInState.Success
                Log.d("SignInViewModel", "Sign-in successful")
            } else {
                _signInState.value = SignInState.Error("Sign-in failed")
                Log.d("SignInViewModel", "Sign-in failed")
            }
        } catch (e: Exception) {
            _signInState.value = SignInState.Error(e.localizedMessage ?: "Unknown error")
            Log.e("SignInViewModel", "Sign-in failed: ${e.localizedMessage}")
        }
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    object Success : SignInState()
    data class Error(val message: String) : SignInState()
}