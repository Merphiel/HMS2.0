package com.example.softeng2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

class OrganizationSignUpActivity : AppCompatActivity() {

    private lateinit var organizationNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_organization)

        organizationNameEditText = findViewById(R.id.inp_name)
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
        val organizationName = organizationNameEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            return false
        }

        return true
    }
}
