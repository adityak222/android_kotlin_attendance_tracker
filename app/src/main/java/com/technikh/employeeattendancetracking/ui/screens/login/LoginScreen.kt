package com.technikh.employeeattendancetracking.ui.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.technikh.employeeattendancetracking.data.database.AppDatabase
import com.technikh.employeeattendancetracking.utils.SettingsManager
import com.technikh.employeeattendancetracking.viewmodel.AttendanceViewModelV2
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToSettings: () -> Unit
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

    val settingsManager = remember { SettingsManager(context) }


    val employees by viewModel.employees.collectAsState()
    val timeline by viewModel.todayTimeline.collectAsState()


    val maxEmployees = settingsManager.maxHomeEmployees
    val displayedEmployees = employees.take(maxEmployees)


    var showAuthDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Home") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAuthDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Employee")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {


            Text(
                "Select Employee",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (displayedEmployees.isEmpty()) {
                    item {
                        Text("No employees found. Click + to add.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                }

                items(displayedEmployees) { employee ->
                    Card(
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth().clickable { onLoginSuccess(employee.employeeId) }
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(employee.name, fontWeight = FontWeight.Bold)
                                Text("ID: ${employee.employeeId}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Divider(thickness = 2.dp, color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))


            Text(
                "Today's Activity",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (timeline.isEmpty()) {
                    item { Text("No punches today.", modifier = Modifier.padding(8.dp), color = Color.Gray) }
                }

                items(timeline) { record ->
                    val empName = employees.find { it.employeeId == record.employeeId }?.name ?: "Unknown"
                    val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(record.timestamp))
                    val color = if (record.punchType == "IN") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = color),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(empName, fontWeight = FontWeight.Bold)
                                Text("ID: ${record.employeeId}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = record.punchType,
                                    fontWeight = FontWeight.Bold,
                                    color = if(record.punchType == "IN") Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Text(time, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }


        if (showAuthDialog) {
            AdminPassDialog(
                correctPassword = settingsManager.adminPassword,
                onDismiss = { showAuthDialog = false },
                onSuccess = { onNavigateToRegister() }
            )
        }
    }
}

@Composable
fun AdminPassDialog(correctPassword: String, onDismiss: () -> Unit, onSuccess: () -> Unit) {
    var input by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Admin Authorization") },
        text = {
            Column {
                OutlinedTextField(
                    value = input, onValueChange = { input = it; isError = false },
                    label = { Text("Admin Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = isError
                )
                if (isError) Text("Incorrect Password", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(onClick = {
                if (input == correctPassword) {
                    onSuccess()
                    onDismiss()
                } else isError = true
            }) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}