import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// AttendanceViewModel.kt
class AttendanceViewModel(
    private val attendanceDao: AttendanceDao,
    private val workReasonDao: WorkReasonDao
) : ViewModel() {

    var showPunchOutDialog by mutableStateOf(false)
        private set

    val dailyReports = attendanceDao.getDailyAttendance(employeeId)
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun onBiometricSuccess(employeeId: String) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                employeeId = employeeId,
                punchType = "IN",
                timestamp = System.currentTimeMillis()
            )
            attendanceDao.insert(record)
        }
    }

    fun punchOut(employeeId: String, reason: String, isOfficeWork: Boolean, workReason: String?) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                employeeId = employeeId,
                punchType = "OUT",
                timestamp = System.currentTimeMillis(),
                reason = reason,
                isOfficeWork = isOfficeWork,
                workReason = workReason
            )
            attendanceDao.insert(record)

            // If office work and new reason, save it
            if (isOfficeWork && workReason != null) {
                val existing = workReasonDao.searchReasons("%$workReason%")
                if (existing.isEmpty()) {
                    workReasonDao.insert(
                        OfficeWorkReason(
                            reason = workReason,
                            usageCount = 1,
                            lastUsed = System.currentTimeMillis()
                        )
                    )
                } else {
                    workReasonDao.incrementUsage(workReason, System.currentTimeMillis())
                }
            }
        }
    }

    suspend fun checkIfLastWasPunchIn(employeeId: String): Boolean {
        return withContext(Dispatchers.IO) {
            val lastRecord = attendanceDao.getAttendanceByEmployee(employeeId).firstOrNull()
            lastRecord?.punchType == "IN"
        }
    }

    fun searchWorkReasons(query: String): List<String> {
        return runBlocking {
            workReasonDao.searchReasons("%$query%").map { it.reason }
        }
    }
}