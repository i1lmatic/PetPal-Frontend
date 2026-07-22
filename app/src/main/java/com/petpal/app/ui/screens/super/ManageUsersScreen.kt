package com.petpal.app.ui.screens.super

import androidx.compose.foundation.clickable
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
import com.petpal.app.ui.components.GradientHeader
import com.petpal.app.ui.components.StatusBadge

@Composable
fun ManageUsersScreen(
    pendingUsers: List<User>,
    activeUsers: List<User>,
    isLoading: Boolean,
    error: String?,
    onLoadPending: () -> Unit,
    onLoadActive: () -> Unit,
    onApprove: (Int) -> Unit,
    onReject: (Int) -> Unit,
    onDeactivate: (Int) -> Unit,
    onReactivate: (Int) -> Unit,
    onUserClick: (User) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) { onLoadPending(); onLoadActive() }

    Column(modifier = Modifier.fillMaxSize()) {
        GradientHeader(title = "Gestionar Usuarios", subtitle = "${pendingUsers.size} pendientes")

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pendientes")
                    if (pendingUsers.isNotEmpty()) { Spacer(Modifier.width(6.dp)); Badge { Text("${pendingUsers.size}") } }
                }
            })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Activos") })
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
                } else if (pendingUsers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(12.dp))
                            Text("No hay usuarios pendientes", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(pendingUsers, key = { it.id }) { user ->
                            Card(shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.size(48.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp)) }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(user.full_name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text("${user.role} \u2022 ${user.phone}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Button(onClick = { onApprove(user.id) }, shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), modifier = Modifier.width(90.dp)) {
                                            Icon(Icons.Filled.Check, null, modifier = Modifier.size(14.dp)); Spacer(Modifier.width(4.dp)); Text("Aprobar", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        OutlinedButton(onClick = { onReject(user.id) }, shape = MaterialTheme.shapes.small, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error), modifier = Modifier.width(90.dp)) {
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
                } else if (activeUsers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay usuarios activos", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(activeUsers, key = { it.id }) { user ->
                            Card(modifier = Modifier.fillMaxWidth().clickable { onUserClick(user) }, shape = MaterialTheme.shapes.medium, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.primary) }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(user.full_name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                        Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    StatusBadge(status = user.status)
                                    Spacer(Modifier.width(8.dp))
                                    Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
