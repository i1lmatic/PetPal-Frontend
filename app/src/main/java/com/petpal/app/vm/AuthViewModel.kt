package com.petpal.app.vm

import android.util.Log
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
        Log.d("PetPalFlow", "VM: AuthViewModel creado")
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            Log.d("PetPalFlow", "VM: checkSession iniciado")
            val token = sessionManager.getToken()
            Log.d("PetPalFlow", "VM: token from DataStore=${if (token != null) "presente" else "null"}")
            if (token != null) {
                sessionManager.setTokenCache(token)
                Log.d("PetPalFlow", "VM: checkSession -> loadProfile")
                loadProfile()
            } else {
                Log.d("PetPalFlow", "VM: checkSession -> no token, login")
                _state.value = AuthState(isCheckingSession = false)
            }
        }
    }

    fun login(email: String, password: String) {
        Log.d("PetPalFlow", "VM: 1. login() llamado email=$email")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            Log.d("PetPalFlow", "VM: 2. llamando repository.login()")
            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    Log.d("PetPalFlow", "VM: 3. login SUCCESS -> loadProfile()")
                    loadProfile()
                }
                is Result.Error -> {
                    Log.e("PetPalFlow", "VM: 3. login ERROR code=${result.code} msg=${result.message}")
                    if (result.code == 403) {
                        _state.value = _state.value.copy(isLoading = false, isPending = true, error = null)
                        Log.d("PetPalFlow", "VM: 3a. cuenta pendiente -> isPending=true")
                    } else {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                        Log.d("PetPalFlow", "VM: 3b. error mostrado en pantalla")
                    }
                }
            }
        }
    }

    fun register(email: String, password: String, fullName: String, phone: String) {
        Log.d("PetPalFlow", "VM: 1. register() llamado email=$email")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            Log.d("PetPalFlow", "VM: 2. llamando repository.register()")
            when (val result = repository.register(email, password, fullName, phone)) {
                is Result.Success -> {
                    val user = result.data
                    Log.d("PetPalFlow", "VM: 3. register SUCCESS status=${user.status}")
                    if (user.status == "pending") {
                        _state.value = _state.value.copy(isLoading = false, isPending = true)
                    } else {
                        login(email, password)
                    }
                }
                is Result.Error -> {
                    Log.e("PetPalFlow", "VM: 3. register ERROR msg=${result.message}")
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun registerVet(
        email: String, password: String, fullName: String, phone: String,
        businessName: String = "", businessAddress: String = "", businessPhone: String = "",
        businessSpecialties: String = "", businessDescription: String? = null,
        businessWorkingHours: String? = null
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.registerVet(
                email, password, fullName, phone,
                businessName, businessAddress, businessPhone,
                businessSpecialties, businessDescription, businessWorkingHours
            )) {
                is Result.Success -> {
                    val user = result.data
                    val pending = user.status == "pending"
                    _state.value = _state.value.copy(isLoading = false, isPending = pending)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun loadProfile() {
        Log.d("PetPalFlow", "VM: loadProfile() iniciado")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isCheckingSession = true)
            Log.d("PetPalFlow", "VM: loadProfile llamando repository.getProfile()")
            when (val result = repository.getProfile()) {
                is Result.Success -> {
                    val user = result.data
                    Log.d("PetPalFlow", "VM: loadProfile SUCCESS role=${user.role} status=${user.status}")
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
                    Log.e("PetPalFlow", "VM: loadProfile ERROR -> limpiando sesion msg=${result.message}")
                    sessionManager.clear()
                    _state.value = AuthState(isCheckingSession = false, error = result.message)
                }
            }
        }
    }

    fun logout() {
        Log.d("PetPalFlow", "VM: logout()")
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
