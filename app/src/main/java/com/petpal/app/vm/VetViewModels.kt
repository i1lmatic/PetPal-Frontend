package com.petpal.app.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petpal.app.data.model.*
import com.petpal.app.data.repo.VetRepository
import com.petpal.app.data.repo.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VetDashboardState(
    val isLoading: Boolean = false,
    val noBusiness: Boolean = false,
    val clinicName: String = "",
    val doctorName: String = "",
    val appointmentsTodayCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val totalPatientsCount: Int = 0,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val error: String? = null
)

class VetDashboardViewModel(
    private val vetRepo: VetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VetDashboardState())
    val state: StateFlow<VetDashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val businessResult = vetRepo.getMyBusiness()
            if (businessResult is Result.Success) {
                val biz = businessResult.data
                _state.value = _state.value.copy(
                    clinicName = biz.name,
                    doctorName = biz.owner_name ?: ""
                )
            } else {
                _state.value = _state.value.copy(isLoading = false, noBusiness = true)
                return@launch
            }

            val appointmentsResult = vetRepo.getAppointments()
            if (appointmentsResult is Result.Success) {
                val appts = appointmentsResult.data
                val today = java.time.LocalDate.now().toString()
                val todayCount = appts.count { it.date_time.startsWith(today) }
                val pendingCount = appts.count { it.status == "pending" }
                val completedCount = appts.count { it.status == "completed" }
                _state.value = _state.value.copy(
                    upcomingAppointments = appts.filter { it.status in listOf("pending", "confirmed") }.sortedBy { it.date_time },
                    appointmentsTodayCount = todayCount,
                    pendingCount = pendingCount,
                    completedCount = completedCount
                )
            } else if (appointmentsResult is Result.Error) {
                _state.value = _state.value.copy(isLoading = false, error = appointmentsResult.message)
                return@launch
            }

            val patientsResult = vetRepo.getPatients()
            if (patientsResult is Result.Success) {
                _state.value = _state.value.copy(totalPatientsCount = patientsResult.data.size)
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class VetAppointmentsState(
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
    val appointments: List<Appointment> = emptyList(),
    val error: String? = null
)

class VetAppointmentsViewModel(
    private val vetRepo: VetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VetAppointmentsState())
    val state: StateFlow<VetAppointmentsState> = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = vetRepo.getAppointments()) {
                is Result.Success -> _state.value = _state.value.copy(isLoading = false, appointments = r.data)
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _state.value = _state.value.copy(selectedTab = tabIndex)
    }

    fun acceptAppointment(appointmentId: Int) {
        viewModelScope.launch {
            when (val r = vetRepo.acceptAppointment(appointmentId)) {
                is Result.Success -> loadAppointments()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun rejectAppointment(appointmentId: Int) {
        viewModelScope.launch {
            when (val r = vetRepo.rejectAppointment(appointmentId)) {
                is Result.Success -> loadAppointments()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun completeAppointment(appointmentId: Int) {
        viewModelScope.launch {
            when (val r = vetRepo.completeAppointment(appointmentId)) {
                is Result.Success -> loadAppointments()
                is Result.Error -> _state.value = _state.value.copy(error = r.message)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class VetPatientItem(
    val pet: Pet,
    val lastVisit: String,
    val lastTreatment: String
)

data class VetPatientsState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val patients: List<VetPatientItem> = emptyList(),
    val allPatients: List<VetPatientItem> = emptyList(),
    val error: String? = null
)

class VetPatientsViewModel(
    private val vetRepo: VetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VetPatientsState())
    val state: StateFlow<VetPatientsState> = _state.asStateFlow()

    init {
        loadPatients()
    }

    fun loadPatients(query: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, searchQuery = query)
            when (val r = vetRepo.getPatients()) {
                is Result.Success -> {
                    val items = r.data.map { pet ->
                        VetPatientItem(pet = pet, lastVisit = "-", lastTreatment = "-")
                    }
                    val filtered = if (query.isBlank()) items else items.filter { item ->
                        item.pet.name.contains(query, ignoreCase = true) ||
                        item.pet.breed.contains(query, ignoreCase = true) ||
                        item.pet.species.contains(query, ignoreCase = true) ||
                        (item.pet.owner_name?.contains(query, ignoreCase = true) == true)
                    }
                    _state.value = _state.value.copy(isLoading = false, patients = filtered, allPatients = items)
                }
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class VetBusinessState(
    val isLoading: Boolean = false,
    val hasBusiness: Boolean = false,
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val specialties: String = "",
    val workingHours: String = "",
    val description: String = "",
    val saved: Boolean = false,
    val error: String? = null
)

class VetBusinessViewModel(
    private val vetRepo: VetRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VetBusinessState())
    val state: StateFlow<VetBusinessState> = _state.asStateFlow()

    init {
        loadBusiness()
    }

    fun loadBusiness() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = vetRepo.getMyBusiness()) {
                is Result.Success -> {
                    val biz = r.data
                    _state.value = _state.value.copy(
                        isLoading = false, hasBusiness = true,
                        name = biz.name, address = biz.address, phone = biz.phone,
                        specialties = biz.specialties, workingHours = biz.working_hours ?: "",
                        description = biz.description ?: ""
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, hasBusiness = false)
                }
            }
        }
    }

    fun saveBusiness(
        name: String, address: String, phone: String,
        specialties: String, workingHours: String, description: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, saved = false)
            val businessData = VeterinaryCreate(
                name = name, address = address, phone = phone,
                specialties = specialties, working_hours = workingHours, description = description
            )
            val result = if (_state.value.hasBusiness) {
                vetRepo.updateBusiness(VeterinaryUpdate(
                    name = name, address = address, phone = phone,
                    specialties = specialties, working_hours = workingHours, description = description
                ))
            } else {
                vetRepo.createBusiness(businessData)
            }
            when (result) {
                is Result.Success -> _state.value = _state.value.copy(
                    isLoading = false, saved = true, hasBusiness = true,
                    name = name, address = address, phone = phone,
                    specialties = specialties, workingHours = workingHours, description = description
                )
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
            }
        }
    }

    fun onSavedHandled() {
        _state.value = _state.value.copy(saved = false)
    }

    fun deactivateBusiness() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val r = vetRepo.deactivateBusiness()) {
                is Result.Success -> {
                    val biz = r.data
                    _state.value = _state.value.copy(
                        isLoading = false,
                        hasBusiness = true,
                        name = biz.name, address = biz.address, phone = biz.phone,
                        specialties = biz.specialties, workingHours = biz.working_hours ?: "",
                        description = biz.description ?: ""
                    )
                }
                is Result.Error -> _state.value = _state.value.copy(isLoading = false, error = r.message)
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
