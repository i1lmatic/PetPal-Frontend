package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.*
import com.petpal.app.data.repo.AppointmentRepository
import com.petpal.app.data.repo.PetRepository
import com.petpal.app.data.repo.Result
import com.petpal.app.data.repo.VetRepository
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
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, pets = result.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun createPet(pet: PetCreate) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, petCreated = false)
            when (val result = repository.createPet(pet)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, petCreated = true)
                    loadPets()
                }
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun onPetCreatedHandled() { _state.value = _state.value.copy(petCreated = false) }
}

data class AppointmentsState(
    val isLoading: Boolean = false,
    val appointments: List<Appointment> = emptyList(),
    val error: String? = null,
    val created: Boolean = false,
    val slotsLoading: Boolean = false,
    val slots: VetSlotsResponse? = null,
    val vetName: String = ""
)

class AppointmentsViewModel(
    private val repository: AppointmentRepository,
    private val vetRepository: VetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentsState())
    val state: StateFlow<AppointmentsState> = _state.asStateFlow()

    fun loadAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = repository.getMyAppointments()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, appointments = result.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun createAppointment(appointment: AppointmentCreate) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, created = false)
            when (val result = repository.createAppointment(appointment)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(isLoading = false, created = true)
                    loadAppointments()
                }
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun loadSlots(vetId: Int, date: String, vetName: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(slotsLoading = true, vetName = vetName)
            when (val result = vetRepository.getVetSlots(vetId, date)) {
                is Result.Success -> _state.value = _state.value.copy(slotsLoading = false, slots = result.data)
                is Result.Error -> _state.value = _state.value.copy(slotsLoading = false)
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun onCreatedHandled() { _state.value = _state.value.copy(created = false) }
}

data class VetSearchState(
    val isLoading: Boolean = false,
    val vets: List<Veterinary> = emptyList(),
    val query: String = "",
    val error: String? = null
)

class VetSearchViewModel(private val repository: VetRepository) : ViewModel() {

    private val _state = MutableStateFlow(VetSearchState())
    val state: StateFlow<VetSearchState> = _state.asStateFlow()

    fun searchVets(query: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, query = query)
            when (val result = repository.searchVets(query)) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, vets = result.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
}
