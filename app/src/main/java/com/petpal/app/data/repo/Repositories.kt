package com.petpal.app.data.repo

import com.petpal.app.data.model.*
import com.petpal.app.data.remote.PetPalApiService

class PetRepository(private val api: PetPalApiService) {

    suspend fun getMyPets(): Result<List<Pet>> = runCatching {
        Result.Success(api.getMyPets())
    }.getOrElse { e -> mapError(e) }

    suspend fun createPet(pet: PetCreate): Result<Pet> = runCatching {
        Result.Success(api.createPet(pet))
    }.getOrElse { e -> mapError(e) }

    suspend fun updatePet(petId: Int, pet: PetUpdate): Result<Pet> = runCatching {
        Result.Success(api.updatePet(petId, pet))
    }.getOrElse { e -> mapError(e) }

    suspend fun getPetHistory(petId: Int): Result<List<MedicalRecord>> = runCatching {
        Result.Success(api.getPetHistory(petId))
    }.getOrElse { e -> mapError(e) }
}

class AppointmentRepository(private val api: PetPalApiService) {

    suspend fun getMyAppointments(): Result<List<Appointment>> = runCatching {
        Result.Success(api.getMyAppointments())
    }.getOrElse { e -> mapError(e) }

    suspend fun createAppointment(appointment: AppointmentCreate): Result<Appointment> = runCatching {
        Result.Success(api.createAppointment(appointment))
    }.getOrElse { e -> mapError(e) }
}

class VetRepository(private val api: PetPalApiService) {

    suspend fun searchVets(query: String = ""): Result<List<Veterinary>> = runCatching {
        Result.Success(api.searchVets(query))
    }.getOrElse { e -> mapError(e) }

    suspend fun getVetDetail(vetId: Int): Result<Veterinary> = runCatching {
        Result.Success(api.getVetDetail(vetId))
    }.getOrElse { e -> mapError(e) }
}

class AdminRepository(private val api: PetPalApiService) {

    suspend fun getPendingUsers(): Result<List<User>> = runCatching {
        Result.Success(api.getPendingUsers())
    }.getOrElse { e -> mapError(e) }

    suspend fun approveUser(userId: Int): Result<User> = runCatching {
        Result.Success(api.approveUser(userId))
    }.getOrElse { e -> mapError(e) }

    suspend fun rejectUser(userId: Int): Result<User> = runCatching {
        Result.Success(api.rejectUser(userId))
    }.getOrElse { e -> mapError(e) }

    suspend fun getActiveUsers(): Result<List<User>> = runCatching {
        Result.Success(api.getActiveUsers())
    }.getOrElse { e -> mapError(e) }

    suspend fun getUserDetail(userId: Int): Result<UserDetail> = runCatching {
        Result.Success(api.getUserDetail(userId))
    }.getOrElse { e -> mapError(e) }

    suspend fun getAllPets(search: String = ""): Result<List<Pet>> = runCatching {
        Result.Success(api.getAllPets(search))
    }.getOrElse { e -> mapError(e) }

    suspend fun getDashboardStats(): Result<DashboardStats> = runCatching {
        Result.Success(api.getDashboardStats())
    }.getOrElse { e -> mapError(e) }

    suspend fun getAllAppointments(): Result<List<Appointment>> = runCatching {
        Result.Success(api.getAllAppointments())
    }.getOrElse { e -> mapError(e) }

    suspend fun createMedicalRecord(
        petId: Int, diagnosis: String, treatment: String, notes: String, appointmentId: Int? = null
    ): Result<MedicalRecord> = runCatching {
        Result.Success(api.createMedicalRecord(MedicalRecordCreate(petId, diagnosis, treatment, notes, appointmentId)))
    }.getOrElse { e -> mapError(e) }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String): Result<Appointment> = runCatching {
        Result.Success(api.updateAppointmentStatus(appointmentId, AppointmentStatusUpdate(status)))
    }.getOrElse { e -> mapError(e) }

    suspend fun getAdminVets(): Result<List<Veterinary>> = runCatching {
        Result.Success(api.getAdminVets())
    }.getOrElse { e -> mapError(e) }

    suspend fun getPendingVets(): Result<List<User>> = runCatching {
        Result.Success(api.getPendingVets())
    }.getOrElse { e -> mapError(e) }

    suspend fun deactivateVet(vetId: Int): Result<Veterinary> = runCatching {
        Result.Success(api.deactivateVet(vetId))
    }.getOrElse { e -> mapError(e) }

    suspend fun reactivateVet(vetId: Int): Result<Veterinary> = runCatching {
        Result.Success(api.reactivateVet(vetId))
    }.getOrElse { e -> mapError(e) }

    suspend fun deactivateUser(userId: Int): Result<User> = runCatching {
        Result.Success(api.deactivateUser(userId))
    }.getOrElse { e -> mapError(e) }

    suspend fun reactivateUser(userId: Int): Result<User> = runCatching {
        Result.Success(api.reactivateUser(userId))
    }.getOrElse { e -> mapError(e) }
}

private fun mapError(e: Throwable): Result.Error {
    val msg = when {
        e is retrofit2.HttpException -> {
            when (e.code()) {
                401 -> "Sesi\u00f3n expirada"
                403 -> "No tienes permisos"
                404 -> "No encontrado"
                else -> "Error del servidor (${e.code()})"
            }
        }
        else -> e.message ?: "Error de conexi\u00f3n"
    }
    return Result.Error(msg)
}
