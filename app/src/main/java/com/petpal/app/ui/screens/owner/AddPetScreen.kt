package com.petpal.app.ui.screens.owner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.petpal.app.ui.components.GradientHeader
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    isLoading: Boolean,
    error: String?,
    onSave: (
        name: String, species: String, breed: String, birthDate: String, weight: Double,
        sex: String, color: String, size: String, allergies: String?, conditions: String?, microchip: String?
    ) -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var conditions by remember { mutableStateOf("") }
    var microchip by remember { mutableStateOf("") }
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GradientHeader(title = "Nueva mascota", subtitle = "Registra tu mascota")

        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; onClearError() },
                label = { Text("Nombre *") },
                leadingIcon = { Icon(Icons.Filled.Pets, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = species,
                onValueChange = { species = it; onClearError() },
                label = { Text("Especie (perro, gato...) *") },
                leadingIcon = { Icon(Icons.Filled.Category, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { breed = it; onClearError() },
                label = { Text("Raza *") },
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
                label = { Text("Fecha de nacimiento *") },
                leadingIcon = { Icon(Icons.Filled.CalendarToday, null) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.EditCalendar, null)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it; onClearError() },
                label = { Text("Peso (kg) *") },
                leadingIcon = { Icon(Icons.Filled.MonitorWeight, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                var sexExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = sexExpanded,
                    onExpandedChange = { sexExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = if (sex.isEmpty()) "" else if (sex == "male") "\u2642 Macho" else "\u2640 Hembra",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sexo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(expanded = sexExpanded, onDismissRequest = { sexExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("\u2642 Macho") },
                            onClick = { sex = "male"; sexExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("\u2640 Hembra") },
                            onClick = { sex = "female"; sexExpanded = false }
                        )
                    }
                }

                var sizeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = sizeExpanded,
                    onExpandedChange = { sizeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = when (size) {
                            "small" -> "Pequeño"
                            "medium" -> "Mediano"
                            "large" -> "Grande"
                            else -> ""
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tamaño") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeExpanded) },
                        modifier = Modifier.menuAnchor(),
                        shape = MaterialTheme.shapes.medium
                    )
                    ExposedDropdownMenu(expanded = sizeExpanded, onDismissRequest = { sizeExpanded = false }) {
                        DropdownMenuItem(text = { Text("Pequeño") }, onClick = { size = "small"; sizeExpanded = false })
                        DropdownMenuItem(text = { Text("Mediano") }, onClick = { size = "medium"; sizeExpanded = false })
                        DropdownMenuItem(text = { Text("Grande") }, onClick = { size = "large"; sizeExpanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = color,
                onValueChange = { color = it },
                label = { Text("Color") },
                leadingIcon = { Icon(Icons.Filled.Palette, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Alergias") },
                leadingIcon = { Icon(Icons.Filled.Warning, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = conditions,
                onValueChange = { conditions = it },
                label = { Text("Condiciones médicas") },
                leadingIcon = { Icon(Icons.Filled.MedicalServices, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = microchip,
                onValueChange = { microchip = it },
                label = { Text("Número de microchip") },
                leadingIcon = { Icon(Icons.Filled.Memory, null) },
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
                            onSave(
                                name.trim(), species.trim(), breed.trim(), birthDate, w,
                                sex, color.trim(), size,
                                allergies.trim().ifEmpty { null },
                                conditions.trim().ifEmpty { null },
                                microchip.trim().ifEmpty { null }
                            )
                        }
                    },
                    enabled = name.isNotBlank() && species.isNotBlank() && breed.isNotBlank() && birthDate.isNotBlank()
                            && weight.toDoubleOrNull() != null && !isLoading,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (isLoading) CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    ) else Text("Guardar")
                }
            }
        }
    }
}
