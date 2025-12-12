package com.example.nutricheckproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun checkCurrentUser() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Success
        }
    }

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Campos vacíos")
            return
        }
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("El formato del correo electrónico no es válido")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.signInWithEmailAndPassword(email, pass).await()
                _authState.value = AuthState.Success
            } catch (_: Exception) {
                _authState.value = AuthState.Error( "Error al iniciar sesión. Revisa los datos ingresados")
            }
        }
    }

    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Campos vacíos")
            return
        }
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("El formato del correo electrónico no es válido")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.createUserWithEmailAndPassword(email, pass).await()
                _authState.value = AuthState.Registered
            } catch (_: Exception) {
                _authState.value = AuthState.Error("Error al registrar")
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Ingresa tu correo para restablecer")
            return
        }
        if (!isEmailValid(email)) {
            _authState.value = AuthState.Error("El formato del correo electrónico no es válido")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.PasswordResetSent
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Error al enviar correo")
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        object Registered : AuthState()
        object PasswordResetSent : AuthState()
        data class Error(val message: String) : AuthState()
    }
}