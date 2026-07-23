package com.petpal.app

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import java.io.File

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

    private val dashboardViewModel: DashboardViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repo) as T
            }
        }
    }

    private val allPetsViewModel: AllPetsViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AllPetsViewModel(repo) as T
            }
        }
    }

    private val activeUsersViewModel: ActiveUsersViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ActiveUsersViewModel(repo) as T
            }
        }
    }

    private val clientDetailViewModel: ClientDetailViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ClientDetailViewModel(repo) as T
            }
        }
    }

    private val editProfileViewModel: EditProfileViewModel by viewModels {
        val repo = AuthRepository(app.apiService, app.sessionManager)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return EditProfileViewModel(repo) as T
            }
        }
    }

    private val vetSearchViewModel: VetSearchViewModel by viewModels {
        val repo = VetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VetSearchViewModel(repo) as T
            }
        }
    }

    private val manageUsersViewModel: ManageUsersViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ManageUsersViewModel(repo) as T
            }
        }
    }

    private val manageVetsViewModel: ManageVetsViewModel by viewModels {
        val repo = AdminRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return ManageVetsViewModel(repo) as T
            }
        }
    }

    private val vetDashboardViewModel: VetDashboardViewModel by viewModels {
        val vetRepo = VetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VetDashboardViewModel(vetRepo) as T
            }
        }
    }

    private val vetAppointmentsViewModel: VetAppointmentsViewModel by viewModels {
        val repo = VetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VetAppointmentsViewModel(repo) as T
            }
        }
    }

    private val vetPatientsViewModel: VetPatientsViewModel by viewModels {
        val repo = VetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VetPatientsViewModel(repo) as T
            }
        }
    }

    private val vetBusinessViewModel: VetBusinessViewModel by viewModels {
        val repo = VetRepository(app.apiService)
        object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return VetBusinessViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashFile = File(cacheDir, "crash_petpal.log")
        if (crashFile.exists()) {
            val content = crashFile.readText()
            Log.e("PreviousCrash", content)
            Toast.makeText(this, "CRASH: ${content.take(300)}", Toast.LENGTH_LONG).show()
        }

        setContent {
            PetPalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    PetPalNavGraph(
                        authViewModel = authViewModel,
                        petsViewModel = petsViewModel,
                        appointmentsViewModel = appointmentsViewModel,
                        petDetailViewModel = petDetailViewModel,
                        vetSearchViewModel = vetSearchViewModel,
                        adminViewModel = adminViewModel,
                        medicalRecordViewModel = medicalRecordViewModel,
                        dashboardViewModel = dashboardViewModel,
                        allPetsViewModel = allPetsViewModel,
                        activeUsersViewModel = activeUsersViewModel,
                        clientDetailViewModel = clientDetailViewModel,
                        editProfileViewModel = editProfileViewModel,
                        manageUsersViewModel = manageUsersViewModel,
                        manageVetsViewModel = manageVetsViewModel,
                        vetDashboardViewModel = vetDashboardViewModel,
                        vetAppointmentsViewModel = vetAppointmentsViewModel,
                        vetPatientsViewModel = vetPatientsViewModel,
                        vetBusinessViewModel = vetBusinessViewModel
                    )
                }
            }
        }
    }
}
