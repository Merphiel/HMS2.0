package com.example.softeng2

import com.example.softeng2.R
import com.example.softeng2.ScheduleActivity



import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Time
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PatientCalendarAppointmentsActivity : AppCompatActivity() {
    lateinit var curDateBasis:LocalDate;
    lateinit var uid:String
    lateinit var duid:String
    lateinit var map:HashMap<Pair<Int,Int>,Array<String>>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        map=HashMap<Pair<Int,Int>,Array<String>>()
        uid = intent.getStringExtra("PUID")?:""
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val rows = 22
        val columns = 8
        val array = Array(rows) { Array(columns) { 0 } }

        val gridLayout = GridLayout(this).apply {
            columnCount = 8
            rowCount = 22
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        }
        //curDateBasis= LocalDate
        val timeSlots = List(20) { 8 * 60 + it * 30 }

        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
        curDateBasis = LocalDate.now()
        val now=curDateBasis
        val endDate = curDateBasis.plusDays(6)
        val db = FirebaseFirestore.getInstance()
        Log.d("aasd", Timestamp(curDateBasis.atStartOfDay().toEpochSecond(ZoneOffset.UTC),0).toString())
        Log.d("aasd",Timestamp(endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC),0).toString())
        Log.d("aasd",uid)
        val collectionReference = db.collection("schedules" )
        collectionReference.
        whereEqualTo("PUID",uid).
//            whereGreaterThanOrEqualTo("Time",
//            Timestamp(curDateBasis.atStartOfDay().toEpochSecond(ZoneOffset.UTC),0)).
//        whereLessThanOrEqualTo("Time",
//            Timestamp(endDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC),0)).
        get().addOnSuccessListener { querySnapshot ->
            for (documentSnapshot in querySnapshot) {
                Log.d("aaasd","Found valid query")
                val documentData = documentSnapshot.data
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                val timeData = LocalTime.parse(documentData.get("Time").toString(), formatter)
                val date = LocalDate.parse(documentData.get("Date").toString())
               // val localDate = datg .toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
               // val formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val type = if (documentData.get("PUID")==="CANCELED") 2 else 1
                map.put(Pair(timeSlots.indexOf((timeData.toSecondOfDay()/60))+2,ChronoUnit.DAYS.between(curDateBasis,date).toInt()+1),arrayOf(type.toString(),documentData.get("DUID").toString()))
                 }
            }
            .addOnFailureListener { exception ->
                Log.e("ERROR", "Error getting document at schedule ", exception)
            }

        for (row in 0 until gridLayout.rowCount) {
            for (col in 0 until gridLayout.columnCount) {
                val textView = TextView(this).apply {
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setStroke(2, Color.BLACK) // Set border thickness and color
                    }
                    if (map.containsKey(Pair(row,col))) {
                        val res= map.get(Pair(row,col))
                        if(res?.get(0) ?:0==1) {
                            setBackgroundColor(Color.GREEN)
                        }else if (res?.get(0) ?:0 ==2) {
                            setBackgroundColor(Color.RED)
                        }
                    }
                    gravity = Gravity.CENTER
                    id = View.generateViewId()
                    array[row][col]=id;
                    if (row == 0) {
                        if(col==0) {
                            text="Time"
                        } else {
                            val day=now.plus((col-1).toLong(), ChronoUnit.DAYS)
                            text = "${day.dayOfWeek.name.take(3)}"
                        }
                    }  else if(row==1) {
                        if(col==0) {

                        } else {
                            val day=now.plus((col-1).toLong(), ChronoUnit.DAYS)
                            text="${day.format(dateFormatter)}"
                        }
                    } else if (col == 0) {
                        text = "${timeSlots[row - 2] / 60}:${if (timeSlots[row - 2] % 60 == 0) "00" else "30"}"
                    }else{
                        val data= HashMap<String, Any>()
                        data["col"]=col
                        data["row"]=row
                        data["date"]= LocalDate.of(now.year, now.monthValue, now.dayOfMonth)
                        tag=data
                        setOnClickListener {
                            if (map.containsKey(Pair(row,col))) {
                                val res= map.get(Pair(row,col))

                                if (res?.get(0) ?:0 ==2) {
                                    Toast.makeText(applicationContext, "This Appointment has been canceled.", Toast.LENGTH_SHORT).show()
                                } else if (res?.get(0) ?:0 ==1) {
                                    val retrievedVal = tag as? HashMap<String, Any>
                                    retrievedVal?.let {
                                        val col= it["col"]?:0
                                        val row=it["row"]?:0
                                        val date=it["date"] as LocalDate
                                        val time="${timeSlots[row.toString().toInt()] / 60}:${if (timeSlots[row.toString().toInt()] % 60 == 0) "00" else "30"}"
                                        Log.d("asdcd",uid+" " + duid + date + time)
                                        val intent = Intent(this@PatientCalendarAppointmentsActivity, ScheduleActivity::class.java)
                                        intent.putExtra("PUID", uid);
                                        intent.putExtra("DUID", duid);
                                        intent.putExtra("Date", date.toString());
                                        intent.putExtra("Time", time)
                                        startActivity(intent);
                                    }
                                }
                            }else {
                                Toast.makeText(applicationContext, "No appointment has been scheduled for this time.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(col, 1f)
                    }
                    textSize = 16f  // Change text size if necessary
                    setPadding(5, 20, 5, 20)  // Add padding if you want extra space around the text
                }
                gridLayout.addView(textView)
            }
        }
        val myLayout = findViewById<LinearLayout>(R.id.calLayout)
        myLayout.addView(gridLayout)
    }

}