import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AttendanceDao {
    @Insert
    suspend fun insert(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId ORDER BY timestamp DESC")
    suspend fun getAttendanceByEmployee(employeeId: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE employeeId = :employeeId AND date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch') ORDER BY timestamp DESC")
    suspend fun getAttendanceByEmployeeAndDate(employeeId: String, date: Long): List<AttendanceRecord>

    @Query("""
        SELECT * FROM attendance_records 
        WHERE employeeId = :employeeId 
        AND strftime('%Y-%m', datetime(timestamp/1000, 'unixepoch')) = :monthYear
        ORDER BY timestamp DESC
    """)
    suspend fun getAttendanceByEmployeeAndMonth(employeeId: String, monthYear: String): List<AttendanceRecord>

    @Query("""
        SELECT 
            date(timestamp/1000, 'unixepoch') as day,
            SUM(CASE WHEN punchType = 'OUT' AND isOfficeWork = 1 THEN 
                (timestamp - (SELECT timestamp FROM attendance_recards r2 
                              WHERE r2.employeeId = r1.employeeId 
                              AND date(r2.timestamp/1000, 'unixepoch') = date(r1.timestamp/1000, 'unixepoch')
                              AND r2.punchType = 'IN' 
                              AND r2.id < r1.id
                              ORDER BY r2.timestamp DESC LIMIT 1)
                ) 
                ELSE 0 END) / (1000 * 60 * 60.0) as officeHours
        FROM attendance_records r1
        WHERE employeeId = :employeeId 
        AND strftime('%Y-%m', datetime(timestamp/1000, 'unixepoch')) = :monthYear
        GROUP BY date(timestamp/1000, 'unixepoch')
    """)
    suspend fun getMonthlyOfficeHours(employeeId: String, monthYear: String): List<DayOfficeHours>
}

data class DayOfficeHours(
    val day: String,
    val officeHours: Double
)