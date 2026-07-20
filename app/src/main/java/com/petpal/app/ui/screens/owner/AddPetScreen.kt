package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    isLoading: Boolean,
    error: String?,
    onSave: (String, String, String, String, Double) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        birthDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text(
            text = "Nueva mascota",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; onClearError() },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Filled.Pets, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = species,
            onValueChange = { species = it; onClearError() },
            label = { Text("Especie (perro, gato...)") },
            leadingIcon = { Icon(Icons.Filled.Pets, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = breed,
            onValueChange = { breed = it; onClearError() },
            label = { Text("Raza") },
            leadingIcon = { Icon(Icons.Filled.Label, null) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
            trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Filled.EditCalendar, null) } },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it; onClearError() },
            label = { Text("Peso (kg)") },
            leadingIcon = { Icon(Icons.Filled.MonitorWeight, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) { Text("Cancelar") }

            Button(
                onClick = {
                    val w = weight.toDoubleOrNull()
                    if (w != null && name.isNotBlank() && species.isNotBlank() && breed.isNotBlank() && birthDate.isNotBlank()) {
                        onSave(name.trim(), species.trim(), breed.trim(), birthDate, w)
                    }
                },
                enabled = name.isNotBlank() && species.isNotBlank() && breed.isNotBlank() && birthDate.isNotBlank()
                        && weight.toDoubleOrNull() != null && !isLoading,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Guardar")
            }
        }
    }
}
