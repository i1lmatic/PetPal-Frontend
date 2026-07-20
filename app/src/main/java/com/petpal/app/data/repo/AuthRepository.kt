package com.petpal.app.data.repo

import com.petpal.app.data.model.TokenResponse
import com.petpal.app.data.model.User
import com.petpal.app.data.model.UserCreate
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

    suspend fun login(email: String, password: String): Result<TokenResponse> = runCatching {
        val res = api.login(email, password)
        sessionManager.saveTokenOnly(res.access_token)
        Result.Success(res)
    }.getOrElse { e -> mapError(e) }

    suspend fun getProfile(): Result<User> = runCatching {
        val user = api.getMyProfile()
        sessionManager.saveUserInfo(user)
        Result.Success(user)
    }.getOrElse { e -> mapError(e) }

    suspend fun logout() {
        sessionManager.clear()
    }

    private fun mapError(e: Throwable): Result.Error {
        val msg = when {
            e is retrofit2.HttpException -> {
                when (e.code()) {
                    401 -> "Email o contrase\\u00f1a incorrectos"
                    403 -> "Cuenta pendiente de aprobaci\\u00f3n"
                    400 -> "Email ya registrado"
                    else -> "Error del servidor (${e.code()})"
                }
            }
            else -> e.message ?: "Error de conexi\\u00f3n"
        }
        val code = (e as? retrofit2.HttpException)?.code() ?: 0
        return Result.Error(msg, code)
    }
}
