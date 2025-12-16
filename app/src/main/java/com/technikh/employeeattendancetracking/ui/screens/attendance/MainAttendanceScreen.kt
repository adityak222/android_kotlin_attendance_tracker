
// MainAttendanceScreen.kt
@Composable
fun MainAttendanceScreen(
    employeeId: String,
    viewModel: AttendanceViewModel = viewModel()
) {
    val context = LocalContext.current
    val biometricPrompt = rememberBiometricPrompt(
        onSuccess = { viewModel.onBiometricSuccess(employeeId) },
        onError = { errorCode, errString ->
            // Handle error
        }
    )

    val currentRecord by viewModel.currentAttendanceRecord.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Employee Info
        EmployeeInfoCard(employeeId = employeeId)

        Spacer(modifier = Modifier.height(16.dp))

        // Punch Button
        Button(
            onClick = {
                if (viewModel.checkIfLastWasPunchIn(employeeId)) {
                    // Show punch out with reason dialog
                    viewModel.showPunchOutDialog()
                } else {
                    // Start biometric for punch in
                    biometricPrompt.authenticate(
                        BiometricPrompt.PromptInfo.Builder()
                            .setTitle("Punch In")
                            .setSubtitle("Authenticate to punch in")
                            .setNegativeButtonText("Cancel")
                            .build()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (viewModel.checkIfLastWasPunchIn(employeeId))
                    Color.Red else Color.Green
            )
        ) {
            Text(
                text = if (viewModel.checkIfLastWasPunchIn(employeeId))
                    "PUNCH OUT" else "PUNCH IN",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Today's Records
        TodayRecordsSection(employeeId = employeeId)

        // Reports Button
        Button(
            onClick = { /* Navigate to reports */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Reports")
        }
    }

    // Punch Out Dialog
    if (viewModel.showPunchOutDialog) {
        PunchOutReasonDialog(
            onDismiss = { viewModel.showPunchOutDialog = false },
            onConfirm = { reason, isOfficeWork, workReason ->
                viewModel.punchOut(employeeId, reason, isOfficeWork, workReason)
            }
        )
    }
}

@Composable
fun PunchOutReasonDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Boolean, String?) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Personal") }
    var isOfficeWork by remember { mutableStateOf(false) }
    var officeWorkReason by remember { mutableStateOf("") }
    val viewModel: AttendanceViewModel = viewModel()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Punch Out Reason") },
        text = {
            Column {
                // Reason Selection
                Text("Select Reason:", fontWeight = FontWeight.Bold)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listOf("Personal", "Office Work").forEach { reason ->
                        DropdownMenuItem(
                            text = { Text(reason) },
                            onClick = {
                                selectedReason = reason
                                isOfficeWork = reason == "Office Work"
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Office Work Reason Input
                if (isOfficeWork) {
                    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
                    var showSuggestions by remember { mutableStateOf(false) }

                    Text("Office Work Reason:")

                    LaunchedEffect(officeWorkReason) {
                        if (officeWorkReason.length >= 2) {
                            suggestions = viewModel.searchWorkReasons(officeWorkReason)
                            showSuggestions = true
                        }
                    }

                    OutlinedTextField(
                        value = officeWorkReason,
                        onValueChange = {
                            officeWorkReason = it
                            showSuggestions = it.isNotEmpty()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter reason") }
                    )

                    // Suggestions Dropdown
                    if (showSuggestions && suggestions.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
                                suggestions.forEach { suggestion ->
                                    Text(
                                        text = suggestion,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                officeWorkReason = suggestion
                                                showSuggestions = false
                                            }
                                            .padding(12.dp)
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedReason, isOfficeWork,
                        if (isOfficeWork) officeWorkReason else null)
                    onDismiss()
                }
            ) {
                Text("Confirm Punch Out")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}