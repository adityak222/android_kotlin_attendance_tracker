// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(this)

        setContent {
            AttendanceAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigation setup
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") { /* Login screen */ }
                        composable("attendance/{employeeId}") { backStackEntry ->
                            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                            MainAttendanceScreen(employeeId = employeeId)
                        }
                        composable("reports/{employeeId}") { backStackEntry ->
                            val employeeId = backStackEntry.arguments?.getString("employeeId") ?: ""
                            ReportsDashboard(employeeId = employeeId)
                        }
                    }
                }
            }
        }
    }
}