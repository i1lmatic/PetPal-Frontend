package com.petpal.app.ui.screens.auth

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    isLoading: Boolean,
    error: String?,
    onRegister: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onClearError: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Pets,
            contentDescription = null,
            modifier = Modifier.size(56.dp).align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; onClearError() },
            label = { Text("Nombre completo") },
            leadingIcon = { Icon(Icons.Filled.Person, "name") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; onClearError() },
            label = { Text("Tel\u00e9fono") },
            leadingIcon = { Icon(Icons.Filled.Phone, "phone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; onClearError() },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, "email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; onClearError() },
            label = { Text("Contrase\u00f1a") },
            leadingIcon = { Icon(Icons.Filled.Lock, "password") },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, "toggle")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; onClearError() },
            label = { Text("Confirmar contrase\u00f1a") },
            leadingIcon = { Icon(Icons.Filled.Lock, "confirm") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            isError = confirmPassword.isNotEmpty() && password != confirmPassword,
            supportingText = if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                { Text("Las contrase\u00f1as no coinciden") }
            } else null,
            singleLine = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onRegister(email.trim(), password, fullName.trim(), phone.trim())
            },
            enabled = fullName.isNotBlank() && phone.isNotBlank() && email.isNotBlank()
                    && password.isNotBlank() && password == confirmPassword && !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text("Registrarse", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("\u00bfYa tienes cuenta? Inicia sesi\u00f3n")
        }
    }
}
