package com.petpal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "pending" -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            "Pendiente"
        )
        "active" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Activo"
        )
        "confirmed" -> Triple(
            Color(0xFFE3F2FD),
            Color(0xFF1565C0),
            "Confirmada"
        )
        "completed" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            "Completada"
        )
        "cancelled" -> Triple(
            Color(0xFFFFEBEE),
            Color(0xFFC62828),
            "Cancelada"
        )
        "inactive" -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            "Inactivo"
        )
        else -> Triple(
            Color(0xFFF5F5F5),
            Color(0xFF757575),
            status
        )
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
