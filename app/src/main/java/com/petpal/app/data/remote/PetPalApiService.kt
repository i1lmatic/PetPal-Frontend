package com.petpal.app.data.remote

import com.petpal.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface PetPalApiService {

    @POST("auth/register")
    suspend fun register(@Body user: UserCreate): User

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): TokenResponse

    @GET("users/me")
    suspend fun getMyProfile(): User

    @PATCH("users/me")
    suspend fun updateMyProfile(@Body update: UserUpdateProfile): User

    @GET("pets/")
    suspend fun getMyPets(): List<Pet>

    @POST("pets/")
    suspend fun createPet(@Body pet: PetCreate): Pet

    @GET("pets/{id}/history")
    suspend fun getPetHistory(@Path("id") petId: Int): List<MedicalRecord>

    @GET("appointments/me")
    suspend fun getMyAppointments(): List<Appointment>

    @POST("appointments/")
    suspend fun createAppointment(@Body appointment: AppointmentCreate): Appointment

    @GET("admin/users/pending")
    suspend fun getPendingUsers(): List<User>

    @PATCH("admin/users/{id}/approve")
    suspend fun approveUser(@Path("id") userId: Int): User

    @DELETE("admin/users/{id}/reject")
    suspend fun rejectUser(@Path("id") userId: Int): User

    @GET("admin/users/active")
    suspend fun getActiveUsers(): List<User>

    @GET("admin/users/{id}")
    suspend fun getUserDetail(@Path("id") userId: Int): UserDetail

    @GET("admin/pets")
    suspend fun getAllPets(@Query("search") search: String = ""): List<Pet>

    @GET("admin/dashboard/stats")
    suspend fun getDashboardStats(): DashboardStats

    @GET("admin/appointments")
    suspend fun getAllAppointments(): List<Appointment>

    @POST("admin/medical-records")
    suspend fun createMedicalRecord(@Body record: MedicalRecordCreate): MedicalRecord

    @PATCH("admin/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") appointmentId: Int,
        @Body status: AppointmentStatusUpdate
    ): Appointment
}
