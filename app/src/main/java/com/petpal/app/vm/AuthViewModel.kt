package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.local.SessionManager
import com.petpal.app.data.model.User
import com.petpal.app.data.repo.AuthRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val role: String? = null,
    val status: String? = null,
    val error: String? = null,
    val isPending: Boolean = false,
    val isCheckingSession: Boolean = true
)

class AuthViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            sessionManager.tokenFlow.collect { token ->
                if (token != null) {
                    loadProfile()
                } else {
                    _state.value = AuthState(isCheckingSession = false)
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    loadProfile()
                }
                is Result.Error -> {
                    if (result.code == 403) {
                        _state.value = _state.value.copy(isLoading = false, isPending = true, error = null)
                    } else {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun register(email: String, password: String, fullName: String, phone: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.register(email, password, fullName, phone)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, isPending = true)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isCheckingSession = true)
            when (val result = repository.getProfile()) {
                is Result.Success -> {
                    val user = result.data
                    _state.value = AuthState(
                        isLoading = false,
                        isLoggedIn = true,
                        user = user,
                        role = user.role,
                        status = user.status,
                        isCheckingSession = false,
                        isPending = user.status == "pending"
                    )
                }
                is Result.Error -> {
                    sessionManager.clear()
                    _state.value = AuthState(isCheckingSession = false, error = result.message)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = AuthState(isCheckingSession = false)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearPending() {
        _state.value = _state.value.copy(isPending = false)
    }
}
