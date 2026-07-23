package com.petpal.app.ui.screens.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.Appointment
import com.petpal.app.ui.components.*
import com.petpal.app.vm.VetAppointmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetAppointmentsScreen(
    viewModel: VetAppointmentsViewModel,
    onAppointmentClick: (Appointment) -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    val filteredAppointments = state.appointments.filter { appt ->
        when (state.selectedTab) {
            0 -> appt.status.lowercase() == "pending"
            1 -> appt.status.lowercase() == "confirmed"
            2 -> appt.status.lowercase() == "completed"
            else -> true
        }
    }

    Scaffold(
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.isLoading) {
            LoadingView(message = "Cargando citas...")
        } else if (state.error != null) {
            ErrorView(
                message = state.error ?: "Error al cargar citas",
                onRetry = { viewModel.loadAppointments() }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. Header con gradiente verde
                GradientHeader(
                    title = "Mis Citas",
                    subtitle = "Gestión de solicitudes y consultas"
                )

                // 2. Tabs de estado (Pendientes | Confirmadas | Completadas)
                TabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        text = {
                            Text(
                                text = "Pendientes",
                                fontWeight = if (state.selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        text = {
                            Text(
                                text = "Confirmadas",
                                fontWeight = if (state.selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = state.selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        text = {
                            Text(
                                text = "Completadas",
                                fontWeight = if (state.selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Listado de citas filtradas
                if (filteredAppointments.isEmpty()) {
                    val emptyMessage = when (state.selectedTab) {
                        0 -> "No tienes citas pendientes"
                        1 -> "No tienes citas confirmadas"
                        else -> "No tienes citas completadas"
                    }
                    EmptyView(
                        message = emptyMessage,
                        icon = Icons.Default.EventNote,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredAppointments) { appointment ->
                            VetAppointmentActionCard(
                                appointment = appointment,
                                tabIndex = state.selectedTab,
                                onClick = { onAppointmentClick(appointment) },
                                onAccept = { viewModel.acceptAppointment(appointment.id) },
                                onReject = { viewModel.rejectAppointment(appointment.id) },
                                onComplete = { viewModel.completeAppointment(appointment.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VetAppointmentActionCard(
    appointment: Appointment,
    tabIndex: Int,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onComplete: () -> Unit
) {
    ModernCard(onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila de Info Mascota y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = "Mascota",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        val displayName = buildString {
                            append(appointment.pet_name ?: "Mascota")
                            if (!appointment.owner_name.isNullOrBlank()) {
                                append(" (${appointment.owner_name})")
                            }
                        }
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = appointment.date_time,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                StatusBadge(status = appointment.status)
            }

            // Fila de Motivo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notes,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = appointment.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Acciones según Pestaña
            when (tabIndex) {
                0 -> { // Pendiente: Aceptar / Rechazar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aceptar")
                        }

                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Rechazar")
                        }
                    }
                }
                1 -> { // Confirmada: Completar
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Marcar como Completada")
                    }
                }
                2 -> { // Completadas: Crear Historial Médico
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Crear Historial Médico")
                    }
                }
            }
        }
    }
}
