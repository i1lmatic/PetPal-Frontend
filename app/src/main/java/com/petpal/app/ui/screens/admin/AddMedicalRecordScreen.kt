package com.petpal.app.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicalRecordScreen(
    isLoading: Boolean,
    error: String?,
    onSave: (Int, String, String, String) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var petIdText by remember { mutableStateOf("") }
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text(
            "Nuevo registro m\u00e9dico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = petIdText,
            onValueChange = { petIdText = it; onClearError() },
            label = { Text("ID de mascota") },
            leadingIcon = { Icon(Icons.Filled.Pets, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = diagnosis,
            onValueChange = { diagnosis = it; onClearError() },
            label = { Text("Diagn\u00f3stico") },
            leadingIcon = { Icon(Icons.Filled.Description, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = treatment,
            onValueChange = { treatment = it; onClearError() },
            label = { Text("Tratamiento") },
            leadingIcon = { Icon(Icons.Filled.MedicalServices, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            minLines = 2
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it; onClearError() },
            label = { Text("Notas adicionales") },
            leadingIcon = { Icon(Icons.Filled.Note, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            minLines = 2
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(50.dp), shape = MaterialTheme.shapes.medium) { Text("Cancelar") }
            Button(
                onClick = {
                    val pid = petIdText.toIntOrNull()
                    if (pid != null) onSave(pid, diagnosis.trim(), treatment.trim(), notes.trim())
                },
                enabled = petIdText.toIntOrNull() != null && diagnosis.isNotBlank() && treatment.isNotBlank() && !isLoading,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Guardar")
            }
        }
    }
}
