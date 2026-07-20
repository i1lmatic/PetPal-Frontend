package com.petpal.app.data.repo

import com.petpal.app.data.model.Pet
import com.petpal.app.data.model.PetCreate
import com.petpal.app.data.model.MedicalRecord
import com.petpal.app.data.remote.PetPalApiService

class PetRepository(private val api: PetPalApiService) {

    suspend fun getMyPets(): Result<List<Pet>> = runCatching {
        Result.Success(api.getMyPets())
    }.getOrElse { e -> mapError(e) }

    suspend fun createPet(
        name: String,
        species: String,
        breed: String,
        birthDate: String,
        weight: Double
    ): Result<Pet> = runCatching {
        Result.Success(api.createPet(PetCreate(name, species, breed, birthDate, weight)))
    }.getOrElse { e -> mapError(e) }

    suspend fun getPetHistory(petId: Int): Result<List<MedicalRecord>> = runCatching {
        Result.Success(api.getPetHistory(petId))
    }.getOrElse { e -> mapError(e) }
}

class AppointmentRepository(private val api: PetPalApiService) {

    suspend fun getMyAppointments(): Result<List<com.petpal.app.data.model.Appointment>> = runCatching {
        Result.Success(api.getMyAppointments())
    }.getOrElse { e -> mapError(e) }

    suspend fun createAppointment(
        petId: Int,
        dateTime: String,
        reason: String
    ): Result<com.petpal.app.data.model.Appointment> = runCatching {
        Result.Success(api.createAppointment(com.petpal.app.data.model.AppointmentCreate(petId, dateTime, reason)))
    }.getOrElse { e -> mapError(e) }
}

class AdminRepository(private val api: PetPalApiService) {

    suspend fun getPendingUsers(): Result<List<com.petpal.app.data.model.User>> = runCatching {
        Result.Success(api.getPendingUsers())
    }.getOrElse { e -> mapError(e) }

    suspend fun approveUser(userId: Int): Result<com.petpal.app.data.model.User> = runCatching {
        Result.Success(api.approveUser(userId))
    }.getOrElse { e -> mapError(e) }

    suspend fun getAllAppointments(): Result<List<com.petpal.app.data.model.Appointment>> = runCatching {
        Result.Success(api.getAllAppointments())
    }.getOrElse { e -> mapError(e) }

    suspend fun createMedicalRecord(
        petId: Int,
        diagnosis: String,
        treatment: String,
        notes: String
    ): Result<MedicalRecord> = runCatching {
        Result.Success(api.createMedicalRecord(
            com.petpal.app.data.model.MedicalRecordCreate(petId, diagnosis, treatment, notes)
        ))
    }.getOrElse { e -> mapError(e) }

    suspend fun updateAppointmentStatus(appointmentId: Int, status: String): Result<com.petpal.app.data.model.Appointment> = runCatching {
        Result.Success(api.updateAppointmentStatus(
            appointmentId,
            com.petpal.app.data.model.AppointmentStatusUpdate(status)
        ))
    }.getOrElse { e -> mapError(e) }
}

private fun mapError(e: Throwable): Result.Error {
    val msg = when {
        e is retrofit2.HttpException -> {
            when (e.code()) {
                401 -> "Sesi\\u00f3n expirada"
                403 -> "No tienes permisos"
                404 -> "No encontrado"
                else -> "Error del servidor (${e.code()})"
            }
        }
        else -> e.message ?: "Error de conexi\\u00f3n"
    }
    return Result.Error(msg)
}
