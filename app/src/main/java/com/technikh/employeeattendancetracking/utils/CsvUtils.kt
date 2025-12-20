package com.technikh.employeeattendancetracking.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.technikh.employeeattendancetracking.data.database.entities.AttendanceRecord
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvUtils {

    fun generateAndShareCsv(
        context: Context,
        employeeName: String,
        records: List<AttendanceRecord>
    ) {
        try {
            val fileName = "Attendance_${employeeName}_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            val writer = FileWriter(file)

            writer.append("Date,Time,Type,Reason,Is Office Work,Work Reason\n")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            records.forEach { record ->
                val date = dateFormat.format(Date(record.timestamp))
                val time = timeFormat.format(Date(record.timestamp))

                val cleanReason = record.reason?.replace(",", " ") ?: ""
                val cleanWorkReason = record.workReason?.replace(",", " ") ?: ""

                writer.append("$date,$time,${record.punchType},$cleanReason,${record.isOfficeWork},$cleanWorkReason\n")
            }

            writer.flush()
            writer.close()

            shareFile(context, file)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareFile(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Attendance Report")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Report via..."))
    }
}