package com.petpal.app.data.remote

import com.petpal.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface PetPalApiService {

    @POST("auth/register")
    suspend fun register(@Body user: UserCreate): User

    @POST("auth/register-vet")
    suspend fun registerVet(@Body user: UserCreate): User

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

    @PATCH("pets/{id}")
    suspend fun updatePet(@Path("id") petId: Int, @Body pet: PetUpdate): Pet

    @GET("pets/{id}/history")
    suspend fun getPetHistory(@Path("id") petId: Int): List<MedicalRecord>

    @GET("appointments/me")
    suspend fun getMyAppointments(): List<Appointment>

    @POST("appointments/")
    suspend fun createAppointment(@Body appointment: AppointmentCreate): Appointment

    @GET("vets/search")
    suspend fun searchVets(@Query("q") query: String = ""): List<Veterinary>

    @GET("vets/{id}")
    suspend fun getVetDetail(@Path("id") vetId: Int): Veterinary

    @GET("vet/business")
    suspend fun getMyVetBusiness(): Veterinary

    @POST("vet/business")
    suspend fun createVetBusiness(@Body business: VeterinaryCreate): Veterinary

    @PATCH("vet/business")
    suspend fun updateVetBusiness(@Body update: VeterinaryUpdate): Veterinary

    @GET("vet/appointments")
    suspend fun getVetAppointments(): List<Appointment>

    @PATCH("vet/appointments/{id}/accept")
    suspend fun acceptVetAppointment(@Path("id") appointmentId: Int): Appointment

    @PATCH("vet/appointments/{id}/reject")
    suspend fun rejectVetAppointment(@Path("id") appointmentId: Int): Appointment

    @PATCH("vet/appointments/{id}/complete")
    suspend fun completeVetAppointment(@Path("id") appointmentId: Int): Appointment

    @GET("vet/patients")
    suspend fun getVetPatients(): List<Pet>

    @POST("vet/medical-records")
    suspend fun createVetMedicalRecord(@Body record: VetMedicalRecordCreate): MedicalRecord

    @GET("admin/users/pending")
    suspend fun getPendingUsers(): List<User>

    @PATCH("admin/users/{id}/approve")
    suspend fun approveUser(@Path("id") userId: Int): User

    @DELETE("admin/users/{id}/reject")
    suspend fun rejectUser(@Path("id") userId: Int): User

    @PATCH("admin/users/{id}/deactivate")
    suspend fun deactivateUser(@Path("id") userId: Int): User

    @PATCH("admin/users/{id}/reactivate")
    suspend fun reactivateUser(@Path("id") userId: Int): User

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

    @GET("admin/vets")
    suspend fun getAdminVets(): List<Veterinary>

    @GET("admin/vets/pending")
    suspend fun getPendingVets(): List<User>

    @PATCH("admin/vets/{id}/deactivate")
    suspend fun deactivateVet(@Path("id") vetId: Int): Veterinary

    @PATCH("admin/vets/{id}/reactivate")
    suspend fun reactivateVet(@Path("id") vetId: Int): Veterinary
}
