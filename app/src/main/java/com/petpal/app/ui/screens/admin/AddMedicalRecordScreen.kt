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
import com.petpal.app.data.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicalRecordScreen(
    pets: List<Pet>,
    isLoading: Boolean,
    error: String?,
    onSave: (Int, String, String, String) -> Unit,
    onBack: () -> Unit,
    onLoadPets: () -> Unit,
    onClearError: () -> Unit,
    preselectedPetId: Int? = null,
    preselectedDiagnosis: String? = null
) {
    var selectedPetId by remember { mutableStateOf(preselectedPetId) }
    var diagnosis by remember { mutableStateOf(preselectedDiagnosis ?: "") }
    var treatment by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onLoadPets() }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text("Nuevo registro medico", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded && preselectedPetId == null,
            onExpandedChange = { if (preselectedPetId == null) dropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = pets.find { it.id == selectedPetId }?.let { "${it.name} (${it.species})" } ?: "",
                onValueChange = {},
                readOnly = true,
                enabled = preselectedPetId == null,
                label = { Text("Mascota") },
                leadingIcon = { Icon(Icons.Filled.Pets, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }) {
                pets.forEach { pet ->
                    DropdownMenuItem(
                        text = { Text("${pet.name} (${pet.species}, ID:${pet.id})") },
                        onClick = { selectedPetId = pet.id; dropdownExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = diagnosis,
            onValueChange = { diagnosis = it; onClearError() },
            label = { Text("Diagnostico") },
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
                    if (selectedPetId != null) onSave(selectedPetId!!, diagnosis.trim(), treatment.trim(), notes.trim())
                },
                enabled = selectedPetId != null && diagnosis.isNotBlank() && treatment.isNotBlank() && !isLoading,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Guardar")
            }
        }
    }
}
