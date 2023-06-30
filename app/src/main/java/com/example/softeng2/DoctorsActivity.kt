package com.example.softeng2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.Adapter.DoctorListAdapter
import com.example.softeng2.databinding.ActivityDoctorsBinding

class DoctorsActivity : AppCompatActivity(){

    private lateinit var activityDoctorsBinding:ActivityDoctorsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDoctorsBinding= ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(activityDoctorsBinding.root)
        val puid= intent.getStringExtra("PUID")?:""
        adapter = DoctorListAdapter(this,puid)
        activityDoctorsBinding.rvDoctors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        activityDoctorsBinding.rvDoctors.adapter = adapter;
        setContentView(activityDoctorsBinding.root)
    }
}