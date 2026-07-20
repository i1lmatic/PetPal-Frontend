package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.Appointment
import com.petpal.app.data.model.Pet
import com.petpal.app.data.repo.AppointmentRepository
import com.petpal.app.data.repo.PetRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PetsState(
    val isLoading: Boolean = false,
    val pets: List<Pet> = emptyList(),
    val error: String? = null,
    val petCreated: Boolean = false
)

class PetsViewModel(private val repository: PetRepository) : ViewModel() {

    private val _state = MutableStateFlow(PetsState())
    val state: StateFlow<PetsState> = _state.asStateFlow()

    fun loadPets() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getMyPets()) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, pets = result.data
                )
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun createPet(name: String, species: String, breed: String, birthDate: String, weight: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, petCreated = false)
            when (val result = repository.createPet(name, species, breed, birthDate, weight)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, petCreated = true)
                    loadPets()
                }
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onPetCreatedHandled() {
        _state.value = _state.value.copy(petCreated = false)
    }
}

data class AppointmentsState(
    val isLoading: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val error: String? = null,
    val created: Boolean = false
)

class AppointmentsViewModel(private val repository: AppointmentRepository) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentsState())
    val state: StateFlow<AppointmentsState> = _state.asStateFlow()

    fun loadAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getMyAppointments()) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, appointments = result.data
                )
                is Result.Error -> _state.value = _state.value.copy(
                    isLoading = false, error = result.message
                )
            }
        }
    }

    fun createAppointment(petId: Int, dateTime: String, reason: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, created = false)
            when (val result = repository.createAppointment(petId, dateTime, reason)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, created = true)
                    loadAppointments()
                }
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
