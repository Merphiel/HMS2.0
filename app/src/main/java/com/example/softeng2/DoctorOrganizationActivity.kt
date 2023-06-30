package com.example.softeng2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class DoctorOrganizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization)

        val addBtn = findViewById<ImageButton>(R.id.btn_add)
        addBtn.setOnClickListener{
            setContentView(R.layout.activity_add_organization)
        }
    }
}