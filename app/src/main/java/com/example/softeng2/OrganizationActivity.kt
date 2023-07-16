package com.example.softeng2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
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

        val addBtn = findViewById<ImageButton>(R.id.btn_add)
        addBtn.setOnClickListener{
            setContentView(R.layout.activity_add_organization)
        }
        recyclerView = findViewById(R.id.rv_hospitals)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadData()
    }
    fun loadData() {
        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        db.collection("doctors").document(intent.getStringExtra("DUID").toString()).get().addOnSuccessListener{
            data->
            val org = data.data!!.get("org").toString()
            db.collection("organizations").whereEqualTo("OID",org).get().addOnSuccessListener{
                data->

            }
        }
    }
    private inner class MyAdapter(
        private val documentList: MutableList<Map<String, Any>>,
        private val itemcount: Int,
        private val context: Context,
        private val puid: String) : RecyclerView.Adapter<OrganizationActivity.MyAdapter.ViewHolder>() {

        // ViewHolder class
        inner class ViewHolder(private val appointmentCardBinding: AppointmentCardBinding, private val context: Context) : RecyclerView.ViewHolder(appointmentCardBinding.root) {
            fun bind(apt: Map<String, Any>, pos:Int) {
                Log.d("errorasd",apt.get("SID").toString())
                apt.let {

                }
                appointmentCardBinding.tvFee.text=apt.get("rate").toString() + " PHP"
                appointmentCardBinding.tvName.text= apt.get("lname").toString()+ ", " + apt.get("fname").toString()+ " " + apt.get("mname").toString() + "."
                val dateFormat = SimpleDateFormat("dd MMMM yyyy")
                val date = apt.get("Date") as Timestamp
                appointmentCardBinding.tvTime.text="When: " + dateFormat.format(date.toDate())+" "+apt.get("Time").toString()
                appointmentCardBinding.btnDetails.setOnClickListener() {
                    val uid= (context as PatientHomeActivity).intent.getStringExtra("PUID")
                    val intent = Intent(context, PatientCheckScheduleActivity::class.java)
                    intent.putExtra("PUID",apt.get("PUID").toString())
                    intent.putExtra("DUID",apt.get("DUID").toString())
                    intent.putExtra("SID",apt.get("SID").toString())
                    intent.putExtra("Time",apt.get("Time").toString())
                    intent.putExtra("Date", dateFormat.format(date.toDate()))
                    Log.d("asdc",apt.get("SID").toString()+apt.get("PUID").toString() + apt.get("DUID").toString())
                    context.startActivity(intent)
                }
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