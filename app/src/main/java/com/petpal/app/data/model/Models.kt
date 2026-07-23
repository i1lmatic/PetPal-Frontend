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

data class VetRegisterRequest(
    val email: String,
    val full_name: String,
    val phone: String,
    val password: String,
    val business_name: String = "",
    val business_address: String = "",
    val business_phone: String = "",
    val business_specialties: String = "",
    val business_description: String? = null,
    val business_working_hours: String? = null
)

data class PendingVetOut(
    val user_id: Int,
    val email: String,
    val full_name: String,
    val phone: String,
    val business_name: String = "",
    val business_address: String = "",
    val business_phone: String = "",
    val business_specialties: String = "",
    val business_description: String? = null,
    val business_working_hours: String? = null
)

data class Veterinary(
    val id: Int,
    val owner_user_id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val specialties: String,
    val description: String? = null,
    val working_hours: String? = null,
    val photo_url: String? = null,
    val status: String = "active",
    val owner_name: String? = null
)

data class VeterinaryCreate(
    val name: String,
    val address: String,
    val phone: String,
    val specialties: String,
    val description: String? = null,
    val working_hours: String? = null,
    val photo_url: String? = null
)

data class VeterinaryUpdate(
    val name: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val specialties: String? = null,
    val description: String? = null,
    val working_hours: String? = null,
    val photo_url: String? = null
)

data class Pet(
    val id: Int,
    val owner_id: Int,
    val name: String,
    val species: String,
    val breed: String,
    val birth_date: String,
    val weight: Double,
    val photo_url: String? = null,
    val sex: String? = null,
    val color: String? = null,
    val size: String? = null,
    val allergies: String? = null,
    val conditions: String? = null,
    val microchip: String? = null,
    val status: String? = "active",
    val owner_name: String? = null
)

data class PetCreate(
    val name: String,
    val species: String,
    val breed: String,
    val birth_date: String,
    val weight: Double,
    val photo_url: String? = null,
    val sex: String = "",
    val color: String = "",
    val size: String = "",
    val allergies: String? = null,
    val conditions: String? = null,
    val microchip: String? = null
)

data class PetUpdate(
    val name: String? = null,
    val species: String? = null,
    val breed: String? = null,
    val birth_date: String? = null,
    val weight: Double? = null,
    val photo_url: String? = null,
    val sex: String? = null,
    val color: String? = null,
    val size: String? = null,
    val allergies: String? = null,
    val conditions: String? = null,
    val microchip: String? = null
)

data class Appointment(
    val id: Int,
    val pet_id: Int,
    val owner_id: Int,
    val date_time: String,
    val reason: String,
    val status: String,
    val vet_id: Int? = null,
    val notes: String? = null,
    val owner_name: String? = null,
    val pet_name: String? = null,
    val has_record: Boolean = false
)

data class AppointmentCreate(
    val pet_id: Int,
    val vet_id: Int,
    val date_time: String,
    val reason: String,
    val notes: String? = null
)

data class MedicalRecord(
    val id: Int,
    val pet_id: Int,
    val date: String,
    val diagnosis: String,
    val treatment: String,
    val notes: String,
    val appointment_id: Int? = null,
    val vet_id: Int? = null
)

data class MedicalRecordCreate(
    val pet_id: Int,
    val diagnosis: String,
    val treatment: String,
    val notes: String,
    val appointment_id: Int? = null,
    val vet_id: Int? = null
)

data class VetMedicalRecordCreate(
    val appointment_id: Int,
    val diagnosis: String,
    val treatment: String,
    val notes: String
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
    val pending_users: Int = 0,
    val total_vets_active: Int = 0,
    val confirmed_appointments: Int = 0,
    val completed_appointments: Int = 0,
    val cancelled_appointments: Int = 0
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
