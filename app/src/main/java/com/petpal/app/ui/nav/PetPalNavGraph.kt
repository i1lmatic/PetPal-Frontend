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
import com.petpal.app.ui.screens.superadmin.SuperDashboardScreen
import com.petpal.app.ui.screens.superadmin.ManageUsersScreen
import com.petpal.app.ui.screens.superadmin.ManageVetsScreen
import com.petpal.app.ui.screens.superadmin.SuperAllAppointmentsScreen
import com.petpal.app.ui.screens.superadmin.SystemConfigScreen
import java.net.URLEncoder

object Routes {
    // --- Autenticación ---
    const val LOGIN = "login"
    const val REGISTER_OWNER = "register_owner"
    const val REGISTER_VET = "register_vet"
    const val PENDING = "pending"

    // --- Módulo Cuidadores (Owner) ---
    const val PETS_LIST = "pets"
    const val ADD_PET = "add_pet"
    const val PET_DETAIL = "pet_detail/{petId}"
    const val SEARCH_VETS = "search_vets"
    const val VET_DETAIL = "vet_detail/{vetId}"
    const val BOOK_APPOINTMENT = "book_appointment/{vetId}"
    const val APPOINTMENTS = "appointments"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"

    // --- Módulo Veterinaria (Vet) ---
    const val VET_DASHBOARD = "vet_dashboard"
    const val VET_APPOINTMENTS = "vet_appointments"
    const val VET_PATIENTS = "vet_patients"
    const val VET_BUSINESS = "vet_business"
    const val VET_PROFILE = "vet_profile"
    const val VET_ADD_RECORD = "vet_add_record/{appointmentId}"

    // --- Módulo Superusuario (Super) ---
    const val SUPER_DASHBOARD = "super_dashboard"
    const val SUPER_USERS = "super_users"
    const val SUPER_VETS = "super_vets"
    const val SUPER_APPOINTMENTS = "super_appointments"
    const val SUPER_CONFIG = "super_config"

    // --- Módulo Administrador (Admin) ---
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_USERS = "admin_users"
    const val ADMIN_APPOINTMENTS = "admin_appointments"
    const val ADMIN_PETS = "admin_pets"
    const val ADMIN_RECORDS = "admin_records"
    const val ADMIN_CLIENT_DETAIL = "admin_client_detail/{userId}"
    const val ADMIN_PET_HISTORY = "admin_pet_history/{petId}"
    const val ADMIN_RECORDS_FROM_APPT = "admin_records?petId={petId}&diagnosis={diagnosis}&appointmentId={appointmentId}"

    // --- Helper functions para rutas dinámicas ---
    fun petDetail(petId: Int) = "pet_detail/$petId"
    fun vetDetail(vetId: Int) = "vet_detail/$vetId"
    fun bookAppointment(vetId: Int) = "book_appointment/$vetId"
    fun vetAddRecord(appointmentId: Int) = "vet_add_record/$appointmentId"
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
    vetSearchViewModel: VetSearchViewModel,
    adminViewModel: AdminViewModel,
    medicalRecordViewModel: AddMedicalRecordViewModel,
    dashboardViewModel: DashboardViewModel,
    allPetsViewModel: AllPetsViewModel,
    activeUsersViewModel: ActiveUsersViewModel,
    clientDetailViewModel: ClientDetailViewModel,
    editProfileViewModel: EditProfileViewModel,
    manageUsersViewModel: ManageUsersViewModel,
    manageVetsViewModel: ManageVetsViewModel,
    vetDashboardViewModel: VetDashboardViewModel,
    vetAppointmentsViewModel: VetAppointmentsViewModel,
    vetPatientsViewModel: VetPatientsViewModel,
    vetBusinessViewModel: VetBusinessViewModel,
    vetMedicalRecordViewModel: VetAddMedicalRecordViewModel,
    navController: NavHostController = rememberNavController()
) {
    val authState by authViewModel.state.collectAsState()

    Log.d("PetPalFlow", "NAV: recompose isLoggedIn=${authState.isLoggedIn} role=${authState.role}")

    val isOwnerScreen = { route: String? ->
        route == Routes.PETS_LIST || route == Routes.SEARCH_VETS || route == Routes.APPOINTMENTS
                || route == Routes.PROFILE || route == Routes.ADD_PET || route == Routes.EDIT_PROFILE
                || route.toString().startsWith("pet_detail") || route.toString().startsWith("vet_detail")
                || route.toString().startsWith("book_appointment")
    }

    val isVetScreen = { route: String? ->
        route == Routes.VET_DASHBOARD || route == Routes.VET_APPOINTMENTS || route == Routes.VET_PATIENTS
                || route == Routes.VET_BUSINESS || route == Routes.VET_PROFILE
                || route.toString().startsWith("vet_add_record")
    }

    val isSuperScreen = { route: String? ->
        route == Routes.SUPER_DASHBOARD || route == Routes.SUPER_USERS || route == Routes.SUPER_VETS
                || route == Routes.SUPER_APPOINTMENTS || route == Routes.SUPER_CONFIG
                || route == Routes.ADMIN_PETS || route == Routes.ADMIN_RECORDS
                || route.toString().startsWith("admin_client_detail")
                || route.toString().startsWith("admin_pet_history")
    }

    LaunchedEffect(authState.isLoggedIn, authState.role, authState.isPending) {
        if (authState.isCheckingSession) return@LaunchedEffect
        val currentRoute = navController.currentDestination?.route
        Log.d("PetPalFlow", "NAV: isLoggedIn=${authState.isLoggedIn} role=${authState.role} current=$currentRoute")
        when {
            authState.isPending && currentRoute != Routes.PENDING -> {
                Log.d("PetPalFlow", "NAV: -> PENDING")
                navController.navigate(Routes.PENDING) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && (authState.role == "admin" || authState.role == "super") && !isSuperScreen(currentRoute) -> {
                Log.d("PetPalFlow", "NAV: -> SUPER_DASHBOARD")
                navController.navigate(Routes.SUPER_DASHBOARD) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && authState.role == "vet" && !isVetScreen(currentRoute) -> {
                Log.d("PetPalFlow", "NAV: -> VET_DASHBOARD")
                navController.navigate(Routes.VET_DASHBOARD) { popUpTo(0) { inclusive = true } }
            }
            authState.isLoggedIn && (authState.role == "owner" || authState.role == "user" || authState.status == "active") && !isOwnerScreen(currentRoute) && !isSuperScreen(currentRoute) && !isVetScreen(currentRoute) -> {
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
                onNavigateToRegister = { navController.navigate(Routes.REGISTER_OWNER) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onNavigateToRegisterVet = { navController.navigate(Routes.REGISTER_VET) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.REGISTER_OWNER) {
            com.petpal.app.ui.screens.auth.RegisterOwnerScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onRegister = { email, pass, name, phone -> authViewModel.register(email, pass, name, phone) },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
                onClearError = { authViewModel.clearError() }
            )
        }

        composable(Routes.REGISTER_VET) {
            com.petpal.app.ui.screens.auth.RegisterVetScreen(
                isLoading = authState.isLoading,
                error = authState.error,
                onRegister = { email, pass, name, phone, bizName, bizAddr, bizPhone, bizSpecs, bizDesc, bizHours ->
                    authViewModel.registerVet(
                        email, pass, name, phone,
                        bizName, bizAddr, bizPhone, bizSpecs, bizDesc, bizHours
                    )
                },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
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

        val doLogout: () -> Unit = {
            authViewModel.logout()
            navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
        }

        val ownerNavHandler: (String) -> Unit = { route ->
            when (route) {
                "pets" -> navController.navigate(Routes.PETS_LIST) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                "search_vets" -> navController.navigate(Routes.SEARCH_VETS) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                "appointments" -> navController.navigate(Routes.APPOINTMENTS) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
                "profile" -> navController.navigate(Routes.PROFILE) { popUpTo(Routes.PETS_LIST) { inclusive = true } }
            }
        }

        composable(Routes.PETS_LIST) {
            val petsState = petsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.PETS_LIST,
                        onNavigate = ownerNavHandler
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.PetsListScreen(
                    pets = petsState.pets,
                    appointments = emptyList(),
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
                onSave = { name, species, breed, birthDate, weight, sex, color, size, allergies, conditions, microchip ->
                    petsViewModel.createPet(
                        com.petpal.app.data.model.PetCreate(
                            name = name, species = species, breed = breed,
                            birth_date = birthDate, weight = weight,
                            sex = sex, color = color, size = size,
                            allergies = allergies, conditions = conditions, microchip = microchip
                        )
                    )
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
                    onLoadHistory = { petDetailViewModel.loadHistory(petId) },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.SEARCH_VETS) {
            val vetSearchState = vetSearchViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.SEARCH_VETS,
                        onNavigate = ownerNavHandler
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.SearchVetsScreen(
                    vets = vetSearchState.vets,
                    isLoading = vetSearchState.isLoading,
                    error = vetSearchState.error,
                    onSearch = { query -> vetSearchViewModel.searchVets(query) },
                    onVetClick = { vetId -> navController.navigate(Routes.vetDetail(vetId)) },
                    onLoad = { vetSearchViewModel.searchVets() }
                )
            }
        }

        composable(Routes.VET_DETAIL, arguments = listOf(navArgument("vetId") { type = NavType.IntType })) {
            val vetId = it.arguments?.getInt("vetId") ?: return@composable
            val vetSearchState = vetSearchViewModel.state.collectAsState().value
            val vet = vetSearchState.vets.find { v -> v.id == vetId }
            com.petpal.app.ui.screens.owner.VetDetailScreen(
                vet = vet,
                isLoading = vetSearchState.isLoading,
                error = vetSearchState.error,
                onBookAppointment = { id -> navController.navigate(Routes.bookAppointment(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.BOOK_APPOINTMENT, arguments = listOf(navArgument("vetId") { type = NavType.IntType })) {
            val vetId = it.arguments?.getInt("vetId") ?: return@composable
            val petsState = petsViewModel.state.collectAsState().value
            val apptState = appointmentsViewModel.state.collectAsState().value
            LaunchedEffect(Unit) { petsViewModel.loadPets() }
            LaunchedEffect(apptState.created) {
                if (apptState.created) {
                    appointmentsViewModel.onCreatedHandled()
                    navController.popBackStack()
                }
            }
            com.petpal.app.ui.screens.owner.BookAppointmentScreen(
                vetId = vetId,
                pets = petsState.pets,
                isLoading = apptState.isLoading,
                error = apptState.error,
                success = apptState.created,
                slots = apptState.slots,
                slotsLoading = apptState.slotsLoading,
                onLoadSlots = { date -> appointmentsViewModel.loadSlots(vetId, date) },
                onBook = { petId, dateTime, reason, notes ->
                    appointmentsViewModel.createAppointment(
                        com.petpal.app.data.model.AppointmentCreate(
                            pet_id = petId, vet_id = vetId,
                            date_time = dateTime, reason = reason, notes = notes.ifBlank { null }
                        )
                    )
                },
                onBack = { navController.popBackStack() },
                onBackHandled = { navController.popBackStack() }
            )
        }

        composable(Routes.APPOINTMENTS) {
            val apptState = appointmentsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.APPOINTMENTS,
                        onNavigate = ownerNavHandler
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.AppointmentsScreen(
                    appointments = apptState.appointments,
                    isLoading = apptState.isLoading,
                    error = apptState.error,
                    onLoad = { appointmentsViewModel.loadAppointments() }
                )
            }
        }

        composable(Routes.PROFILE) {
            ScreenWithBottomBar(
                bottomBar = {
                    com.petpal.app.ui.components.OwnerBottomBar(
                        currentRoute = Routes.PROFILE,
                        onNavigate = ownerNavHandler
                    )
                }
            ) {
                com.petpal.app.ui.screens.owner.ProfileScreen(
                    user = authState.user,
                    onLogout = doLogout,
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

        val vetNavHandler: (String) -> Unit = { route ->
            when (route) {
                "vet_dashboard" -> navController.navigate(Routes.VET_DASHBOARD) { popUpTo(Routes.VET_DASHBOARD) { inclusive = true } }
                "vet_appointments" -> navController.navigate(Routes.VET_APPOINTMENTS) { popUpTo(Routes.VET_DASHBOARD) { inclusive = true } }
                "vet_patients" -> navController.navigate(Routes.VET_PATIENTS) { popUpTo(Routes.VET_DASHBOARD) { inclusive = true } }
                "vet_business" -> navController.navigate(Routes.VET_BUSINESS) { popUpTo(Routes.VET_DASHBOARD) { inclusive = true } }
                "vet_profile" -> navController.navigate(Routes.VET_PROFILE) { popUpTo(Routes.VET_DASHBOARD) { inclusive = true } }
            }
        }

        composable(Routes.VET_DASHBOARD) {
            com.petpal.app.ui.screens.vet.VetDashboardScreen(
                viewModel = vetDashboardViewModel,
                onAppointmentClick = { appt ->
                    navController.navigate(Routes.VET_APPOINTMENTS)
                },
                onNavigateToBusiness = { navController.navigate(Routes.VET_BUSINESS) },
                bottomBar = {
                    com.petpal.app.ui.components.VetBottomBar(
                        currentRoute = Routes.VET_DASHBOARD,
                        onNavigate = vetNavHandler
                    )
                }
            )
        }

        composable(Routes.VET_APPOINTMENTS) {
            com.petpal.app.ui.screens.vet.VetAppointmentsScreen(
                viewModel = vetAppointmentsViewModel,
                onAppointmentClick = { appt ->
                    if (appt.status.lowercase() == "completed") {
                        navController.navigate(Routes.vetAddRecord(appt.id))
                    }
                },
                bottomBar = {
                    com.petpal.app.ui.components.VetBottomBar(
                        currentRoute = Routes.VET_APPOINTMENTS,
                        onNavigate = vetNavHandler
                    )
                }
            )
        }

        composable(Routes.VET_PATIENTS) {
            com.petpal.app.ui.screens.vet.VetPatientsScreen(
                viewModel = vetPatientsViewModel,
                onPetClick = { },
                bottomBar = {
                    com.petpal.app.ui.components.VetBottomBar(
                        currentRoute = Routes.VET_PATIENTS,
                        onNavigate = vetNavHandler
                    )
                }
            )
        }

        composable(Routes.VET_BUSINESS) {
            com.petpal.app.ui.screens.vet.VetBusinessScreen(
                viewModel = vetBusinessViewModel,
                bottomBar = {
                    com.petpal.app.ui.components.VetBottomBar(
                        currentRoute = Routes.VET_BUSINESS,
                        onNavigate = vetNavHandler
                    )
                }
            )
        }

        composable(Routes.VET_PROFILE) {
            com.petpal.app.ui.screens.vet.VetProfileScreen(
                user = authState.user,
                onLogout = doLogout,
                onEditProfile = { navController.navigate(Routes.EDIT_PROFILE) },
                bottomBar = {
                    com.petpal.app.ui.components.VetBottomBar(
                        currentRoute = Routes.VET_PROFILE,
                        onNavigate = vetNavHandler
                    )
                }
            )
        }

        composable(
            Routes.VET_ADD_RECORD,
            arguments = listOf(navArgument("appointmentId") { type = NavType.IntType })
        ) { backStackEntry ->
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: -1
            val apptState = vetAppointmentsViewModel.state.collectAsState().value
            val targetAppt = apptState.appointments.find { it.id == appointmentId }
            val recState = vetMedicalRecordViewModel.state.collectAsState().value

            LaunchedEffect(recState.created) {
                if (recState.created) {
                    vetMedicalRecordViewModel.onCreatedHandled()
                    vetAppointmentsViewModel.loadAppointments()
                    navController.popBackStack()
                }
            }

            com.petpal.app.ui.screens.vet.AddMedicalRecordScreen(
                appointment = targetAppt,
                isLoading = recState.isLoading,
                error = recState.error,
                onSave = { petId, diag, treat, notes, apptId ->
                    vetMedicalRecordViewModel.createRecord(apptId ?: -1, diag, treat, notes)
                },
                onBack = { navController.popBackStack() }
            )
        }

        val superNavHandler: (String) -> Unit = { route ->
            when (route) {
                "super_dashboard" -> navController.navigate(Routes.SUPER_DASHBOARD) { popUpTo(Routes.SUPER_DASHBOARD) { inclusive = true } }
                "super_users" -> navController.navigate(Routes.SUPER_USERS) { popUpTo(Routes.SUPER_DASHBOARD) { inclusive = true } }
                "super_vets" -> navController.navigate(Routes.SUPER_VETS) { popUpTo(Routes.SUPER_DASHBOARD) { inclusive = true } }
                "super_appointments" -> navController.navigate(Routes.SUPER_APPOINTMENTS) { popUpTo(Routes.SUPER_DASHBOARD) { inclusive = true } }
                "super_config" -> navController.navigate(Routes.SUPER_CONFIG) { popUpTo(Routes.SUPER_DASHBOARD) { inclusive = true } }
            }
        }

        composable(Routes.SUPER_DASHBOARD) {
            val dashState = dashboardViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = { com.petpal.app.ui.components.SuperBottomBar(currentRoute = Routes.SUPER_DASHBOARD, onNavigate = superNavHandler, onLogout = doLogout) }
            ) {
                SuperDashboardScreen(
                    stats = dashState.stats, isLoading = dashState.isLoading, error = dashState.error,
                    onLoad = { dashboardViewModel.load() }, onNavigate = { r -> navController.navigate(r) }
                )
            }
        }

        composable(Routes.SUPER_USERS) {
            val state = manageUsersViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = { com.petpal.app.ui.components.SuperBottomBar(currentRoute = Routes.SUPER_USERS, onNavigate = superNavHandler, onLogout = doLogout) }
            ) {
                ManageUsersScreen(
                    pendingUsers = state.pendingUsers, activeUsers = state.activeUsers,
                    isLoading = state.isLoading, error = state.error,
                    onLoadPending = { manageUsersViewModel.loadPending() }, onLoadActive = { manageUsersViewModel.loadActive() },
                    onApprove = { manageUsersViewModel.approve(it) }, onReject = { manageUsersViewModel.reject(it) },
                    onDeactivate = { manageUsersViewModel.deactivate(it) }, onReactivate = { manageUsersViewModel.reactivate(it) },
                    onUserClick = { user -> navController.navigate(Routes.clientDetail(user.id)) }
                )
            }
        }

        composable(Routes.SUPER_VETS) {
            val state = manageVetsViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = { com.petpal.app.ui.components.SuperBottomBar(currentRoute = Routes.SUPER_VETS, onNavigate = superNavHandler, onLogout = doLogout) }
            ) {
                ManageVetsScreen(
                    vets = state.vets, pendingVets = state.pendingVets, isLoading = state.isLoading, error = state.error,
                    onLoad = { manageVetsViewModel.load() }, onLoadPending = { manageVetsViewModel.loadPending() },
                    onDeactivate = { manageVetsViewModel.deactivate(it) }, onReactivate = { manageVetsViewModel.reactivate(it) },
                    onApproveVet = { manageVetsViewModel.approveVet(it) }, onRejectVet = { manageVetsViewModel.rejectVet(it) }
                )
            }
        }

        composable(Routes.SUPER_APPOINTMENTS) {
            val adminState = adminViewModel.state.collectAsState().value
            ScreenWithBottomBar(
                bottomBar = { com.petpal.app.ui.components.SuperBottomBar(currentRoute = Routes.SUPER_APPOINTMENTS, onNavigate = superNavHandler, onLogout = doLogout) }
            ) {
                SuperAllAppointmentsScreen(
                    appointments = adminState.allAppointments, isLoading = adminState.isLoading, error = adminState.error,
                    onLoad = { adminViewModel.loadAllAppointments() },
                    onUpdateStatus = { id, status -> adminViewModel.updateAppointmentStatus(id, status) }
                )
            }
        }

        composable(Routes.SUPER_CONFIG) {
            ScreenWithBottomBar(
                bottomBar = { com.petpal.app.ui.components.SuperBottomBar(currentRoute = Routes.SUPER_CONFIG, onNavigate = superNavHandler, onLogout = doLogout) }
            ) {
                SystemConfigScreen(onLogout = doLogout)
            }
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
                        navController.navigate("admin_records?petId=${appt.pet_id}&diagnosis=${URLEncoder.encode(appt.reason, "UTF-8")}&appointmentId=${appt.id}")
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
                    onSave = { petId, diag, treat, notes, _ ->
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
                onLoad = { clientDetailViewModel.load(userId) },
                onBack = { navController.popBackStack() },
                onPetClick = { pet -> navController.navigate(Routes.petHistory(pet.id)) },
                userId = userId
            )
        }

        composable(
            Routes.ADMIN_RECORDS_FROM_APPT,
            arguments = listOf(
                navArgument("petId") { type = NavType.IntType },
                navArgument("diagnosis") { type = NavType.StringType; defaultValue = "" },
                navArgument("appointmentId") { type = NavType.IntType; defaultValue = -1 }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getInt("petId") ?: return@composable
            val diagnosis = backStackEntry.arguments?.getString("diagnosis") ?: ""
            val appointmentId = backStackEntry.arguments?.getInt("appointmentId") ?: -1
            val apptIdToPass = if (appointmentId > 0) appointmentId else null
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
                onSave = { pid, diag, treat, notes, _ ->
                    medicalRecordViewModel.createRecord(pid, diag, treat, notes, apptIdToPass)
                },
                onBack = { navController.popBackStack() },
                onLoadPets = { allPetsViewModel.load("") },
                onClearError = { medicalRecordViewModel.clearError() },
                preselectedPetId = petId,
                preselectedDiagnosis = diagnosis,
                appointmentId = apptIdToPass
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
                    onLoadHistory = { petDetailViewModel.loadHistory(petId) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
