package com.example.softeng2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class PatientSignUpActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var middleNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_patient)

        firstNameEditText = findViewById(R.id.inp_fname)
        middleNameEditText = findViewById(R.id.inp_mname)
        lastNameEditText = findViewById(R.id.inp_lname)
        phoneNumberEditText = findViewById(R.id.inp_phone)
        emailEditText = findViewById(R.id.inp_email)
        doneButton = findViewById(R.id.btn_done)

        doneButton.setOnClickListener {
            if (validateInputs()) {
                // Perform signup action here
            }
        }
    }

    private fun validateInputs(): Boolean {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (firstName.isEmpty()) {
            firstNameEditText.error = "Please enter your first name"
            return false
        }

        if (lastName.isEmpty()) {
            lastNameEditText.error = "Please enter your last name"
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

        return true
    }
}
