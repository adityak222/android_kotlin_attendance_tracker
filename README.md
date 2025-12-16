attendance-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/attendanceapp/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   │   ├── RegistrationScreen.kt
│   │   │   │   │   │   │   └── FingerprintRegistrationScreen.kt
│   │   │   │   │   │   ├── attendance/
│   │   │   │   │   │   │   ├── MainAttendanceScreen.kt
│   │   │   │   │   │   │   ├── PunchOutReasonDialog.kt
│   │   │   │   │   │   │   ├── EmployeeInfoCard.kt
│   │   │   │   │   │   │   └── TodayRecordsSection.kt
│   │   │   │   │   │   ├── reports/
│   │   │   │   │   │   │   ├── ReportsDashboard.kt
│   │   │   │   │   │   │   ├── DailyReportView.kt
│   │   │   │   │   │   │   ├── MonthlyReportView.kt
│   │   │   │   │   │   │   └── charts/
│   │   │   │   │   │   │       ├── AttendanceChart.kt
│   │   │   │   │   │   │       └── OfficeHoursChart.kt
│   │   │   │   │   │   ├── admin/
│   │   │   │   │   │   │   ├── AdminDashboard.kt
│   │   │   │   │   │   │   ├── EmployeeListScreen.kt
│   │   │   │   │   │   │   └── AdminReportsScreen.kt
│   │   │   │   │   │   └── settings/
│   │   │   │   │   │       └── SettingsScreen.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── common/
│   │   │   │   │   │   │   ├── LoadingIndicator.kt
│   │   │   │   │   │   │   ├── ErrorDialog.kt
│   │   │   │   │   │   │   ├── ConfirmationDialog.kt
│   │   │   │   │   │   │   └── DatePickerDialog.kt
│   │   │   │   │   │   ├── cards/
│   │   │   │   │   │   │   ├── AttendanceCard.kt
│   │   │   │   │   │   │   ├── ReportCard.kt
│   │   │   │   │   │   │   └── EmployeeCard.kt
│   │   │   │   │   │   └── inputs/
│   │   │   │   │   │       ├── AutoCompleteTextField.kt
│   │   │   │   │   │       ├── ReasonDropdown.kt
│   │   │   │   │   │       └── TimeInputField.kt
│   │   │   │   │   └── navigation/
│   │   │   │   │       ├── NavGraph.kt
│   │   │   │   │       ├── Screens.kt
│   │   │   │   │       └── NavigationViewModel.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── daos/
│   │   │   │   │   │   │   ├── EmployeeDao.kt
│   │   │   │   │   │   │   ├── AttendanceDao.kt
│   │   │   │   │   │   │   └── WorkReasonDao.kt
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── Employee.kt
│   │   │   │   │   │   │   ├── AttendanceRecord.kt
│   │   │   │   │   │   │   └── OfficeWorkReason.kt
│   │   │   │   │   │   └── converters/
│   │   │   │   │   │       └── Converters.kt
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   ├── AttendanceRepository.kt
│   │   │   │   │   │   ├── EmployeeRepository.kt
│   │   │   │   │   │   └── WorkReasonRepository.kt
│   │   │   │   │   ├── datastore/
│   │   │   │   │   │   ├── AppPreferences.kt
│   │   │   │   │   │   └── DataStoreManager.kt
│   │   │   │   │   └── models/
│   │   │   │   │       ├── DayOfficeHours.kt
│   │   │   │   │       ├── DailyAttendance.kt
│   │   │   │   │       ├── MonthlyReport.kt
│   │   │   │   │       └── AttendanceSummary.kt
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── AttendanceViewModel.kt
│   │   │   │   │   ├── EmployeeViewModel.kt
│   │   │   │   │   ├── ReportsViewModel.kt
│   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   └── SharedViewModel.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── AttendanceUtils.kt
│   │   │   │   │   ├── BiometricHelper.kt
│   │   │   │   │   ├── TimeCalculator.kt
│   │   │   │   │   ├── Validators.kt
│   │   │   │   │   ├── Constants.kt
│   │   │   │   │   └── extensions/
│   │   │   │   │       ├── StringExtensions.kt
│   │   │   │   │       ├── DateExtensions.kt
│   │   │   │   │       └── ViewExtensions.kt
│   │   │   │   └── theme/
│   │   │   │       ├── Theme.kt
│   │   │   │       ├── Color.kt
│   │   │   │       ├── Type.kt
│   │   │   │       └── ThemeViewModel.kt
│   │   │   ├── resources/
│   │   │   │   ├── res/
│   │   │   │   │   ├── values/
│   │   │   │   │   │   ├── colors.xml
│   │   │   │   │   │   ├── strings.xml
│   │   │   │   │   │   ├── themes.xml
│   │   │   │   │   │   └── dimens.xml
│   │   │   │   │   ├── values-night/
│   │   │   │   │   │   └── themes.xml
│   │   │   │   │   ├── drawable/
│   │   │   │   │   │   ├── ic_fingerprint.xml
│   │   │   │   │   │   ├── ic_attendance.xml
│   │   │   │   │   │   ├── ic_reports.xml
│   │   │   │   │   │   ├── ic_settings.xml
│   │   │   │   │   │   ├── bg_card.xml
│   │   │   │   │   │   └── ripple_effect.xml
│   │   │   │   │   ├── mipmap/
│   │   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   │   └── layout/
│   │   │   │   │       └── activity_main.xml (optional - if not using full Compose)
│   │   │   │   └── AndroidManifest.xml
│   │   │   ├── test/ (Unit tests)
│   │   │   │   └── java/com/example/attendanceapp/
│   │   │   │       ├── utils/
│   │   │   │       │   └── AttendanceUtilsTest.kt
│   │   │   │       ├── repository/
│   │   │   │       │   └── AttendanceRepositoryTest.kt
│   │   │   │       └── viewmodel/
│   │   │   │           └── AttendanceViewModelTest.kt
│   │   │   └── androidTest/ (Instrumentation tests)
│   │   │       └── java/com/example/attendanceapp/
│   │   │           └── ui/
│   │   │               └── AttendanceScreenTest.kt
│   │   └── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md