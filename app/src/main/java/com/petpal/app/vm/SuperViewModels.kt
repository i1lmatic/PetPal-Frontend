package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.*
import com.petpal.app.data.repo.AdminRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ManageUsersState(
    val isLoading: Boolean = false,
    val pendingUsers: List<User> = emptyList(),
    val activeUsers: List<User> = emptyList(),
    val error: String? = null
)

class ManageUsersViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(ManageUsersState())
    val state: StateFlow<ManageUsersState> = _state.asStateFlow()

    fun loadPending() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getPendingUsers()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, pendingUsers = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun loadActive() {
        viewModelScope.launch {
            when (val r = repo.getActiveUsers()) {
                is Result.Success -> _state.value = _state.value.copy(activeUsers = r.data)
                is Result.Error -> {}
            }
        }
    }

    fun approve(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.approveUser(userId)) {
                is Result.Success -> loadPending()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun reject(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.rejectUser(userId)) {
                is Result.Success -> loadPending()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun deactivate(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.deactivateUser(userId)) {
                is Result.Success -> { loadActive(); loadPending() }
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun reactivate(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.reactivateUser(userId)) {
                is Result.Success -> { loadActive(); loadPending() }
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
}

data class ManageVetsState(
    val isLoading: Boolean = false,
    val vets: List<Veterinary> = emptyList(),
    val pendingVets: List<PendingVetOut> = emptyList(),
    val error: String? = null
)

class ManageVetsViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(ManageVetsState())
    val state: StateFlow<ManageVetsState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getAdminVets()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, vets = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun loadPending() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getPendingVets()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, pendingVets = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun approveVet(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.approveUser(userId)) {
                is Result.Success -> loadPending()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun rejectVet(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.rejectUser(userId)) {
                is Result.Success -> loadPending()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun deactivate(vetId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.deactivateVet(vetId)) {
                is Result.Success -> load()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun reactivate(vetId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val r = repo.reactivateVet(vetId)) {
                is Result.Success -> load()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
}
