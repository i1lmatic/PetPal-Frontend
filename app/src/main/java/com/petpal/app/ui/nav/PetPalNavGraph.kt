package com.petpal.app.ui.nav

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.petpal.app.vm.*
import java.net.URLEncoder

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PENDING = "pending"
    const val PETS_LIST = "pets"
    const val ADD_PET = "add_pet"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val APPOINTMENTS = "appointments"
    const val ADD_APPOINTMENT = "add_appointment"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_APPOINTMENTS = "admin_appointments"
    const val ADMIN_PETS = "admin_pets"
    const val ADMIN_RECORDS = "admin_records"
    const val ADMIN_CLIENT_DETAIL = "admin_client_detail/{userId}"
    const val ADMIN_PET_HISTORY = "admin_pet_history/{petId}"
    const val ADMIN_RECORDS_FROM_APPT = "admin_records?petId={petId}&diagnosis={diagnosis}"

    fun petDetail(petId: Int) = "pet_detail/$petId"
    fun clientDetail(userId: Int) = "admin_client_detail/$userId"
    fun petHistory(petId: Int) = "admin_pet_history/$petId"
}

@Composable
fun ScreenWithBottomBar(
    bottomBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(bottomBar = bottomBar) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            content()
        }
    }
}

@Composable
fun PetPalNavGraph(
    authViewModel: AuthViewModel,
    petsViewModel: PetsViewModel,
    appointmentsViewModel: AppointmentsViewModel,
    petDetailViewModel: PetDetailViewModel,
    adminViewModel: AdminViewModel,
    medicalRecordViewModel: AddMedicalRecordViewModel,
    dashboardViewModel: DashboardViewModel,
    allPetsViewModel: AllPetsViewModel,
    activeUsersViewModel: ActiveUsersViewModel,
    clientDetailViewModel: ClientDetailViewModel,
    editProfileViewModel: EditProfileViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.state.collectAsState()

    Log.d("PetPalFlow", "NAV: recompose isLoggedIn=${authState.isLoggedIn} role=${authState.role}")

    LaunchedEffect(authState.isLoggedIn, authState.role, authState.isPending) {
        if (authState.isCheckingSession) return@LaunchedEffect
        val currentRoute = navController.currentDestination?.route
        Log.d("PetPalFlow", "NAV: isLoggedIn=${authState.isLoggedIn} role=${authState.role} current=$currentRoute")
        when {
            authState.isPending && currentRoute != Routes.PENDING -> {
                Log.d("PetPalFlow", "NAV: -> PENDING")
                navController.navigate(Routes.PENDING) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && authState.role == "admin"
                && currentRoute != Routes.ADMIN_DASHBOARD
                && currentRoute != Routes.ADMIN_USERS
                && currentRoute != Routes.ADMIN_APPOINTMENTS
                && currentRoute != Routes.ADMIN_PETS
                && currentRoute != Routes.ADMIN_RECORDS
                && !currentRoute.toString().startsWith("admin_client_detail")
                && !currentRoute.toString().startsWith("admin_pet_history") -> {
                Log.d("PetPalFlow", "NAV: -> ADMIN")
                navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && authState.status == "active"
                && currentRoute != Routes.PETS_LIST
                && currentRoute != Routes.APPOINTMENTS
                && currentRoute != Routes.PROFILE
                && currentRoute != Routes.ADD_PET
                && currentRoute != Routes.ADD_APPOINTMENT
                && currentRoute != Routes.EDIT_PROFILE
                && !currentRoute.toString().startsWith("pet_detail") -> {
                Log.d("PetPalFlow", "NAV: -> PETS_LIST")
                navController.navigate(Routes.PETS_LIST) { popUpTo(0) { inclusive = true } }
            }
        }
    }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            com.petpal.app.ui.screens.auth.LoginScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onLogin = { email, pass -> authViewModel.login(email, pass) },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.REGISTER) {
            com.petpal.app.ui.screens.auth.RegisterScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onRegister = { email, pass, name, phone -> authViewModel.register(email, pass, name, phone) },
                onNavigateToLogin = { navController.popBackStack() },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.PENDING) {
            com.petpal.app.ui.screens.auth.PendingApprovalScreen(
                onBackToLogin = {
                    authViewModel.clearPending()
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.PETS_LIST) {
            val petsState = petsViewModel.state.collectAsState().value
            val apptState = appointmentsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.PETS_LIST,
                        onNavigate = { route ->
                            when (route) {
                                "pets" -> navController.navigate(Routes.PETS_LIST) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "appointments" -> navController.navigate(Routes.APPOINTMENTS) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "profile" -> navController.navigate(Routes.PROFILE) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.PetsListScreen(
                    pets = petsState.pets,
                    appointments = apptState.appointments,
                    isLoading = petsState.isLoading,
                    error = petsState.error,
                    onLoadPets = { petsViewModel.loadPets() },
                    onAddPet = { navController.navigate(Routes.ADD_PET) },
                    onAddAppointment = { navController.navigate(Routes.ADD_APPOINTMENT) },
                    onPetClick = { pet -> navController.navigate(Routes.petDetail(pet.id)) }
                )
            }
        }

        composable(Routes.ADD_PET) {
            val petsState = petsViewModel.state.collectAsState().value
            LaunchedEffect(petsState.petCreated) {
                if (petsState.petCreated) {
                    petsViewModel.onPetCreatedHandled()
                    navController.popBackStack()
                }
            }
            com.petpal.app.ui.screens.owner.AddPetScreen(
                isLoading = petsState.isLoading,
                error = petsState.error,
                onSave = { name, species, breed, birthDate, weight ->
                    petsViewModel.createPet(name, species, breed, birthDate, weight)
                },
                onBack = { navController.popBackStack() },
                onClearError = { petsViewModel.clearError() }
            )
        }

        composable(Routes.PET_DETAIL, arguments = listOf(navArgument("petId") { type = NavType.IntType })) {
            val petId = it.arguments?.getInt("petId") ?: return@composable
            val detailState = petDetailViewModel.state.collectAsState().value
            val petsState = petsViewModel.state.collectAsState().value
            val pet = petsState.pets.find { p -> p.id == petId }
            if (pet != null) {
                com.petpal.app.ui.screens.owner.PetDetailScreen(
                    pet = pet,
                    records = detailState.records,
                    isLoading = detailState.isLoading,
                    error = detailState.error,
                    onLoadHistory = { petDetailViewModel.loadHistory(it) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.APPOINTMENTS) {
            val apptState = appointmentsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.APPOINTMENTS,
                        onNavigate = { route ->
                            when (route) {
                                "pets" -> navController.navigate(Routes.PETS_LIST) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "appointments" -> navController.navigate(Routes.APPOINTMENTS) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "profile" -> navController.navigate(Routes.PROFILE) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.AppointmentsScreen(
                    appointments = apptState.appointments,
                    isLoading = apptState.isLoading,
                    error = apptState.error,
                    onLoad = { appointmentsViewModel.loadAppointments() },
                    onAdd = { navController.navigate(Routes.ADD_APPOINTMENT) }
                )
            }
        }

        composable(Routes.ADD_APPOINTMENT) {
            val apptState = appointmentsViewModel.state.collectAsState().value
            val petsState = petsViewModel.state.collectAsState().value
            LaunchedEffect(apptState.created) {
                if (apptState.created) {
                    appointmentsViewModel.onCreatedHandled()
                    navController.popBackStack()
                }
            }
            com.petpal.app.ui.screens.owner.AddAppointmentScreen(
                pets = petsState.pets,
                isLoading = apptState.isLoading,
                error = apptState.error,
                onSave = { petId, dateTime, reason ->
                    appointmentsViewModel.createAppointment(petId, dateTime, reason)
                },
                onBack = { navController.popBackStack() },
                onLoadPets = { petsViewModel.loadPets() },
                onClearError = { appointmentsViewModel.clearError() }
            )
        }

        composable(Routes.PROFILE) {
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.PROFILE,
                        onNavigate = { route ->
                            when (route) {
                                "pets" -> navController.navigate(Routes.PETS_LIST) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "appointments" -> navController.navigate(Routes.APPOINTMENTS) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                                "profile" -> navController.navigate(Routes.PROFILE) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.ProfileScreen(
                    user = authState.user,
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                    },
                    onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) }
                )
            }
        }

        composable(Routes.EDIT_PROFILE) {
            val editState = editProfileViewModel.state.collectAsState().value
            LaunchedEffect(editState.saved) {
                if (editState.saved) {
                    editProfileViewModel.onSavedHandled()
                    authViewModel.loadProfile()
                    navController.popBackStack()
                }
            }
            com.petpal.app.ui.screens.owner.EditProfileScreen(
                user = authState.user,
                isLoading = editState.isLoading,
                error = editState.error,
                onSave = { name, phone -> editProfileViewModel.save(name, phone) },
                onBack = { navController.popBackStack() },
                onClearError = { editProfileViewModel.clearError() }
            )
        }

        val doLogout: () -> Unit = {
            authViewModel.logout()
            navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
        }

        composable(Routes.ADMIN_DASHBOARD) {
            val dashState = dashboardViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_DASHBOARD,
                        onNavigate = { route ->
                            when (route) {
                                "admin_dashboard" -> navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_pets" -> navController.navigate(Routes.ADMIN_PETS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                            }
                        },
                        onLogout = doLogout
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.DashboardScreen(
                    stats = dashState.stats,
                    isLoading = dashState.isLoading,
                    error = dashState.error,
                    onLoad = { dashboardViewModel.load() },
                    onNavigate = { r -> navController.navigate(r) }
                )
            }
        }

        composable(Routes.ADMIN_USERS) {
            val adminState = adminViewModel.state.collectAsState().value
            val activeState = activeUsersViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_USERS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_dashboard" -> navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_pets" -> navController.navigate(Routes.ADMIN_PETS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                            }
                        },
                        onLogout = doLogout
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.UsersScreen(
                    pendingUsers = adminState.pendingUsers,
                    activeUsers = activeState.users,
                    isLoading = adminState.isLoading,
                    isLoadingActive = activeState.isLoading,
                    error = adminState.error,
                    onLoadPending = { adminViewModel.loadPendingUsers() },
                    onLoadActive = { activeUsersViewModel.load() },
                    onApprove = { adminViewModel.approveUser(it) },
                    onReject = { adminViewModel.rejectUser(it) },
                    onUserClick = { user -> navController.navigate(Routes.clientDetail(user.id)) }
                )
            }
        }

        composable(Routes.ADMIN_APPOINTMENTS) {
            val adminState = adminViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_APPOINTMENTS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_dashboard" -> navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_pets" -> navController.navigate(Routes.ADMIN_PETS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                            }
                        },
                        onLogout = doLogout
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.AllAppointmentsScreen(
                    appointments = adminState.allAppointments,
                    isLoading = adminState.isLoading,
                    error = adminState.error,
                    onLoad = { adminViewModel.loadAllAppointments() },
                    onUpdateStatus = { id, status -> adminViewModel.updateAppointmentStatus(id, status) },
                    onCreateRecord = { appt ->
                        navController.navigate("admin_records?petId=${appt.pet_id}&diagnosis=${URLEncoder.encode(appt.reason, "UTF-8")}")
                    }
                )
            }
        }

        composable(Routes.ADMIN_PETS) {
            val petsState = allPetsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_PETS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_dashboard" -> navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_pets" -> navController.navigate(Routes.ADMIN_PETS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                            }
                        },
                        onLogout = doLogout
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.AllPetsScreen(
                    pets = petsState.pets,
                    isLoading = petsState.isLoading,
                    error = petsState.error,
                    onLoad = { search -> allPetsViewModel.load(search) },
                    onPetClick = { pet -> navController.navigate(Routes.petHistory(pet.id)) }
                )
            }
        }

        composable(Routes.ADMIN_RECORDS) {
            val recState = medicalRecordViewModel.state.collectAsState().value
            val allPets = allPetsViewModel.state.collectAsState().value.pets
            LaunchedEffect(Unit) { allPetsViewModel.load("") }
            LaunchedEffect(recState.created) {
                if (recState.created) medicalRecordViewModel.onCreatedHandled()
            }
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_RECORDS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_dashboard" -> navController.navigate(Routes.ADMIN_DASHBOARD) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                                "admin_pets" -> navController.navigate(Routes.ADMIN_PETS) { popUpTo(Routes.ADMIN_DASHBOARD) { inclusive = true } }
                            }
                        },
                        onLogout = doLogout
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.AddMedicalRecordScreen(
                    pets = allPets,
                    isLoading = recState.isLoading,
                    error = recState.error,
                    onSave = { petId, diag, treat, notes ->
                        medicalRecordViewModel.createRecord(petId, diag, treat, notes)
                    },
                    onBack = { navController.popBackStack() },
                    onLoadPets = { allPetsViewModel.load("") },
                    onClearError = { medicalRecordViewModel.clearError() }
                )
            }
        }

        composable(Routes.ADMIN_CLIENT_DETAIL, arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
            val userId = it.arguments?.getInt("userId") ?: return@composable
            val detailState = clientDetailViewModel.state.collectAsState().value
            com.petpal.app.ui.screens.admin.ClientDetailScreen(
                user = detailState.user,
                isLoading = detailState.isLoading,
                error = detailState.error,
                onLoad = { clientDetailViewModel.load(it) },
                onBack = { navController.popBackStack() },
                onPetClick = { pet -> navController.navigate(Routes.petHistory(pet.id)) },
                userId = userId
            )
        }

        composable(
            Routes.ADMIN_RECORDS_FROM_APPT,
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType },
                navArgument("diagnosis") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: return@composable
            val diagnosis = backStackEntry.arguments?.getString("diagnosis") ?: ""
            val recState = medicalRecordViewModel.state.collectAsState().value
            val allPets = allPetsViewModel.state.collectAsState().value.pets
            LaunchedEffect(Unit) { allPetsViewModel.load("") }
            LaunchedEffect(recState.created) {
                if (recState.created) {
                    medicalRecordViewModel.onCreatedHandled()
                    navController.popBackStack()
                }
            }
            com.petpal.app.ui.screens.admin.AddMedicalRecordScreen(
                pets = allPets,
                isLoading = recState.isLoading,
                error = recState.error,
                onSave = { pid, diag, treat, notes ->
                    medicalRecordViewModel.createRecord(pid, diag, treat, notes)
                },
                onBack = { navController.popBackStack() },
                onLoadPets = { allPetsViewModel.load("") },
                onClearError = { medicalRecordViewModel.clearError() },
                preselectedPetId = petId,
                preselectedDiagnosis = diagnosis
            )
        }

        composable(Routes.ADMIN_PET_HISTORY, arguments = listOf(navArgument("petId") { type = NavType.IntType })) {
            val petId = it.arguments?.getInt("petId") ?: return@composable
            val detailState = petDetailViewModel.state.collectAsState().value
            val allPets = allPetsViewModel.state.collectAsState().value.pets
            val pet = allPets.find { p -> p.id == petId }
            if (pet != null) {
                com.petpal.app.ui.screens.owner.PetDetailScreen(
                    pet = pet,
                    records = detailState.records,
                    isLoading = detailState.isLoading,
                    error = detailState.error,
                    onLoadHistory = { petDetailViewModel.loadHistory(it) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
