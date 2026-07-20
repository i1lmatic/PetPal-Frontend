package com.petpal.app.ui.screens.owner

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentScreen(
    pets: List<Pet>,
    isLoading: Boolean,
    error: String?,
    onSave: (Int, String, String) -> Unit,
    onBack: () -> Unit,
    onLoadPets: () -> Unit,
    onClearError: () -> Unit
) {
    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var reason by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var petDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { onLoadPets() }

    if (showDatePicker) {
        val dateState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = dateState) }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Hora") },
            text = { TimePicker(state = timeState) },
            confirmButton = {
                TextButton(onClick = {
                    time = String.format("%02d:%02d", timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") } }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        Text("Nueva cita", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = petDropdownExpanded,
            onExpandedChange = { petDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = pets.find { it.id == selectedPetId }?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Mascota") },
                leadingIcon = { Icon(Icons.Filled.Pets, null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = petDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = MaterialTheme.shapes.medium
            )
            ExposedDropdownMenu(expanded = petDropdownExpanded, onDismissRequest = { petDropdownExpanded = false }) {
                pets.forEach { pet ->
                    DropdownMenuItem(
                        text = { Text("${pet.name} (${pet.species})") },
                        onClick = { selectedPetId = pet.id; petDropdownExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = date,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
            trailingIcon = { IconButton(onClick = { showDatePicker = true }) { Icon(Icons.Filled.EditCalendar, null) } },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = time,
            onValueChange = {},
            readOnly = true,
            label = { Text("Hora") },
            leadingIcon = { Icon(Icons.Filled.Schedule, null) },
            trailingIcon = { IconButton(onClick = { showTimePicker = true }) { Icon(Icons.Filled.Edit, null) } },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = reason,
            onValueChange = { reason = it; onClearError() },
            label = { Text("Motivo") },
            leadingIcon = { Icon(Icons.Filled.Description, null) },
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
                    val dateTime = "${date}T${time}:00"
                    if (selectedPetId != null) onSave(selectedPetId!!, dateTime, reason.trim())
                },
                enabled = selectedPetId != null && date.isNotBlank() && time.isNotBlank() && reason.isNotBlank() && !isLoading,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Agendar")
            }
        }
    }
}
