package com.technikh.employeeattendancetracking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

import com.technikh.employeeattendancetracking.data.database.daos.AttendanceDao
import com.technikh.employeeattendancetracking.data.database.daos.WorkReasonDao
import com.technikh.employeeattendancetracking.data.database.daos.EmployeeDao
import com.technikh.employeeattendancetracking.data.database.entities.AttendanceRecord
import com.technikh.employeeattendancetracking.data.database.entities.DailyAttendance
import com.technikh.employeeattendancetracking.data.database.entities.DayOfficeHours
import com.technikh.employeeattendancetracking.data.database.entities.OfficeWorkReason
import com.technikh.employeeattendancetracking.data.database.entities.Employee

class AttendanceViewModelV2(
    private val attendanceDao: AttendanceDao,
    private val workReasonDao: WorkReasonDao,
    private val employeeDao: EmployeeDao
) : ViewModel() {



    // Live Timeline (Bottom half of Home Screen)
    val todayTimeline = attendanceDao.getTodayAttendance(getStartOfDay())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Employee List
    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    val employees = _employees.asStateFlow()

    // Current Status (For Punch Button Color)
    private val _isPunchedIn = MutableStateFlow(false)
    val isPunchedIn = _isPunchedIn.asStateFlow()

    private val _currentEmployeeName = MutableStateFlow("")
    val currentEmployeeName = _currentEmployeeName.asStateFlow()



    // Selected Date for filtering (Defaults to Today)
    private val _selectedMonth = MutableStateFlow(Calendar.getInstance())

    // Formatted Month Text (e.g. "December 2025") for UI
    val currentMonthText = _selectedMonth.map { cal ->
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")


    private val _allRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())

    // Filtered Daily Reports (Reacts to _selectedMonth changes)
    val dailyReports = combine(_allRecords, _selectedMonth) { records, cal ->
        val selectedMonthStr = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)


        val filtered = records.filter {
            val recordMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(it.timestamp))
            recordMonth == selectedMonthStr
        }

        // Group by Date
        filtered.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
        }.map { (date, dailyRecs) -> DailyAttendance(date, dailyRecs) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _monthlyReport = MutableStateFlow<List<DayOfficeHours>>(emptyList())
    val monthlyReport = _monthlyReport.asStateFlow()

    private var activeEmployeeId: String? = null

    private val _workReasonSuggestions = MutableStateFlow<List<String>>(emptyList())
    val workReasonSuggestions = _workReasonSuggestions.asStateFlow()

    init {
        viewModelScope.launch {
            employeeDao.getAllEmployees().collect { list -> _employees.value = list }
        }
    }




    fun registerEmployee(name: String, id: String, password: String) {
        viewModelScope.launch {
            val newEmployee = Employee(name = name, employeeId = id, password = password)
            employeeDao.insertEmployee(newEmployee)
        }
    }


    fun getLiveStatus(employeeId: String): Flow<AttendanceRecord?> {
        return attendanceDao.getLastRecordFlow(employeeId)
    }


    fun loadDashboardData(employeeId: String) {
        viewModelScope.launch {
            activeEmployeeId = employeeId
            val emp = employeeDao.getEmployeeById(employeeId)
            _currentEmployeeName.value = emp?.name ?: "Unknown"


            val lastRecord = attendanceDao.getLastRecord(employeeId)
            _isPunchedIn.value = lastRecord?.punchType == "IN"


            val all = attendanceDao.getAttendanceByEmployee(employeeId)
            _allRecords.value = all


            refreshMonthlyChart()
        }
    }


    fun changeMonth(monthsToAdd: Int) {
        val current = _selectedMonth.value.clone() as Calendar
        current.add(Calendar.MONTH, monthsToAdd)
        _selectedMonth.value = current
        refreshMonthlyChart()
    }

    fun setDate(timestamp: Long) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        _selectedMonth.value = cal
        refreshMonthlyChart()
    }

    private fun refreshMonthlyChart() {
        activeEmployeeId?.let { id ->
            viewModelScope.launch {
                val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                val monthStr = format.format(_selectedMonth.value.time)
                _monthlyReport.value = attendanceDao.getMonthlyOfficeHours(id, monthStr)
            }
        }
    }


    fun punchIn(employeeId: String, selfiePath: String?) {
        viewModelScope.launch {
            val record = AttendanceRecord(
                employeeId = employeeId,
                punchType = "IN",
                timestamp = System.currentTimeMillis(),
                selfiePath = selfiePath
            )
            attendanceDao.insert(record)
            _isPunchedIn.value = true
            loadDashboardData(employeeId)
        }
    }

    fun punchOut(employeeId: String, reason: String, isOfficeWork: Boolean, workReason: String?, selfiePath: String?) {
        viewModelScope.launch {
            attendanceDao.insert(AttendanceRecord(
                employeeId = employeeId,
                punchType = "OUT",
                timestamp = System.currentTimeMillis(),
                reason = reason,
                isOfficeWork = isOfficeWork,
                workReason = workReason,
                selfiePath = selfiePath
            ))

            if (isOfficeWork && !workReason.isNullOrBlank()) {
                saveNewReason(workReason)
            }

            _isPunchedIn.value = false
            loadDashboardData(employeeId)
        }
    }

    fun searchReasons(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _workReasonSuggestions.value = emptyList()
            } else {
                val reasons = workReasonDao.searchReasons("%$query%")
                _workReasonSuggestions.value = reasons.map { it.reason }
            }
        }
    }

    private suspend fun saveNewReason(reasonText: String) {
        val existing = workReasonDao.searchReasons(reasonText)
        if (existing.isEmpty()) {
            workReasonDao.insert(OfficeWorkReason(reason = reasonText, usageCount = 1))
        } else {
            workReasonDao.incrementUsage(reasonText, System.currentTimeMillis())
        }
    }

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    class Factory(
        private val attendanceDao: AttendanceDao,
        private val workReasonDao: WorkReasonDao,
        private val employeeDao: EmployeeDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AttendanceViewModelV2::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AttendanceViewModelV2(attendanceDao, workReasonDao, employeeDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}