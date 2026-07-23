package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.Appointment
import com.petpal.app.data.model.MedicalRecord
import com.petpal.app.data.model.User
import com.petpal.app.data.repo.AdminRepository
import com.petpal.app.data.repo.PetRepository
import com.petpal.app.data.repo.VetRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminState(
    val isLoading: Boolean = false,
    val pendingUsers: List<User> = emptyList(),
    val allAppointments: List<Appointment> = emptyList(),
    val error: String? = null,
    val userApproved: Boolean = false,
    val statusUpdated: Boolean = false
)

class AdminViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    fun loadPendingUsers() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getPendingUsers()) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, pendingUsers = result.data
                )
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun approveUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.approveUser(userId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, userApproved = true)
                    loadPendingUsers()
                }
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun loadAllAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getAllAppointments()) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, allAppointments = result.data
                )
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun updateAppointmentStatus(appointmentId: Int, newStatus: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null, statusUpdated = false)
            when (val result = repository.updateAppointmentStatus(appointmentId, newStatus)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(statusUpdated = true)
                    loadAllAppointments()
                }
                is Result.Error -> _state.value = _state.value.copy(error = result.message)
            }
        }
    }

    fun rejectUser(userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            when (val result = repository.rejectUser(userId)) {
                is Result.Success -> loadPendingUsers()
                is Result.Error -> _state.value = _state.value.copy(error = result.message)
            }
        }
    }
}

data class PetDetailState(
    val isLoading: Boolean = false,
    val records: List<MedicalRecord> = emptyList(),
    val error: String? = null
)

class PetDetailViewModel(private val repository: PetRepository) : ViewModel() {

    private val _state = MutableStateFlow(PetDetailState())
    val state: StateFlow<PetDetailState> = _state.asStateFlow()

    fun loadHistory(petId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getPetHistory(petId)) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, records = result.data
                )
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class MedicalRecordState(
    val isLoading: Boolean = false,
    val created: Boolean = false,
    val error: String? = null
)

class AddMedicalRecordViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _state = MutableStateFlow(MedicalRecordState())
    val state: StateFlow<MedicalRecordState> = _state.asStateFlow()

    fun createRecord(petId: Int, diagnosis: String, treatment: String, notes: String, appointmentId: Int? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, created = false)
            when (val result = repository.createMedicalRecord(petId, diagnosis, treatment, notes, appointmentId)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, created = true)
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onCreatedHandled() {
        _state.value = _state.value.copy(created = false)
    }
}

class VetAddMedicalRecordViewModel(private val vetRepo: VetRepository) : ViewModel() {

    private val _state = MutableStateFlow(MedicalRecordState())
    val state: StateFlow<MedicalRecordState> = _state.asStateFlow()

    fun createRecord(appointmentId: Int, diagnosis: String, treatment: String, notes: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, created = false)
            when (val result = vetRepo.createMedicalRecord(appointmentId, diagnosis, treatment, notes)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, created = true)
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onCreatedHandled() {
        _state.value = _state.value.copy(created = false)
    }
}
