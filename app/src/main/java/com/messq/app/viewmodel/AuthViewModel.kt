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

    val isLoggedIn: Boolean get() = repo.isLoggedIn

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repo.login(email, password)
            if (result.isSuccess) {
                val name = repo.getUserName()
                _authState.value = AuthState.Success(name)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
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
            val result = repo.register(name, email, password, role)
            if (result.isSuccess) {
                _authState.value = AuthState.Success(name)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
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
