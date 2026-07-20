package com.petpal.app

import android.app.Application
import android.util.Log
import com.petpal.app.data.local.SessionManager
import com.petpal.app.data.remote.RetrofitInstance
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

class PetPalApp : Application() {

    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()

        val original = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val crashLog = buildString {
                    appendLine("==== CRASH PetPal ====")
                    appendLine("Thread: ${thread.name}")
                    appendLine(sw.toString())
                }
                File(cacheDir, "crash_petpal.log").writeText(crashLog)
                Log.e("PetPalCrash", crashLog)
            } catch (_: Throwable) {}
            original?.uncaughtException(thread, throwable)
        }

        sessionManager = SessionManager(this)
    }

    val apiService by lazy {
        RetrofitInstance.create(sessionManager)
    }
}
