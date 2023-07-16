package com.example.softeng2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Integers

class DoctorSignUpActivity : AppCompatActivity() {

    private val items = arrayOf("Dentist", "Dermatologist", "Pediatrician", "Therapist", "Ophthalmologist")
    private lateinit var autoCompleteTxt: AutoCompleteTextView
    private lateinit var adapterItems: ArrayAdapter<String>

    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var doneButton: Button
    private lateinit var bio:EditText
    private lateinit var rate:EditText
    private lateinit var type:AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_doctor)

        val uid = intent.getStringExtra("UID")
        firstNameEditText = findViewById(R.id.inp_fname)
        middleNameEditText = findViewById(R.id.inp_mname)
        lastNameEditText = findViewById(R.id.inp_lname)
        phoneNumberEditText = findViewById(R.id.inp_phone)
        emailEditText = findViewById(R.id.inp_email)
        doneButton = findViewById(R.id.btn_done)
        bio=findViewById(R.id.inp_bio)
        rate = findViewById(R.id.inp_rate)
        type = findViewById(R.id.tv_doctorType)


        autoCompleteTxt = findViewById<AutoCompleteTextView>(R.id.tv_doctorType)
        adapterItems = ArrayAdapter<String>(this, R.layout.dropdown_users, items)

        autoCompleteTxt.setAdapter(adapterItems)

        autoCompleteTxt.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
        })

        doneButton.setOnClickListener {
            if (validateInputs()) {
                Log.d("sendhelp","b")
                val db = FirebaseFirestore.getInstance()

                val uData = HashMap<String,Any>()
                uData["fname"] = firstNameEditText.text.toString()
                uData["mname"] = middleNameEditText.text.toString()
                uData["lname"] = lastNameEditText.text.toString()
                uData["phone"] = phoneNumberEditText.text.toString()
                uData["email"] = emailEditText.text.toString()
                uData["bio"] = bio.text.toString()
                uData["rate"] = rate.text.toString().toInt()
                uData["type"] = type.text.toString()

                Log.d("sendhelp","c")
                db.collection("doctors")
                    .document(uid?:"")
                    .set(uData)
                    .addOnSuccessListener {

                        Log.d("sendhelp","d")
                        val intent = Intent(this@DoctorSignUpActivity, DoctorHomeActivity::class.java)

                        var uid = intent.getStringExtra("DUID")?:""
                        intent.putExtra("DUID",uid)
                        startActivity(intent);
                    }
                    .addOnFailureListener { e ->
                        Log.w("signuppatient", "Error writing document", e)
                    }

            }
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val fee= rate.text.toString().trim();

        if (firstName.isEmpty()) {
            firstNameEditText.error = "Please enter your first name"
            return false
        }

        if (lastName.isEmpty()) {
            lastNameEditText.error = "Please enter your last name"
            return false
        }

        if(!phoneNumber.matches(Regex("^\\d{11}$"))){
            phoneNumberEditText.error = "Please enter a valid 11 digit phone number"
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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            return false
        }
        try {
            fee.toInt()
        } catch (e: NumberFormatException) {
            return false
        }

        return true
    }
}
