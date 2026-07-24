package com.petpal.app.data.repo

import android.util.Log
import com.petpal.app.data.model.TokenResponse
import com.petpal.app.data.model.User
import com.petpal.app.data.model.UserCreate
import com.petpal.app.data.model.VetRegisterRequest
import com.petpal.app.data.remote.PetPalApiService
import com.petpal.app.data.local.SessionManager

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = 0) : Result<Nothing>()
}

class AuthRepository(
    private val api: PetPalApiService,
    private val sessionManager: SessionManager
) {
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phone: String
    ): Result<User> = runCatching {
        val user = api.register(UserCreate(email, full_name = fullName, phone, password))
        Result.Success(user)
    }.getOrElse { e -> mapError(e) }

    suspend fun registerVet(
        email: String,
        password: String,
        fullName: String,
        phone: String,
        businessName: String = "",
        businessAddress: String = "",
        businessPhone: String = "",
        businessSpecialties: String = "",
        businessDescription: String? = null,
        businessWorkingHours: String? = null
    ): Result<User> = runCatching {
        val user = api.registerVet(VetRegisterRequest(
            email, full_name = fullName, phone, password,
            business_name = businessName,
            business_address = businessAddress,
            business_phone = businessPhone,
            business_specialties = businessSpecialties,
            business_description = businessDescription,
            business_working_hours = businessWorkingHours
        ))
        Result.Success(user)
    }.getOrElse { e -> mapError(e) }

    suspend fun login(email: String, password: String): Result<TokenResponse> {
        Log.d("PetPalFlow", "REPO: api.login() iniciando")
        return runCatching {
            val res = api.login(email, password)
            Log.d("PetPalFlow", "REPO: api.login() OK, guardando token")
            sessionManager.saveTokenOnly(res.access_token)
            Log.d("PetPalFlow", "REPO: token guardado")
            Result.Success(res)
        }.getOrElse { e ->
            Log.e("PetPalFlow", "REPO: api.login() FAIL", e)
            mapError(e, isLogin = true)
        }
    }

    suspend fun getProfile(): Result<User> {
        Log.d("PetPalFlow", "REPO: api.getMyProfile() iniciando")
        return runCatching {
            val user = api.getMyProfile()
            Log.d("PetPalFlow", "REPO: getMyProfile() OK role=${user.role}")
            sessionManager.saveUserInfo(user)
            Log.d("PetPalFlow", "REPO: user info guardada")
            Result.Success(user)
        }.getOrElse { e ->
            Log.e("PetPalFlow", "REPO: getMyProfile() FAIL", e)
            mapError(e)
        }
    }

    suspend fun updateProfile(fullName: String?, phone: String?): Result<User> = runCatching {
        Result.Success(api.updateMyProfile(com.petpal.app.data.model.UserUpdateProfile(fullName, phone)))
    }.getOrElse { e -> mapError(e) }

    suspend fun logout() {
        sessionManager.clear()
    }

    private fun mapError(e: Throwable, isLogin: Boolean = false): Result.Error {
        val msg = when {
            e is retrofit2.HttpException -> {
                when (e.code()) {
                    401 -> if (isLogin) "Email o contrase\u00f1a incorrectos" else "Sesi\u00f3n expirada"
                    403 -> "Cuenta pendiente de aprobaci\u00f3n"
                    400 -> "Email ya registrado"
                    else -> "Error del servidor (${e.code()})"
                }
            }
            else -> e.message ?: "Error de conexi\u00f3n"
        }
        val code = (e as? retrofit2.HttpException)?.code() ?: 0
        return Result.Error(msg, code)
    }
}
