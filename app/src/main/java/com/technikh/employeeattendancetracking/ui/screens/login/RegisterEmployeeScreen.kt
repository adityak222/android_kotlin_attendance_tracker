package com.technikh.employeeattendancetracking.ui.screens.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.technikh.employeeattendancetracking.data.database.AppDatabase
import com.technikh.employeeattendancetracking.viewmodel.AttendanceViewModelV2

@Composable
fun RegisterEmployeeScreen(
    onRegistered: () -> Unit
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)

    val viewModel: AttendanceViewModelV2 = viewModel(
        factory = AttendanceViewModelV2.Factory(
            database.attendanceDao(),
            database.workReasonDao(),
            database.employeeDao()
        )
    )

    BackHandler {
        onRegistered()
    }

    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(32.dp)) {
            Text("Register New Employee", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(24.dp))


            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                placeholder = { Text("e.g. Aditya") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))


            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("Employee ID") },
                placeholder = { Text("e.g. EMP001") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))


            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Set User Password") },
                placeholder = { Text("e.g. 1234") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(), // Hides text
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    // Check if password is also filled
                    if (name.isNotBlank() && id.isNotBlank() && password.isNotBlank()) {
                        viewModel.registerEmployee(name, id, password)
                        onRegistered()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Employee")
            }
        }

        FloatingActionButton(
            onClick = onRegistered,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}