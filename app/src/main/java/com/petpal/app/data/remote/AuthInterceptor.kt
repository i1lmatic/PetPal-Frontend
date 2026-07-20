package com.petpal.app.data.remote

import com.petpal.app.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath()
        val isAuthRoute = path.contains("/auth/login") || path.contains("/auth/register")

        val request = if (isAuthRoute) {
            chain.request()
        } else {
            val token = runBlocking { sessionManager.getToken() }
            if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }
        }

        return chain.proceed(request)
    }
}
