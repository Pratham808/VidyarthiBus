package com.example.vidyarthi_bus.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            viewModelScope.launch { _loginEvent.emit(LoginEvent.Error("Email and password cannot be empty")) }
            return
        }

        _isLoading.value = true
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            _loginEvent.emit(LoginEvent.Success)
                        } else {
                            val errorMsg = task.exception?.message ?: "Login failed"
                            Log.e("LoginViewModel", "Login error: $errorMsg")
                            _loginEvent.emit(LoginEvent.Error(errorMsg))
                        }
                    }
                }
        } catch (e: Exception) {
            _isLoading.value = false
            Log.e("LoginViewModel", "Firebase Login Exception", e)
            viewModelScope.launch { _loginEvent.emit(LoginEvent.Error("Authentication Service Error")) }
        }
    }

    sealed class LoginEvent {
        object Success : LoginEvent()
        data class Error(val message: String) : LoginEvent()
    }
}