package com.example.softeng2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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

class DoctorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var count:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.rv_doctors)

        // Set the listener for navigation item clicks
        navigationView.setNavigationItemSelectedListener(this)

        // Create and set the ActionBarDrawerToggle
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set the action bar's Home button to act as the navigation drawer toggle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set up the RecyclerView
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        loadData()
    }
    fun loadData() {
        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        val now = LocalDate.now()
        val max = now.plus(7, ChronoUnit.DAYS)
        val nowts:Timestamp= Timestamp(
            LocalDate.parse(
                LocalDate.of(now.year,now.monthValue,now.dayOfMonth)
            .toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toEpochSecond(
                ZoneOffset.UTC),0)
        val maxts:Timestamp= Timestamp(
            LocalDate.parse(
                LocalDate.of(max.year,max.monthValue,max.dayOfMonth)
            .toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toEpochSecond(
                ZoneOffset.UTC),0)
        db.collection("schedules")
            .whereGreaterThanOrEqualTo("Date",nowts)
            .whereLessThanOrEqualTo("Date",maxts)
            .whereEqualTo("DUID",intent.getStringExtra("DUID").toString())
            .orderBy("Date", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("aasdac",querySnapshot.size().toString())
                count = querySnapshot.size()
                for (document in querySnapshot.documents) {
                    val documentData = document.data
                    documentData?.let {
                        val documentHashMap = HashMap<String, Any>()
                        documentHashMap["SID"]=document.id
                        for ((key, value) in it.entries) {
                            documentHashMap[key] = value
                            Log.d("arrVal",documentHashMap.entries.joinToString("\n") { (key, value) -> "$key: $value" })
                        }
                        db.collection("patients").document(documentHashMap["PUID"].toString()).get().addOnSuccessListener {
                                data-> val documentData2 = data.data
                            documentData2?.let {
                                for ((key, value) in it.entries) {
                                    documentHashMap[key] = value
                                    Log.d("arrVal2",documentHashMap.entries.joinToString("\n") { (key, value) -> "$key: $value" })
                                }
                                documentList.add(documentHashMap)
                                if(documentList.size==count) {
                                    Log.d("arrVal3",documentList.toString())
                                    mAdapter = MyAdapter(documentList,count,this,intent.getStringExtra("PUID")?:"")
                                    recyclerView.adapter = mAdapter
                                }
                            }

                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle ActionBarDrawerToggle clicks
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation item clicks
        when (item.itemId) {
            R.id.home -> {

            }
            R.id.organizations -> {
                Toast.makeText(this, "Organizations", Toast.LENGTH_SHORT).show()
                var uid = intent.getStringExtra("DUID")?:""
                val intent = Intent(this, DoctorOrganizationActivity::class.java)
                intent.putExtra("DUID",uid)
                startActivity(intent)
            }
            R.id.myProfile -> Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.logOut -> {
                val intent = Intent(this@DoctorHomeActivity, MainActivity::class.java)
                startActivity(intent)
            }

        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawer(navigationView)
        return true
    }

    // Custom Adapter class
    private inner class MyAdapter(
        private val documentList: MutableList<Map<String, Any>>,
        private val itemcount: Int,
        private val context: Context,
        private val puid: String) : RecyclerView.Adapter<DoctorHomeActivity.MyAdapter.ViewHolder>() {

        // ViewHolder class
        inner class ViewHolder(private val appointmentCardBinding: AppointmentCardBinding, private val context: Context) : RecyclerView.ViewHolder(appointmentCardBinding.root) {
            fun bind(apt: Map<String, Any>, pos:Int) {
                Log.d("errorasd",apt.get("SID").toString())
                apt.let {

                }
                appointmentCardBinding.layout12.removeView(appointmentCardBinding.tvFee)
                appointmentCardBinding.layout12.removeView(appointmentCardBinding.tvLocation)
                appointmentCardBinding.tvName.text= apt.get("lname").toString()+ ", " + apt.get("fname").toString()+ " " + apt.get("mname").toString() + "."
                val dateFormat = SimpleDateFormat("dd MMMM yyyy")
                val date = apt.get("Date") as Timestamp
                appointmentCardBinding.tvTime.text="When: " + dateFormat.format(date.toDate())+" "+apt.get("Time").toString()
                appointmentCardBinding.btnDetails.setOnClickListener() {
                    val uid= (context as DoctorHomeActivity).intent.getStringExtra("PUID")
                    val intent = Intent(context, DoctorCheckScheduleActivity::class.java)
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
