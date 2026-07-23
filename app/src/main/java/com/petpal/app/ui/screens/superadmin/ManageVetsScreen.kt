package com.petpal.app.ui.screens.superadmin

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
import com.petpal.app.data.model.User
import com.petpal.app.data.model.Veterinary
import com.petpal.app.ui.components.GradientHeader
import com.petpal.app.ui.components.StatusBadge

@Composable
fun ManageVetsScreen(
    vets: List<Veterinary>,
    pendingVets: List<User>,
    isLoading: Boolean,
    error: String?,
    onLoad: () -> Unit,
    onLoadPending: () -> Unit,
    onDeactivate: (Int) -> Unit,
    onReactivate: (Int) -> Unit,
    onApproveVet: (Int) -> Unit,
    onRejectVet: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { onLoad(); onLoadPending() }

    val activeVets = vets.filter { it.status == "active" }
    val inactiveVets = vets.filter { it.status != "active" }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(title = "Veterinarias", subtitle = "${activeVets.size} activas \u2022 ${pendingVets.size} pendientes")

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pendientes")
                    if (pendingVets.isNotEmpty()) { Spacer(Modifier.width(6.dp)); Badge { Text("${pendingVets.size}") } }
                }
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activas")
                    if (activeVets.isNotEmpty()) { Spacer(Modifier.width(6.dp)); Badge { Text("${activeVets.size}") } }
                }
            })
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Inactivas")
                    if (inactiveVets.isNotEmpty()) { Spacer(Modifier.width(6.dp)); Badge { Text("${inactiveVets.size}") } }
                }
            })
        }

        when (selectedTab) {
            0 -> {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (error != null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(error, color = MaterialTheme.colorScheme.error)
                            TextButton(onClick = onLoadPending) { Text("Reintentar") }
                        }
                    }
                } else if (pendingVets.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            Text("No hay veterinarias pendientes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(pendingVets, key = { it.id }) { vet ->
                            Card(shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.LocalHospital, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp)) }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(vet.full_name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(vet.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("Solicitud de registro como veterinaria", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Button(onClick = { onApproveVet(vet.id) }, shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), modifier = Modifier.width(90.dp)) {
                                            Icon(Icons.Filled.Check, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Aprobar", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        OutlinedButton(onClick = { onRejectVet(vet.id) }, shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), modifier = Modifier.width(90.dp)) {
                                            Icon(Icons.Filled.Close, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Rechazar", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (activeVets.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.LocalHospital, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                            Spacer(Modifier.height(12.dp))
                            Text("No hay veterinarias activas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(activeVets, key = { it.id }) { vet -> VetCard(vet) { onDeactivate(vet.id) } }
                    }
                }
            }
            2 -> {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                } else if (inactiveVets.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay veterinarias inactivas", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(inactiveVets, key = { it.id }) { vet -> VetCard(vet, showReactivate = true) { onReactivate(vet.id) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun VetCard(vet: Veterinary, showReactivate: Boolean = false, onAction: () -> Unit) {
    Card(shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.LocalHospital, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(vet.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(vet.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(status = vet.status)
            }

            if (vet.specialties.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("Especialidades: ${vet.specialties}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (vet.owner_name != null) {
                Text("Veterinario: ${vet.owner_name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(12.dp))

            if (showReactivate) {
                Button(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reactivar", style = MaterialTheme.typography.labelSmall)
                }
            } else {
                OutlinedButton(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Block, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Desactivar", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
