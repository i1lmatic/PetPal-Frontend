package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.User
import com.petpal.app.ui.components.InfoRow

@Composable
fun ProfileScreen(
    user: User?,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text(
            "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Surface(
                    modifier = Modifier.size(72.dp).align(Alignment.CenterHorizontally),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Person,
                            null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = user?.full_name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = user?.role?.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(20.dp))

                InfoRow("Email", user?.email ?: "")
                Spacer(Modifier.height(12.dp))
                InfoRow("Tel\u00e9fono", user?.phone ?: "")
            }
        }

        Spacer(Modifier.weight(1f))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.Filled.Logout, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesi\u00f3n")
        }
    }
}
