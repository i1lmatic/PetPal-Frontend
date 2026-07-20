package com.petpal.app

import android.app.Application
import com.petpal.app.data.local.SessionManager
import com.petpal.app.data.remote.RetrofitInstance

class PetPalApp : Application() {

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
    }

    val apiService by lazy {
        RetrofitInstance.create(sessionManager)
    }
}
