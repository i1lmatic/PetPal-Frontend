package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.Pet
import com.petpal.app.ui.components.GradientHeader
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookAppointmentScreen(
    vetId: Int,
    pets: List<Pet>,
    isLoading: Boolean,
    error: String?,
    success: Boolean?,
    onBook: (petId: Int, dateTime: String, reason: String, notes: String) -> Unit,
    onBack: () -> Unit,
    onBackHandled: () -> Unit
) {
    var selectedPetId by remember { mutableStateOf<Int?>(null) }
    var reason by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var petExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(success) {
        if (success == true) onBackHandled()
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        date = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    time = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GradientHeader(title = "Agendar Cita", subtitle = "Selecciona fecha y motivo")

        Column(modifier = Modifier.padding(20.dp)) {
            ExposedDropdownMenuBox(
                expanded = petExpanded,
                onExpandedChange = { petExpanded = it }
            ) {
                OutlinedTextField(
                    value = pets.find { it.id == selectedPetId }?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Seleccionar mascota *") },
                    leadingIcon = { Icon(Icons.Filled.Pets, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = petExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = petExpanded, onDismissRequest = { petExpanded = false }) {
                    pets.forEach { pet ->
                        DropdownMenuItem(
                            text = { Text("${pet.name} (${pet.species})") },
                            onClick = { selectedPetId = pet.id; petExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha *") },
                leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.EditCalendar, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                label = { Text("Hora *") },
                leadingIcon = { Icon(Icons.Filled.Schedule, null) },
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        Icon(Icons.Filled.EditCalendar, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                label = { Text("Motivo de la visita *") },
                leadingIcon = { Icon(Icons.Filled.Description, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas adicionales") },
                leadingIcon = { Icon(Icons.Filled.Notes, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                minLines = 2
            )

            val displayError = error ?: validationError
            if (displayError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(displayError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
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
                        validationError = null
                        if (selectedPetId != null && date.isNotBlank() && time.isNotBlank() && reason.isNotBlank()) {
                            val today = LocalDate.now().toString()
                            if (date < today) {
                                validationError = "No puedes agendar citas en fechas pasadas"
                            } else if (date == today) {
                                val now = LocalTime.now()
                                val selected = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
                                if (selected.isBefore(now)) {
                                    validationError = "No puedes agendar citas en un horario que ya pasó"
                                } else {
                                    onBook(selectedPetId!!, "${date}T${time}:00", reason.trim(), notes.trim())
                                }
                            } else {
                                onBook(selectedPetId!!, "${date}T${time}:00", reason.trim(), notes.trim())
                            }
                        }
                    },
                    enabled = selectedPetId != null && date.isNotBlank() && time.isNotBlank() && reason.isNotBlank() && !isLoading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    ) else Text("Agendar")
                }
            }
        }
    }
}
