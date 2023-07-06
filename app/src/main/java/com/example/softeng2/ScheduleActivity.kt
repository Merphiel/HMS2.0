package com.example.softeng2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScheduleActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        val dname:TextView= findViewById(R.id.tv_docname)
        val demail:TextView= findViewById(R.id.tv_docemail)
        val dphone:TextView= findViewById(R.id.tv_docphone)
        val dtype:TextView= findViewById(R.id.tv_doctype)
        val drate:TextView= findViewById(R.id.tv_docrate)
        val ddate:TextView= findViewById(R.id.tv_date)
        val bdemail:ImageButton =findViewById(R.id.btn_demail)
        val bdphone:ImageButton =findViewById(R.id.btn_phone)
        val back:Button =findViewById(R.id.btn_back)
        val sched:Button =findViewById(R.id.btn_Sched)


        back.setOnClickListener() {
            onBackPressed()
        }
        var uid = intent.getStringExtra("PUID")?:""
        var duid= intent.getStringExtra("DUID")?:""
        var time = intent.getStringExtra("Time")?:""
        var date= intent.getStringExtra("Date")?:""
        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("doctors" )
        val documentReference = collectionReference.document(duid)
        documentReference.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    dname.setText(
                        documentSnapshot.data?.get("lname").toString()
                            +", "+ documentSnapshot.data?.get("fname").toString()
                            + " "+documentSnapshot.data?.get("mname").toString()+".")
                    ddate.setText(date + " "+time)
                    drate.setText(documentSnapshot.data?.get("rate").toString())
                    dtype.setText(documentSnapshot.data?.get("type").toString())
                    demail.setText(documentSnapshot.data?.get("email").toString())
                    dphone.setText(documentSnapshot.data?.get("phone").toString())
                } else {

                }
            }
            .addOnFailureListener { exception ->
                // Handle failure to retrieve the document
                Log.e("ERROR", "Error getting document at schedule ", exception)
            }
        sched.setOnClickListener() {

            Log.d("sendhelp","b")
            val db = FirebaseFirestore.getInstance()

            val uData = HashMap<String,Any>()
            uData["PUID"] = uid
            uData["DUID"] = duid
            uData["Date"] = Timestamp(LocalDate.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toEpochSecond(ZoneOffset.UTC),0)
            uData["Time"] = time

            Log.d("sendhelp","c")

            val collectionReference = db.collection("schedules")
            val documentReference = collectionReference.document()

            val generatedId = documentReference.id


            documentReference.set(uData)
                .addOnSuccessListener {
                    println("Document with ID $generatedId is created successfully.")
                }
                .addOnFailureListener { exception ->
                    println("Error creating document: $exception")
                }
            onBackPressed()
        }
        bdphone.setOnClickListener(){
            val db = FirebaseFirestore.getInstance()
            val collectionReference = db.collection("doctors" )
            val documentReference = collectionReference.document(duid)
            documentReference.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val phoneNumber= documentSnapshot.data?.get("phone").toString()
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$phoneNumber")
                        }
                        startActivity(intent)
                        true
                    } else {

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure to retrieve the document
                    Log.e("ERROR", "Error getting document at schedule ", exception)
                }

        }
        bdemail.setOnClickListener() {

            val db = FirebaseFirestore.getInstance()
            val collectionReference = db.collection("doctors" )
            val documentReference = collectionReference.document(duid)
            documentReference.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val selection= documentSnapshot.data?.get("email").toString()
                        val recipients = arrayOf(selection)

                        val intentSelector = Intent(Intent.ACTION_SENDTO)
                        intentSelector.data = Uri.parse("mailto:")
                        val emailIntent = Intent(Intent.ACTION_SEND)
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "test")
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "test")
                        emailIntent.selector = intentSelector

                        startActivity(Intent.createChooser(emailIntent, "Send email"))
                        true
                    } else {

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure to retrieve the document
                    Log.e("ERROR", "Error getting document at schedule ", exception)
                }
        }
    }
}