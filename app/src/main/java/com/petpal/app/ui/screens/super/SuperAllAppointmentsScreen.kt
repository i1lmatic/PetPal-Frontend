package com.petpal.app.ui.screens.super

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.Appointment
import com.petpal.app.ui.components.GradientHeader
import com.petpal.app.ui.components.StatusBadge

@Composable
fun SuperAllAppointmentsScreen(
    appointments: List<Appointment>,
    isLoading: Boolean,
    error: String?,
    onLoad: () -> Unit,
    onUpdateStatus: (Int, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Todas", "Pendientes", "Confirmadas", "Completadas")

    LaunchedEffect(Unit) { onLoad() }

    val filtered = when (selectedTab) {
        1 -> appointments.filter { it.status == "pending" }
        2 -> appointments.filter { it.status == "confirmed" }
        3 -> appointments.filter { it.status == "completed" }
        else -> appointments
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(title = "Todas las Citas", subtitle = "${appointments.size} citas en el sistema")

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = onLoad) { Text("Reintentar") }
                }
            }
        } else if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CalendarMonth, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(12.dp))
                    Text("No hay citas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered, key = { it.id }) { appt ->
                    Card(shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(appt.pet_name ?: "Mascota #${appt.pet_id}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                StatusBadge(status = appt.status)
                            }
                            if (!appt.owner_name.isNullOrBlank()) {
                                Spacer(Modifier.height(2.dp))
                                Text(appt.owner_name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(appt.reason, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(2.dp))
                            Text(appt.date_time.replace("T", " ").take(16), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)

                            if (appt.status == "pending") {
                                Spacer(Modifier.height(12.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = { onUpdateStatus(appt.id, "confirmed") }, shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Confirmar", style = MaterialTheme.typography.labelSmall)
                                    }
                                    OutlinedButton(onClick = { onUpdateStatus(appt.id, "cancelled") }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), modifier = Modifier.weight(1f)) {
                                        Icon(Icons.Filled.Cancel, null, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(4.dp)); Text("Cancelar", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
