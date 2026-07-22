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

data class VetDashboardState(
    val isLoading: Boolean = false,
    val clinicName: String = "VetCare Clínica",
    val doctorName: String = "Dr. Carlos",
    val appointmentsTodayCount: Int = 3,
    val pendingCount: Int = 5,
    val completedCount: Int = 12,
    val totalPatientsCount: Int = 28,
    val upcomingAppointments: List<Appointment> = emptyList(),
    val error: String? = null
)

class VetDashboardViewModel(
    private val appointmentRepo: AppointmentRepository? = null,
    private val vetRepo: VetRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(VetDashboardState())
    val state: StateFlow<VetDashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            
            // Datos de demostración estructurados conforme al wireframe del proyecto
            val mockUpcoming = listOf(
                Appointment(
                    id = 1, pet_id = 101, owner_id = 1,
                    date_time = "Hoy 10:00 AM", reason = "Vacunación anual",
                    status = "pending", pet_name = "Max", owner_name = "Patrick"
                ),
                Appointment(
                    id = 2, pet_id = 102, owner_id = 2,
                    date_time = "Hoy 11:30 AM", reason = "Revisión general",
                    status = "pending", pet_name = "Luna", owner_name = "María"
                ),
                Appointment(
                    id = 3, pet_id = 103, owner_id = 3,
                    date_time = "Hoy 2:00 PM", reason = "Revisión dental",
                    status = "confirmed", pet_name = "Rocky", owner_name = "William"
                ),
                Appointment(
                    id = 4, pet_id = 104, owner_id = 4,
                    date_time = "Mañana 9:00 AM", reason = "Control de peso",
                    status = "pending", pet_name = "Toby", owner_name = "Carlos"
                ),
                Appointment(
                    id = 5, pet_id = 105, owner_id = 5,
                    date_time = "Mañana 11:00 AM", reason = "Desparasitación",
                    status = "confirmed", pet_name = "Mimi", owner_name = "Ana"
                )
            )

            _state.value = _state.value.copy(
                isLoading = false,
                upcomingAppointments = mockUpcoming
            )
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class VetAppointmentsState(
    val isLoading: Boolean = false,
    val selectedTab: Int = 0, // 0 = Pendientes, 1 = Confirmadas, 2 = Completadas
    val appointments: List<Appointment> = emptyList(),
    val error: String? = null
)

class VetAppointmentsViewModel(
    private val appointmentRepo: AppointmentRepository? = null,
    private val adminRepo: com.petpal.app.data.repo.AdminRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(VetAppointmentsState())
    val state: StateFlow<VetAppointmentsState> = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun loadAppointments() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val mockList = listOf(
                Appointment(
                    id = 1, pet_id = 101, owner_id = 1,
                    date_time = "25/07 10:00 AM", reason = "Vacunación anual",
                    status = "pending", pet_name = "Max", owner_name = "Patrick"
                ),
                Appointment(
                    id = 2, pet_id = 102, owner_id = 2,
                    date_time = "25/07 2:00 PM", reason = "Revisión dental",
                    status = "pending", pet_name = "Rocky", owner_name = "William"
                ),
                Appointment(
                    id = 3, pet_id = 103, owner_id = 3,
                    date_time = "22/07 3:00 PM", reason = "Chequeo de piel",
                    status = "confirmed", pet_name = "Luna", owner_name = "María"
                ),
                Appointment(
                    id = 4, pet_id = 104, owner_id = 4,
                    date_time = "15/07 9:00 AM", reason = "Control de vacunas",
                    status = "completed", pet_name = "Toby", owner_name = "Carlos"
                )
            )
            _state.value = _state.value.copy(
                isLoading = false,
                appointments = mockList
            )
        }
    }

    fun selectTab(tabIndex: Int) {
        _state.value = _state.value.copy(selectedTab = tabIndex)
    }

    fun acceptAppointment(appointmentId: Int) {
        updateStatus(appointmentId, "confirmed")
    }

    fun rejectAppointment(appointmentId: Int) {
        updateStatus(appointmentId, "cancelled")
    }

    fun completeAppointment(appointmentId: Int) {
        updateStatus(appointmentId, "completed")
    }

    private fun updateStatus(appointmentId: Int, newStatus: String) {
        viewModelScope.launch {
            val updated = _state.value.appointments.map { appt ->
                if (appt.id == appointmentId) appt.copy(status = newStatus) else appt
            }
            _state.value = _state.value.copy(appointments = updated)
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
    val error: String? = null
)

class VetPatientsViewModel(
    private val petRepo: PetRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(VetPatientsState())
    val state: StateFlow<VetPatientsState> = _state.asStateFlow()

    init {
        loadPatients()
    }

    fun loadPatients(query: String = "") {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, searchQuery = query)
            val mockList = listOf(
                VetPatientItem(
                    pet = Pet(
                        id = 101, owner_id = 1, name = "Max", species = "Perro", breed = "Labrador",
                        birth_date = "2022-03-15", weight = 25.0, sex = "Macho", owner_name = "Patrick"
                    ),
                    lastVisit = "15/07/2026",
                    lastTreatment = "Gripe"
                ),
                VetPatientItem(
                    pet = Pet(
                        id = 102, owner_id = 2, name = "Luna", species = "Gato", breed = "Persa",
                        birth_date = "2023-01-10", weight = 4.2, sex = "Hembra", owner_name = "María"
                    ),
                    lastVisit = "10/07/2026",
                    lastTreatment = "Vacunación"
                ),
                VetPatientItem(
                    pet = Pet(
                        id = 103, owner_id = 3, name = "Rocky", species = "Perro", breed = "Bulldog",
                        birth_date = "2021-08-20", weight = 18.5, sex = "Macho", owner_name = "William"
                    ),
                    lastVisit = "05/07/2026",
                    lastTreatment = "Revisión dental"
                )
            )
            val filtered = if (query.isBlank()) {
                mockList
            } else {
                mockList.filter { item ->
                    item.pet.name.contains(query, ignoreCase = true) ||
                    item.pet.breed.contains(query, ignoreCase = true) ||
                    item.pet.species.contains(query, ignoreCase = true) ||
                    (item.pet.owner_name?.contains(query, ignoreCase = true) == true)
                }
            }
            _state.value = _state.value.copy(isLoading = false, patients = filtered)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}

data class VetBusinessState(
    val isLoading: Boolean = false,
    val name: String = "VetCare Clínica",
    val address: String = "San José, Costa Rica",
    val phone: String = "2222-3333",
    val specialties: String = "Cirugía, Dermatología, Odontología",
    val workingHours: String = "Lun-Vie 8:00 - 17:00, Sáb 9:00 - 13:00",
    val description: String = "Clínica veterinaria especializada con atención personalizada y equipamiento de última generación.",
    val saved: Boolean = false,
    val deactivated: Boolean = false,
    val error: String? = null
)

class VetBusinessViewModel(
    private val vetRepo: VetRepository? = null
) : ViewModel() {

    private val _state = MutableStateFlow(VetBusinessState())
    val state: StateFlow<VetBusinessState> = _state.asStateFlow()

    fun saveBusiness(
        name: String,
        address: String,
        phone: String,
        specialties: String,
        workingHours: String,
        description: String
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, saved = false)
            _state.value = _state.value.copy(
                isLoading = false,
                name = name,
                address = address,
                phone = phone,
                specialties = specialties,
                workingHours = workingHours,
                description = description,
                saved = true
            )
        }
    }

    fun deactivateBusiness() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, deactivated = false)
            _state.value = _state.value.copy(isLoading = false, deactivated = true)
        }
    }

    fun onSavedHandled() {
        _state.value = _state.value.copy(saved = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
