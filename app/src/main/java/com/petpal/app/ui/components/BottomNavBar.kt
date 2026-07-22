package com.petpal.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class OwnerNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Pets("Mascotas", Icons.Filled.Pets, Icons.Outlined.Pets, "pets"),
    SearchVet("Buscar Vet", Icons.Filled.Search, Icons.Outlined.Search, "search_vets"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "appointments"),
    Profile("Perfil", Icons.Filled.Person, Icons.Outlined.Person, "profile")
}

enum class VetNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Dashboard("Inicio", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "vet_dashboard"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "vet_appointments"),
    Patients("Pacientes", Icons.Filled.Pets, Icons.Outlined.Pets, "vet_patients"),
    Business("Negocio", Icons.Filled.Business, Icons.Outlined.Business, "vet_business"),
    Profile("Perfil", Icons.Filled.Person, Icons.Outlined.Person, "vet_profile")
}

enum class SuperNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Dashboard("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "super_dashboard"),
    Users("Usuarios", Icons.Filled.Group, Icons.Outlined.Group, "super_users"),
    Vets("Vets", Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital, "super_vets"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "super_appointments"),
    Config("Config", Icons.Filled.Settings, Icons.Outlined.Settings, "super_config")
}

@Composable
fun OwnerBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        OwnerNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun VetBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        VetNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

enum class AdminNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Dashboard("Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "admin_dashboard"),
    Users("Usuarios", Icons.Filled.Group, Icons.Outlined.Group, "admin_users"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "admin_appointments"),
    Pets("Mascotas", Icons.Filled.Pets, Icons.Outlined.Pets, "admin_pets")
}

@Composable
fun AdminBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        AdminNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun SuperBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        SuperNavItem.entries.forEach { item ->
            val isLogout = item.route == "super_config"
            val selected = currentRoute == item.route && !isLogout
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (isLogout) onLogout()
                    else onNavigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
