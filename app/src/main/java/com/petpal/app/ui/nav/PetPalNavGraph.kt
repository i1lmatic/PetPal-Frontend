package com.petpal.app.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_APPOINTMENTS = "admin_appointments"
    const val ADMIN_RECORDS = "admin_records"

    fun petDetail(petId: Int) = "pet_detail/$petId"
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
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authState.isLoggedIn, authState.role, authState.isPending) {
        if (authState.isCheckingSession) return@LaunchedEffect
        val currentRoute = navController.currentDestination?.route
        when {
            authState.isPending && currentRoute != Routes.PENDING -> {
                navController.navigate(Routes.PENDING) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && authState.role == "admin"
                && currentRoute != Routes.ADMIN_USERS
                && currentRoute != Routes.ADMIN_APPOINTMENTS
                && currentRoute != Routes.ADMIN_RECORDS -> {
                navController.navigate(Routes.ADMIN_USERS) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && authState.role == "client" && authState.status == "active"
                && currentRoute != Routes.PETS_LIST
                && currentRoute != Routes.APPOINTMENTS
                && currentRoute != Routes.PROFILE
                && currentRoute != Routes.ADD_PET
                && currentRoute != Routes.ADD_APPOINTMENT
                && !currentRoute.toString().startsWith("pet_detail") -> {
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
                    isLoading = petsState.isLoading,
                    error = petsState.error,
                    onLoadPets = { petsViewModel.loadPets() },
                    onAddPet = { navController.navigate(Routes.ADD_PET) },
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

        composable(
            Routes.PET_DETAIL,
            arguments = listOf(navArgument("petId") { type = NavType.IntType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: return@composable
            val detailState = petDetailViewModel.state.collectAsState().value
            val petsState = petsViewModel.state.collectAsState().value
            val pet = petsState.pets.find { it.id == petId }
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
                    }
                )
            }
        }

        composable(Routes.ADMIN_USERS) {
            val adminState = adminViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_USERS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_records" -> navController.navigate(Routes.ADMIN_RECORDS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.PendingUsersScreen(
                    users = adminState.pendingUsers,
                    isLoading = adminState.isLoading,
                    error = adminState.error,
                    onLoad = { adminViewModel.loadPendingUsers() },
                    onApprove = { adminViewModel.approveUser(it) }
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
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_records" -> navController.navigate(Routes.ADMIN_RECORDS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.AllAppointmentsScreen(
                    appointments = adminState.allAppointments,
                    isLoading = adminState.isLoading,
                    error = adminState.error,
                    onLoad = { adminViewModel.loadAllAppointments() },
                    onUpdateStatus = { id, status -> adminViewModel.updateAppointmentStatus(id, status) }
                )
            }
        }

        composable(Routes.ADMIN_RECORDS) {
            val recState = medicalRecordViewModel.state.collectAsState().value
            LaunchedEffect(recState.created) {
                if (recState.created) medicalRecordViewModel.onCreatedHandled()
            }
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.AdminBottomBar(
                        currentRoute = Routes.ADMIN_RECORDS,
                        onNavigate = { route ->
                            when (route) {
                                "admin_users" -> navController.navigate(Routes.ADMIN_USERS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_appointments" -> navController.navigate(Routes.ADMIN_APPOINTMENTS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                                "admin_records" -> navController.navigate(Routes.ADMIN_RECORDS) { popUpTo(Routes.ADMIN_USERS) { inclusive = true } }
                            }
                        }
                    )
                }
            ) {
                com.petpal.app.ui.screens.admin.AddMedicalRecordScreen(
                    isLoading = recState.isLoading,
                    error = recState.error,
                    onSave = { petId, diag, treat, notes ->
                        medicalRecordViewModel.createRecord(petId, diag, treat, notes)
                    },
                    onBack = { navController.popBackStack() },
                    onClearError = { medicalRecordViewModel.clearError() }
                )
            }
        }
    }
}
