package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Authenticated(val user: FirebaseUser?) : AuthUiState
    data class Error(val message: String, val isFirebasePending: Boolean = false) : AuthUiState
    object PasswordResetSent : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var fullName by mutableStateOf("")
    var rememberMe by mutableStateOf(true)
    var termsAccepted by mutableStateOf(false)

    var isPasswordVisible by mutableStateOf(false)
    var isConfirmPasswordVisible by mutableStateOf(false)

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            _uiState.value = AuthUiState.Authenticated(user)
        }
    }

    fun isFirebaseConfigured(): Boolean {
        return authRepository.isFirebaseConfigured()
    }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter both email and password.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }

        if (!authRepository.isFirebaseConfigured()) {
            _uiState.value = AuthUiState.Error(
                "Firebase Authentication setup is pending. Configure Firebase google-services.json to enable live login, or use Demo Bypass mode.",
                isFirebasePending = true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.login(email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Authenticated(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Login failed. Please check your credentials.")
                }
            )
        }
    }

    fun register() {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all required fields.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters long.")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Passwords do not match.")
            return
        }
        if (!termsAccepted) {
            _uiState.value = AuthUiState.Error("You must accept the Terms of Service and Privacy Policy.")
            return
        }

        if (!authRepository.isFirebaseConfigured()) {
            _uiState.value = AuthUiState.Error(
                "Firebase Authentication setup is pending. Configure Firebase google-services.json to enable live registration, or use Demo Bypass mode.",
                isFirebasePending = true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.register(fullName.trim(), email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = AuthUiState.Authenticated(user)
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Registration failed. Please try again.")
                }
            )
        }
    }

    fun sendPasswordResetEmail() {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email address first.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }

        if (!authRepository.isFirebaseConfigured()) {
            _uiState.value = AuthUiState.Error(
                "Firebase setup is pending. Password reset requires Firebase configuration.",
                isFirebasePending = true
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.sendPasswordReset(email.trim())
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.PasswordResetSent
                },
                onFailure = { error ->
                    _uiState.value = AuthUiState.Error(error.localizedMessage ?: "Failed to send password reset email.")
                }
            )
        }
    }

    fun bypassAuthForDemo() {
        // Allows testing the app UI without Firebase configured yet
        _uiState.value = AuthUiState.Authenticated(null)
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
        email = ""
        password = ""
        confirmPassword = ""
        fullName = ""
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}
