package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.*
import com.petpal.app.data.repo.AdminRepository
import com.petpal.app.data.repo.AuthRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val error: String? = null
)

class DashboardViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getDashboardStats()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, stats = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }
}

data class AllPetsState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val error: String? = null
)

class AllPetsViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(AllPetsState())
    val state: StateFlow<AllPetsState> = _state.asStateFlow()

    fun load(search: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getAllPets(search)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, pets = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }
}

data class ActiveUsersState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val error: String? = null
)

class ActiveUsersViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(ActiveUsersState())
    val state: StateFlow<ActiveUsersState> = _state.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getActiveUsers()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, users = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }
}

data class ClientDetailState(
    val isLoading: Boolean = false,
    val user: UserDetail? = null,
    val error: String? = null
)

class ClientDetailViewModel(private val repo: AdminRepository) : ViewModel() {
    private val _state = MutableStateFlow(ClientDetailState())
    val state: StateFlow<ClientDetailState> = _state.asStateFlow()

    fun load(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = repo.getUserDetail(userId)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, user = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }
}

data class EditProfileState(
    val isLoading: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

class EditProfileViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow(EditProfileState())
    val state: StateFlow<EditProfileState> = _state.asStateFlow()

    fun save(fullName: String?, phone: String?) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, saved = false)
            when (val r = repo.updateProfile(fullName, phone)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, saved = true)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun onSavedHandled() { _state.value = _state.value.copy(saved = false) }
    fun clearError() { _state.value = _state.value.copy(error = null) }
}
