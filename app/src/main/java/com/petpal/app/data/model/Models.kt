package com.petpal.app.data.model

data class TokenResponse(
    val access_token: String,
    val token_type: String
)

data class User(
    val id: Int,
    val email: String,
    val full_name: String,
    val phone: String,
    val role: String,
    val status: String
)

data class UserCreate(
    val email: String,
    val full_name: String,
    val phone: String,
    val password: String
)

data class Pet(
    val id: Int,
    val owner_id: Int,
    val name: String,
    val species: String,
    val breed: String,
    val birth_date: String,
    val weight: Double,
    val photo_url: String? = null
)

data class PetCreate(
    val name: String,
    val species: String,
    val breed: String,
    val birth_date: String,
    val weight: Double,
    val photo_url: String? = null
)

data class Appointment(
    val id: Int,
    val pet_id: Int,
    val owner_id: Int,
    val date_time: String,
    val reason: String,
    val status: String,
    val owner_name: String? = null,
    val pet_name: String? = null,
    val has_record: Boolean = false
)

data class AppointmentCreate(
    val pet_id: Int,
    val date_time: String,
    val reason: String
)

data class MedicalRecord(
    val id: Int,
    val pet_id: Int,
    val date: String,
    val diagnosis: String,
    val treatment: String,
    val notes: String
)

data class MedicalRecordCreate(
    val pet_id: Int,
    val diagnosis: String,
    val treatment: String,
    val notes: String,
    val appointment_id: Int? = null
)

data class AppointmentStatusUpdate(
    val status: String
)

data class UserUpdateProfile(
    val full_name: String? = null,
    val phone: String? = null
)

data class DashboardStats(
    val total_users: Int = 0,
    val total_pets: Int = 0,
    val total_appointments: Int = 0,
    val appointments_today: Int = 0,
    val pending_appointments: Int = 0,
    val pending_users: Int = 0
)

data class UserDetail(
    val id: Int,
    val email: String,
    val full_name: String,
    val phone: String,
    val role: String,
    val status: String,
    val pets: List<Pet> = emptyList()
)
