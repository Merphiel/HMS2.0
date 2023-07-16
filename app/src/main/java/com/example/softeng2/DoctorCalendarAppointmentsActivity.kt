package com.example.softeng2

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
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class DoctorCalendarAppointmentsActivity: AppCompatActivity() {
    lateinit var curDateBasis:LocalDate;
    lateinit var uid:String
    lateinit var duid:String
    lateinit var map:HashMap<Pair<Int,Int>,String>
    private var count=0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        map=HashMap<Pair<Int,Int>,String>()
        val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
        duid= intent.getStringExtra("DUID")?:""
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
        Log.d("aaasda",intent.getStringExtra("DUID").toString())
        val daysOfWeek = mutableListOf("Time", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val timeSlots = List(20) { 8 * 60 + it * 30 }
        val timeSlotsStr = arrayOf(
            "8:00", "8:30", "9:00", "9:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30",
            "14:00", "14:30", "15:00", "15:30", "16:00", "16:30",
            "17:00", "17:30"
        )
        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        curDateBasis = LocalDate.now()
        val now=curDateBasis
        val nowFmt=dateFormatter.format(now)
        val max = now.plus(7,ChronoUnit.DAYS)
        val nowts: Timestamp = Timestamp(LocalDate.parse(LocalDate.of(now.year,now.monthValue,now.dayOfMonth)
            .toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toEpochSecond(
            ZoneOffset.UTC),0)
        val maxts: Timestamp = Timestamp(LocalDate.parse(LocalDate.of(max.year,max.monthValue,max.dayOfMonth)
            .toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toEpochSecond(
            ZoneOffset.UTC),0)
        db.collection("schedules")
            .whereGreaterThanOrEqualTo("Date",nowts)
            .whereLessThanOrEqualTo("Date",maxts)
            .whereEqualTo("DUID",intent.getStringExtra("DUID").toString())
            .orderBy("Date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("aasdac",querySnapshot.size().toString())
                count = querySnapshot.size()
                for (document in querySnapshot.documents) {
                    Log.d("testCompar1","huh")
                    val test= (document.data!!.get("Date") as Timestamp).toDate().toInstant().atZone(ZoneOffset.UTC).toLocalDate()
                    Log.d("testCompar",ChronoUnit.DAYS.between(curDateBasis,test).toString())

                    map.put(Pair(timeSlotsStr.indexOf(document.data!!.get("Time").toString())+2,ChronoUnit.DAYS.between(curDateBasis,test).toInt()+1),document.id)
                    Log.d("testasd",map.toString())
                    Log.d("testasd",(document.data!!.get("Time")).toString())
                }



                for (row in 0 until gridLayout.rowCount) {
                    for (col in 0 until gridLayout.columnCount) {
                        val textView = TextView(this).apply {
                            background = GradientDrawable().apply {
                                shape = GradientDrawable.RECTANGLE
                                setStroke(2, Color.BLACK) // Set border thickness and color
                            }
                            var res=0;

                            Log.d("varset",row.toString()+col.toString())
                            Log.d("varset",map.toString())
                            Log.d("varset",Pair(row,col).toString())

                            if (map.containsKey(Pair(row,col))) {
                                val res= map.get(Pair(row,col))
                                setBackgroundColor(Color.GREEN)
                                setOnClickListener{
                                    db.collection("schedules").document(res!!).get().addOnSuccessListener{
                                        docData->

                                        val intent = Intent(this@DoctorCalendarAppointmentsActivity, DoctorCheckScheduleActivity::class.java)
                                        intent.putExtra("SID", res)
                                        val dateFormat = SimpleDateFormat("dd MMMM yyyy")
                                        intent.putExtra("PUID", docData.data!!.get("PUID").toString());
                                        intent.putExtra("DUID", docData.data!!.get("DUID").toString());
                                        intent.putExtra("Date", dateFormat.format((docData.data!!.get("Date") as Timestamp).toDate()))
                                        intent.putExtra("Time", docData.data!!.get("Time").toString())
                                        startActivity(intent);
                                    }
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
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
        //curDateBasis= LocalDate


    }

}