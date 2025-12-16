import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmployeeDao {
    @Insert
    suspend fun insert(employee: Employee)

    @Query("SELECT * FROM employees WHERE employeeId = :employeeId")
    suspend fun getEmployee(employeeId: String): Employee?

    @Query("UPDATE employees SET fingerprintRegistered = :registered WHERE employeeId = :employeeId")
    suspend fun updateFingerprintStatus(employeeId: String, registered: Boolean)

    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<Employee>
}