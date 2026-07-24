package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.petpal.app.data.model.Pet
import com.petpal.app.data.model.VetSlotsResponse
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
    slots: VetSlotsResponse?,
    slotsLoading: Boolean,
    onLoadSlots: (String) -> Unit,
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
    var petExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(success) {
        if (success == true) {
            snackbarHostState.showSnackbar("Cita agendada exitosamente", duration = SnackbarDuration.Short)
            showSuccessSnackbar = true
        }
    }
    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            onBackHandled()
        }
    }
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
        }
    }
    LaunchedEffect(date) {
        if (date.isNotBlank()) {
            time = ""
            onLoadSlots(date)
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
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

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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

                if (slots != null && date.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    WorkingHoursCard(slots.working_hours)
                    Spacer(modifier = Modifier.height(12.dp))
                    TimeSlotsGrid(
                        allSlots = slots.slots,
                        bookedSlots = slots.booked,
                        selectedTime = time,
                        onSelect = { time = it }
                    )
                } else if (date.isNotBlank() && slotsLoading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

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

                val displayError = validationError
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
}

@Composable
private fun WorkingHoursCard(workingHours: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Schedule, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Horario de atención: $workingHours",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TimeSlotsGrid(
    allSlots: List<String>,
    bookedSlots: List<String>,
    selectedTime: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            "Horarios disponibles",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val rows = allSlots.chunked(4)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { slot ->
                    val isBooked = slot in bookedSlots
                    val isSelected = slot == selectedTime
                    val bgColor = when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isBooked -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    }
                    val textColor = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        isBooked -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 3.dp)
                            .background(bgColor, RoundedCornerShape(8.dp))
                            .then(
                                if (isBooked) Modifier
                                else Modifier.clickable { onSelect(slot) }
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            slot,
                            color = textColor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Text(
            if (bookedSlots.isNotEmpty()) "Los horarios en gris ya están reservados" else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
