// ReportsDashboard.kt
@Composable
fun ReportsDashboard(
    employeeId: String,
    viewModel: AttendanceViewModel = viewModel()
) {
    val dailyReports by viewModel.dailyReports.collectAsState()
    val monthlyReport by viewModel.monthlyReport.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tabs for Daily/Monthly
        var selectedTab by remember { mutableIntStateOf(0) }

        TabRow(selectedTabIndex = selectedTab) {
            listOf("Daily Report", "Monthly Report").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> DailyReportView(dailyReports)
            1 -> MonthlyReportView(monthlyReport)
        }
    }
}

@Composable
fun DailyReportView(reports: List<DailyAttendance>) {
    LazyColumn {
        items(reports) { daily ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = daily.date,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    daily.records.forEach { record ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = SimpleDateFormat("HH:mm").format(Date(record.timestamp)),
                                color = if (record.punchType == "IN") Color.Green else Color.Red
                            )
                            Text(record.punchType)
                            if (record.reason != null) {
                                Text(record.reason, fontStyle = FontStyle.Italic)
                            }
                        }
                    }

                    // Total hours calculation
                    val totalHours = calculateDailyHours(daily.records)
                    if (totalHours > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Total: ${"%.2f".format(totalHours)} hours",
                            fontWeight = FontWeight.Bold,
                            color = Color.Blue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyReportView(monthlyData: List<DayOfficeHours>) {
    // Chart using Vico library
    val chartEntryModel = remember(monthlyData) {
        chartEntryModelOf(
            monthlyData.mapIndexed { index, dayData ->
                entryOf(index.toFloat(), dayData.officeHours.toFloat())
            }
        )
    }

    Column {
        // Chart
        Chart(
            chart = columnChart(),
            chartModel = chartEntryModel,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Table view
        LazyColumn {
            items(monthlyData) { dayData ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(dayData.day)
                    Text("${"%.2f".format(dayData.officeHours)} hours")
                }
                Divider()
            }
        }
    }
}