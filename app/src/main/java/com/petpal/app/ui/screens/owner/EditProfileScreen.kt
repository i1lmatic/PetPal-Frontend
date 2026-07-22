package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.User
import com.petpal.app.ui.components.GradientHeader

@Composable
fun EditProfileScreen(
    user: User?,
    isLoading: Boolean,
    error: String?,
    onSave: (String?, String?) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var fullName by remember { mutableStateOf(user?.full_name ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GradientHeader(title = "Editar Perfil", subtitle = "Actualiza tu información")

        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; onClearError() },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Filled.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; onClearError() },
                label = { Text("Telefono") },
                leadingIcon = { Icon(Icons.Filled.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) { Text("Cancelar") }

                Button(
                    onClick = { onSave(fullName.trim().ifEmpty { null }, phone.trim().ifEmpty { null }) },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) CircularProgressIndicator(
                        Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    ) else Text("Guardar")
                }
            }
        }
    }
}
