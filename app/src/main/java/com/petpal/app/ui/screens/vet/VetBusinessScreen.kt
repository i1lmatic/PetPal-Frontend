package com.petpal.app.ui.screens.vet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petpal.app.ui.components.*
import com.petpal.app.vm.VetBusinessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetBusinessScreen(
    viewModel: VetBusinessViewModel,
    bottomBar: @Composable () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    var name by remember(state.name) { mutableStateOf(state.name) }
    var address by remember(state.address) { mutableStateOf(state.address) }
    var phone by remember(state.phone) { mutableStateOf(state.phone) }
    var specialties by remember(state.specialties) { mutableStateOf(state.specialties) }
    var workingHours by remember(state.workingHours) { mutableStateOf(state.workingHours) }
    var description by remember(state.description) { mutableStateOf(state.description) }

    var showDeactivateDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("Cambios guardados correctamente")
            viewModel.onSavedHandled()
        }
    }

    Scaffold(
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (state.isLoading) {
            LoadingView(message = "Guardando información del negocio...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 1. Header con gradiente verde
                GradientHeader(
                    title = if (state.hasBusiness) "Mi Negocio" else "Registrar Negocio",
                    subtitle = if (state.hasBusiness) "Configuración y datos de la clínica" else "Completa los datos de tu veterinaria"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (state.error != null) {
                        Text(
                            text = state.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Campo: Nombre de la Clínica
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la clínica") },
                        leadingIcon = {
                            Icon(Icons.Default.Business, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo: Dirección
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Dirección") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo: Teléfono
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono de contacto") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo: Especialidades
                    OutlinedTextField(
                        value = specialties,
                        onValueChange = { specialties = it },
                        label = { Text("Especialidades") },
                        leadingIcon = {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo: Horarios de atención (interactivo)
                    Text(
                        text = "Horarios de atención",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    WorkingHoursPicker(
                        value = workingHours,
                        onValueChange = { workingHours = it }
                    )

                    // Campo: Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción de la clínica") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón Guardar Cambios
                    Button(
                        onClick = {
                            viewModel.saveBusiness(
                                name = name,
                                address = address,
                                phone = phone,
                                specialties = specialties,
                                workingHours = workingHours,
                                description = description
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.hasBusiness) "Guardar Cambios" else "Crear Negocio",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Botón Desactivar Negocio (solo si ya existe el negocio)
                    if (state.hasBusiness) {
                        OutlinedButton(
                            onClick = { showDeactivateDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Desactivar Negocio",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para desactivación
    if (showDeactivateDialog) {
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text("¿Desactivar negocio?") },
            text = { Text("Si desactivas tu negocio, los dueños de mascotas no podrán agendar nuevas citas hasta que sea reactivado por el administrador.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeactivateDialog = false
                        viewModel.deactivateBusiness()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirmar Desactivación")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
