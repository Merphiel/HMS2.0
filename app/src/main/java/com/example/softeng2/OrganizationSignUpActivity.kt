package com.example.softeng2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class OrganizationSignUpActivity : AppCompatActivity() {

    private lateinit var organizationNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var doneButton: Button
    private var oidList = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_organization)

        val uid = intent.getStringExtra("UID")
        organizationNameEditText = findViewById(R.id.inp_name)
        phoneNumberEditText = findViewById(R.id.inp_phone)
        emailEditText = findViewById(R.id.inp_email)
        doneButton = findViewById(R.id.btn_done)
        idEditText=findViewById(R.id.inp_id)

        val db = FirebaseFirestore.getInstance()
        val documentList = mutableListOf<Map<String, Any>>()
        val doctorsCollectionRef = db.collection("organizations").get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                        oidList.add(document.get("OID").toString())
                }
            }
        doneButton.setOnClickListener {
            if (validateInputs()) {
                Log.d("sendhelp","b")
                val db = FirebaseFirestore.getInstance()

                val uData = HashMap<String,Any>()
                uData["oname"] = organizationNameEditText.text.toString()
                uData["phone"] = phoneNumberEditText.text.toString()
                uData["email"] = emailEditText.text.toString()
                uData["OID"] = idEditText.text.toString()

                Log.d("sendhelp","c")
                db.collection("organizations")
                    .document(uid?:"")
                    .set(uData)
                    .addOnSuccessListener {

                        Log.d("sendhelp","d")
                        val intent = Intent(this@OrganizationSignUpActivity, OrganizationHome::class.java)
                        startActivity(intent);
                    }
                    .addOnFailureListener { e ->
                        Log.w("signuppatient", "Error writing document", e)
                    }

            }
        }
    }

    private fun validateInputs(): Boolean {
        val organizationName = organizationNameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val oid =idEditText.text.toString().trim()

        if (organizationName.isEmpty()) {
            organizationNameEditText.error = "Please enter the organization's name"
            return false
        }


        if (phoneNumber.isEmpty()) {
            phoneNumberEditText.error = "Please enter your phone number"
            return false
        }

        if (email.isEmpty()) {
            emailEditText.error = "Please enter your email"
            return false
        }

        if (oidList.contains(oid)) {
            idEditText.error = "ID Already taken. Please assign a different one"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            return false
        }

        return true
    }
}
