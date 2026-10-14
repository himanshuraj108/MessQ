package com.messq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.firebase.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userName: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private var _demoLoggedIn = false
    val isLoggedIn: Boolean get() = repo.isLoggedIn || _demoLoggedIn

    fun demoLogin() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repo.login("student@bitepass.app", "password123")
            if (result.isSuccess) {
                val name = repo.getUserName()
                _authState.value = AuthState.Success(if (name.isNotEmpty() && name != "Student") name else "Himanshu Raj")
            } else {
                _demoLoggedIn = true
                _authState.value = AuthState.Success("Himanshu Raj")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter both email and password")
            return
        }
        val cleanEmail = email.trim()
        val cleanPass = password.trim()
        if (cleanEmail == "student@bitepass.app" && cleanPass == "password123") {
            demoLogin()
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repo.login(cleanEmail, cleanPass)
            if (result.isSuccess) {
                val name = repo.getUserName()
                _authState.value = AuthState.Success(name)
            } else {
                val rawMsg = result.exceptionOrNull()?.message ?: "Login failed"
                val friendlyMsg = if (rawMsg.contains("credential") || rawMsg.contains("malformed") || rawMsg.contains("expired") || rawMsg.contains("user-not-found")) {
                    "Invalid email or password. Tap 'One-Tap Demo Login' below to sign in instantly."
                } else {
                    rawMsg
                }
                _authState.value = AuthState.Error(friendlyMsg)
            }
        }
    }

    fun register(name: String, email: String, password: String, role: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repo.register(name.trim(), email.trim(), password.trim(), role)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(name.trim())
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        _demoLoggedIn = false
        repo.logout()
        _authState.value = AuthState.Idle
    }

    fun getUserName(onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult(repo.getUserName())
        }
    }

    fun getWalletBalance(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            onResult(repo.getWalletBalance())
        }
    }
}
