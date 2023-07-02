package com.example.softeng2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions

class PatientCheckScheduleActivity: AppCompatActivity() {
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
            uData["Date"] = date
            uData["Time"] = time

            Log.d("sendhelp","c")


            var documentRef = db.collection("patients").document(uid)

            documentRef.update("schedules", FieldValue.arrayUnion(uData))
                .addOnSuccessListener {
                    // Update successful
                    println("Array entry added successfully.")
                }
                .addOnFailureListener { exception ->
                    // Check if the exception is due to arrayField not existing
                    if (exception is FirebaseFirestoreException && exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        // If arrayField doesn't exist, create it and add the entry
                        documentRef.set(hashMapOf("arrayField" to arrayListOf(uData)), SetOptions.merge())
                            .addOnSuccessListener {
                                println("Array field created and entry added successfully.")
                            }
                            .addOnFailureListener { e ->
                                // Error handling
                                println("Error creating array field and adding entry: $e")
                            }
                    } else {
                        // Error handling for other exceptions
                        println("Error adding array entry: $exception")
                    }
                }
            documentRef = db.collection("doctors").document(duid)

            documentRef.update("schedules", FieldValue.arrayUnion(uData))
                .addOnSuccessListener {
                    // Update successful
                    println("Array entry added successfully.")
                }
                .addOnFailureListener { exception ->
                    // Check if the exception is due to arrayField not existing
                    if (exception is FirebaseFirestoreException && exception.code == FirebaseFirestoreException.Code.NOT_FOUND) {
                        // If arrayField doesn't exist, create it and add the entry
                        documentRef.set(hashMapOf("arrayField" to arrayListOf(uData)), SetOptions.merge())
                            .addOnSuccessListener {
                                println("Array field created and entry added successfully.")
                            }
                            .addOnFailureListener { e ->
                                // Error handling
                                println("Error creating array field and adding entry: $e")
                            }
                    } else {
                        // Error handling for other exceptions
                        println("Error adding array entry: $exception")
                    }
                }

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