package com.petpal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.petpal.app.data.repo.*
import com.petpal.app.ui.nav.PetPalNavGraph
import com.petpal.app.ui.theme.PetPalTheme
import com.petpal.app.vm.*

class MainActivity : ComponentActivity() {

    private val app: PetPalApp get() = application as PetPalApp

    private val authViewModel: AuthViewModel by viewModels {
        val api = app.apiService
        val repo = AuthRepository(api, app.sessionManager)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo, app.sessionManager) as T
            }
        }
    }

    private val petsViewModel: PetsViewModel by viewModels {
        val repo = PetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PetsViewModel(repo) as T
            }
        }
    }

    private val appointmentsViewModel: AppointmentsViewModel by viewModels {
        val repo = AppointmentRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AppointmentsViewModel(repo) as T
            }
        }
    }

    private val petDetailViewModel: PetDetailViewModel by viewModels {
        val repo = PetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PetDetailViewModel(repo) as T
            }
        }
    }

    private val adminViewModel: AdminViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AdminViewModel(repo) as T
            }
        }
    }

    private val medicalRecordViewModel: AddMedicalRecordViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AddMedicalRecordViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PetPalNavGraph(
                        authViewModel = authViewModel,
                        petsViewModel = petsViewModel,
                        appointmentsViewModel = appointmentsViewModel,
                        petDetailViewModel = petDetailViewModel,
                        adminViewModel = adminViewModel,
                        medicalRecordViewModel = medicalRecordViewModel
                    )
                }
            }
        }
    }
}
