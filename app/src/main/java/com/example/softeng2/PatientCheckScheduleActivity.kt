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
        setContentView(R.layout.activity_viewschedulepatientcard)

        val dname:TextView= findViewById(R.id.tv_docname)
        val demail:TextView= findViewById(R.id.tv_docemail)
        val dphone:TextView= findViewById(R.id.tv_docphone)
        val dtype:TextView= findViewById(R.id.tv_doctype)
        val drate:TextView= findViewById(R.id.tv_docrate)
        val ddate:TextView= findViewById(R.id.tv_date)
        val dloc:TextView= findViewById(R.id.tv_loc)
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
        var sid= intent.getStringExtra("SID")?:""
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
            db.collection("schedules").document(sid).delete()
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