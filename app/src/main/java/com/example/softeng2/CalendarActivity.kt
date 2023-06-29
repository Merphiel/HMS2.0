package com.example.softeng2

import android.os.Build
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CalendarActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
        val now = LocalDate.now()
        val table = findViewById<TableLayout>(R.id.Tablelay)

        // Creating the headers for each day
        val headerRow = TableRow(this)
        for (i in 0 until 7) {
            val day = now.plus(i.toLong(), ChronoUnit.DAYS)
            val textView = TextView(this).apply {
                text = "${day.dayOfWeek.name.take(3)}\n${day.format(dateFormatter)}"
                setPadding(16, 16, 16, 16)
            }
            headerRow.addView(textView)
        }
        table.addView(headerRow)

        // Creating rows for each time slot
        val timeSlots = listOf("9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")
        for (timeSlot in timeSlots) {
            val row = TableRow(this)
            for (i in 0 until 7) {
                val textView = TextView(this).apply {
                    text = if (i == 0) timeSlot else ""
                    setPadding(16, 16, 16, 16)
                }
                row.addView(textView)
            }
            table.addView(row)
        }
    }
}