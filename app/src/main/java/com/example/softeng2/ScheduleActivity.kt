package com.example.softeng2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.softeng2.databinding.ActivitySignup2Binding
import com.google.firebase.firestore.FirebaseFirestore

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
        val sched:Button =findViewById(R.id.btn_sched)

        back.setOnClickListener() {
            onBackPressed()
        }
        var uid = intent.getStringExtra("UID")?:""
        var duid= intent.getStringExtra("DUID")?:""
        var time = intent.getStringExtra("Time")?:""
        var date= intent.getStringExtra("Date")?:""
        drate.setOnClickListener() {

        }
        bdphone.setOnClickListener(){
            val phoneNumber = 1
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            startActivity(intent)
            true
        }
        bdemail.setOnClickListener() {

            val db = FirebaseFirestore.getInstance()
            val collectionReference = db.collection("doctors" )
            val documentReference = collectionReference.document(duid)
            documentReference.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val selection= documentSnapshot.data?.get("userType")
                    } else {

                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure to retrieve the document
                    Log.e("ERROR", "Error getting document at schedule ", exception)
                }

            val recipients = arrayOf("")

            val intentSelector = Intent(Intent.ACTION_SENDTO)
            intentSelector.data = Uri.parse("mailto:")
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "test")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "test")
            emailIntent.selector = intentSelector

            startActivity(Intent.createChooser(emailIntent, "Send email"))
            true

        }
    }
}