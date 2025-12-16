import androidx.room.Entity
import androidx.room.PrimaryKey

// AttendanceRecord.kt
@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: String,
    val punchType: String, // "IN" or "OUT"
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String? = null,
    val workReason: String? = null,
    val isOfficeWork: Boolean = false
)