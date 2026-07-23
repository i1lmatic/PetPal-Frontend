package com.petpal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petpal.app.ui.theme.*
import java.util.Calendar

data class DayHours(
    val day: String,
    val abbr: String,
    val enabled: Boolean,
    val openHour: Int,
    val openMinute: Int,
    val closeHour: Int,
    val closeMinute: Int
)

private val ALL_DAYS = listOf(
    "Lunes" to "Lun",
    "Martes" to "Mar",
    "Miércoles" to "Mié",
    "Jueves" to "Jue",
    "Viernes" to "Vie",
    "Sábado" to "Sáb",
    "Domingo" to "Dom"
)

fun formatWorkingHours(days: List<DayHours>): String {
    val enabled = days.filter { it.enabled }
    if (enabled.isEmpty()) return ""
    val open = String.format("%02d:%02d", enabled.first().openHour, enabled.first().openMinute)
    val close = String.format("%02d:%02d", enabled.first().closeHour, enabled.first().closeMinute)
    val sameTime = enabled.all {
        it.openHour == enabled.first().openHour && it.openMinute == enabled.first().openMinute &&
                it.closeHour == enabled.first().closeHour && it.closeMinute == enabled.first().closeMinute
    }
    return if (sameTime) {
        "${enabled.first().abbr}-${enabled.last().abbr} $open-$close"
    } else {
        enabled.joinToString(", ") { "${it.abbr} ${String.format("%02d:%02d", it.openHour, it.openMinute)}-${String.format("%02d:%02d", it.closeHour, it.closeMinute)}" }
    }
}

fun parseWorkingHours(str: String): List<DayHours> {
    if (str.isBlank()) {
        return ALL_DAYS.map { (day, abbr) ->
            DayHours(day, abbr, false, 8, 0, 18, 0)
        }
    }
    val defaultDays = ALL_DAYS.map { (day, abbr) ->
        DayHours(day, abbr, false, 8, 0, 18, 0)
    }
    val result = defaultDays.toMutableList()
    val timePattern = Regex("""(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})""")
    val entries = str.split(",").map { it.trim() }
    for (entry in entries) {
        val timeMatch = timePattern.find(entry)
        if (timeMatch != null) {
            val oh = timeMatch.groupValues[1].toIntOrNull() ?: 8
            val om = timeMatch.groupValues[2].toIntOrNull() ?: 0
            val ch = timeMatch.groupValues[3].toIntOrNull() ?: 18
            val cm = timeMatch.groupValues[4].toIntOrNull() ?: 0
            val dayPart = entry.substringBefore(timeMatch.value).trim()
            val enabledIndices = mutableListOf<Int>()
            if (dayPart.contains("-")) {
                val parts = dayPart.split("-").map { it.trim().lowercase() }
                val startIdx = ALL_DAYS.indexOfFirst { it.second.lowercase() == parts[0] }
                val endIdx = ALL_DAYS.indexOfFirst { it.second.lowercase() == parts.getOrNull(1) ?: parts[0] }
                if (startIdx >= 0 && endIdx >= 0) {
                    for (i in if (startIdx <= endIdx) startIdx..endIdx else endIdx..startIdx) {
                        enabledIndices.add(i)
                    }
                }
            } else if (dayPart.isNotEmpty()) {
                val idx = ALL_DAYS.indexOfFirst { it.second.lowercase() == dayPart.lowercase() || it.first.lowercase() == dayPart.lowercase() }
                if (idx >= 0) enabledIndices.add(idx)
            }
            for (i in enabledIndices) {
                result[i] = result[i].copy(enabled = true, openHour = oh, openMinute = om, closeHour = ch, closeMinute = cm)
            }
        }
    }
    return result
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkingHoursPicker(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = remember(value) { parseWorkingHours(value).toMutableStateList() }

    fun updateAndSave() {
        onValueChange(formatWorkingHours(days))
    }

    Column(modifier.fillMaxWidth()) {
        days.forEachIndexed { index, dayHours ->
            WorkingHoursRow(
                dayHours = dayHours,
                onToggle = { enabled ->
                    days[index] = dayHours.copy(enabled = enabled)
                    updateAndSave()
                },
                onOpenChange = { h, m ->
                    days[index] = dayHours.copy(openHour = h, openMinute = m)
                    updateAndSave()
                },
                onCloseChange = { h, m ->
                    days[index] = dayHours.copy(closeHour = h, closeMinute = cm)
                    updateAndSave()
                }
            )
        }
    }
}

@Composable
private fun WorkingHoursRow(
    dayHours: DayHours,
    onToggle: (Boolean) -> Unit,
    onOpenChange: (Int, Int) -> Unit,
    onCloseChange: (Int, Int) -> Unit
) {
    var showOpenPicker by remember { mutableStateOf(false) }
    var showClosePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = dayHours.enabled,
            onCheckedChange = { onToggle(it) },
            colors = CheckboxDefaults.colors(checkedColor = Green40)
        )
        Text(
            text = dayHours.day,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(90.dp),
            color = if (dayHours.enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        if (dayHours.enabled) {
            TimeChip(
                label = String.format("%02d:%02d", dayHours.openHour, dayHours.openMinute),
                onClick = { showOpenPicker = true }
            )
            Text(" - ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Green40)
            TimeChip(
                label = String.format("%02d:%02d", dayHours.closeHour, dayHours.closeMinute),
                onClick = { showClosePicker = true }
            )
        } else {
            Text(
                text = "Cerrado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    if (showOpenPicker) {
        TimePickerDialog(
            initialHour = dayHours.openHour,
            initialMinute = dayHours.openMinute,
            title = "Hora de apertura",
            onConfirm = { h, m -> onOpenChange(h, m); showOpenPicker = false },
            onDismiss = { showOpenPicker = false }
        )
    }
    if (showClosePicker) {
        TimePickerDialog(
            initialHour = dayHours.closeHour,
            initialMinute = dayHours.closeMinute,
            title = "Hora de cierre",
            onConfirm = { h, m -> onCloseChange(h, m); showClosePicker = false },
            onDismiss = { showClosePicker = false }
        )
    }
}

@Composable
private fun TimeChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = Teal40.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Teal40,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    title: String,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    selectorColor = Green40,
                    containerColor = MaterialTheme.colorScheme.surface,
                    selectedWidgetBackgroundColor = Green40.copy(alpha = 0.15f)
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("OK", color = Green40, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun QuickWorkingHoursPresets(
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(
        "Lun-Vie 08:00-18:00" to "Lunes a Viernes",
        "Lun-Vie 09:00-17:00" to "Lunes a Viernes (9-5)",
        "Lun-Sáb 08:00-14:00" to "Lunes a Sábado (8-2)",
        "Mar-Dom 10:00-20:00" to "Martes a Domingo (10-8)"
    )
    Column(modifier.fillMaxWidth()) {
        Text(
            "Atajos:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.take(2).forEach { (hours, label) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(hours) },
                    color = Green40.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = hours,
                        style = MaterialTheme.typography.labelSmall,
                        color = Green40,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.drop(2).forEach { (hours, label) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(hours) },
                    color = Teal40.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = hours,
                        style = MaterialTheme.typography.labelSmall,
                        color = Teal40,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
