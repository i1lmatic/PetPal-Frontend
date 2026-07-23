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
import com.petpal.app.data.model.Veterinary
import com.petpal.app.ui.components.GradientHeader
import com.petpal.app.ui.components.StatusBadge

@Composable
fun ManageVetsScreen(
    vets: List<Veterinary>,
    isLoading: Boolean,
    error: String?,
    onLoad: () -> Unit,
    onDeactivate: (Int) -> Unit,
    onReactivate: (Int) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { onLoad() }

    val activeVets = vets.filter { it.status == "active" }
    val inactiveVets = vets.filter { it.status != "active" }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(title = "Veterinarias", subtitle = "${activeVets.size} activas")

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activas")
                    if (activeVets.isNotEmpty()) { Spacer(Modifier.width(6.dp)); Badge { Text("${activeVets.size}") } }
                }
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = {
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
                            TextButton(onClick = onLoad) { Text("Reintentar") }
                        }
                    }
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
            1 -> {
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
