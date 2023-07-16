package com.example.softeng2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.softeng2.databinding.AppointmentCardBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class OrganizationActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: OrganizationActivity.MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var count=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization)

        val DUID = intent.getStringExtra("DUID")
        val addBtn = findViewById<ImageButton>(R.id.btn_add)
        addBtn.setOnClickListener{
            setContentView(R.layout.activity_add_organization)
            findViewById<Button>(R.id.btn_existing).setOnClickListener {
                val db = Firebase.firestore
                val documentList = mutableListOf<Map<String, Any>>()
                db.collection("organizations")
                    .whereEqualTo("OID",findViewById<EditText>(R.id.inp_oid).text.toString())
                    .get()
                    .addOnSuccessListener() {
                        db.collection("doctors").document(DUID.toString()).update("org",findViewById<EditText>(R.id.inp_oid).text.toString())
                    }
                onBackPressed()
            }
            findViewById<Button>(R.id.btn_independent).setOnClickListener {
                val db = Firebase.firestore
                val documentList = mutableListOf<Map<String, Any>>()
                db.collection("doctors").document(DUID!!).get().addOnSuccessListener {
                    data->
                    val acq= data.data!!
                    val map= mutableMapOf<String, Any>()
                    map.put("phone",acq.get("phone").toString())
                    map.put("email",acq.get("email").toString())
                    map.put("oname",findViewById<EditText>(R.id.inp_iid).text.toString())
                    map.put("address",findViewById<EditText>(R.id.editTextText3).text.toString())
                    val collectionReference = db.collection("organizations")
                    val documentReference = collectionReference.document()
                    documentReference.set(map)
                    onBackPressed()
                }
            }
        }
        recyclerView = findViewById(R.id.rv_hospitals)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadData()
    }
    fun loadData() {
        Log.d("testas",intent.getStringExtra("DUID").toString())
        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        db.collection("doctors").document(intent.getStringExtra("DUID")!!).get().addOnSuccessListener{
            data->
            val org=data.data!!.get("org")
            Log.d("org12",org.toString())
            db.collection("organizations")
                .whereEqualTo("OID",org)
                .get()
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
                    mAdapter = MyAdapter(documentList,count,this)
                    recyclerView.adapter = mAdapter
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }
        }

    }
    private inner class MyAdapter(
        private val documentList: MutableList<Map<String, Any>>,
        private val itemcount: Int,
        private val context: Context) : RecyclerView.Adapter<OrganizationActivity.MyAdapter.ViewHolder>() {

        // ViewHolder class
        inner class ViewHolder(private val appointmentCardBinding: AppointmentCardBinding, private val context: Context) : RecyclerView.ViewHolder(appointmentCardBinding.root) {
            fun bind(apt: Map<String, Any>, pos:Int) {
                Log.d("errorasd",apt.get("SID").toString())
                apt.let {

                }
                appointmentCardBinding.tvFee.text=apt.get("phone").toString()
                appointmentCardBinding.tvName.text= apt.get("oname").toString()
                appointmentCardBinding.tvTime.text=apt.get("email").toString()
                appointmentCardBinding.tvLocation.text=apt.get("address").toString()
                appointmentCardBinding.layout12.removeView(appointmentCardBinding.btnDetails)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = AppointmentCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding,context)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(documentList[position],position)
        }

        override fun getItemCount(): Int {
            Log.d("aasda",itemcount.toString())
            return itemcount
        }
    }
}