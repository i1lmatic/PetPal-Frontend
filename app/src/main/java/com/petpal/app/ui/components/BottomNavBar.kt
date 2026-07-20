package com.petpal.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Pets("Mascotas", Icons.Filled.Pets, Icons.Outlined.Pets, "pets"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "appointments"),
    Profile("Perfil", Icons.Filled.Person, Icons.Outlined.Person, "profile")
}

enum class AdminNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
) {
    Users("Usuarios", Icons.Filled.Group, Icons.Outlined.Group, "admin_users"),
    Appointments("Citas", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "admin_appointments"),
    Records("Historial", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices, "admin_records"),
    Leave("Salir", Icons.Filled.Logout, Icons.Outlined.Logout, "salir")
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
        BottomNavItem.entries.forEach { item ->
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
            val isLeave = item.route == "salir"
            val selected = currentRoute == item.route && !isLeave
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (isLeave) onLogout()
                    else onNavigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (isLeave) item.unselectedIcon else
                            if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isLeave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    selectedTextColor = if (isLeave) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    indicatorColor = if (isLeave) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
