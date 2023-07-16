package com.example.softeng2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.Adapter.DoctorListAdapter
import com.example.softeng2.databinding.ActivityDoctorsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DoctorsActivity : AppCompatActivity(){

    private lateinit var activityDoctorsBinding:ActivityDoctorsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorListAdapter

    private var count:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDoctorsBinding= ActivityDoctorsBinding.inflate(layoutInflater)
        setContentView(activityDoctorsBinding.root)
        val puid= intent.getStringExtra("PUID")?:""

        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        val doctorsCollectionRef = db.collection("doctors").get()
            .addOnSuccessListener { querySnapshot ->
                count = querySnapshot.size()
                for (document in querySnapshot.documents) {
                    val documentData = document.data
                    documentData?.let {
                        val documentHashMap = HashMap<String, Any>()
                        documentHashMap["UID"]=document.id
                        for ((key, value) in it.entries) {
                            documentHashMap[key] = value
                        }
                        documentList.add(documentHashMap)
                    }
                }
                adapter = DoctorListAdapter(documentList ,count,this,puid)
                activityDoctorsBinding.rvDoctors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                activityDoctorsBinding.rvDoctors.adapter = adapter;
                setContentView(activityDoctorsBinding.root)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }
}