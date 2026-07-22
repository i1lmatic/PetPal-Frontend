package com.petpal.app.ui.screens.super

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.DashboardStats
import com.petpal.app.ui.components.GradientHeader

@Composable
fun SuperDashboardScreen(
    stats: DashboardStats?,
    isLoading: Boolean,
    error: String?,
    onLoad: () -> Unit,
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) { onLoad() }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(title = "Panel de Administración", subtitle = "PetPal v2")

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.ErrorOutline, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onLoad) { Text("Reintentar") }
                }
            }
        } else if (stats != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard("Usuarios", "${stats.total_users}", Icons.Filled.Group, Modifier.weight(1f))
                    StatCard("Vets", "${stats.total_vets_active}", Icons.Filled.LocalHospital, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard("Mascotas", "${stats.total_pets}", Icons.Filled.Pets, Modifier.weight(1f))
                    StatCard("Citas", "${stats.total_appointments}", Icons.Filled.CalendarMonth, Modifier.weight(1f))
                }

                Spacer(Modifier.height(4.dp))

                QuickAccessCard("Gestionar Usuarios", Icons.Filled.Group, "admin_users", onNavigate)
                QuickAccessCard("Gestionar Veterinarias", Icons.Filled.LocalHospital, "super_vets", onNavigate)
                QuickAccessCard("Ver Mascotas", Icons.Filled.Pets, "admin_pets", onNavigate)
                QuickAccessCard("Todas las Citas", Icons.Filled.CalendarMonth, "super_appointments", onNavigate)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
private fun QuickAccessCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, route: String, onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onNavigate(route) },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(22.dp)) }
            }
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
