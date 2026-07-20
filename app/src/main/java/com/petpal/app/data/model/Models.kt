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
    val status: String
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
    val notes: String
)

data class AppointmentStatusUpdate(
    val status: String
)
