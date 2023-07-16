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
import com.example.softeng2.Adapter.DoctorListAdapter
import com.example.softeng2.databinding.DoctorCardBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrganizationHome : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: OrganizationHome.MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var count:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.rv_organizations)

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

        val db = Firebase.firestore
        val documentList = mutableListOf<Map<String, Any>>()
        db.collection("organizations").document(intent.getStringExtra("OUID").toString()).get().addOnSuccessListener{
            data->
            Log.d("data",intent.getStringExtra("OUID").toString())
            val org = data.data!!.get("OID").toString()
            Log.d("data",org)
            val doctorsCollectionRef = db.collection("doctors")
                .whereEqualTo("org",org)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    count = querySnapshot.size()
                    Log.d("count",count.toString())
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
            R.id.settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.logOut -> {
                val intent = Intent(this@OrganizationHome, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawer(navigationView)
        return true
    }
    private inner class MyAdapter(

        private val documentList: MutableList<Map<String, Any>>,
        private val itemcount: Int,
        private val context: Context,
    ) : RecyclerView.Adapter<OrganizationHome.MyAdapter.ViewHolder>() {
        var num=0
        inner class ViewHolder(private val doctorCardBinding: DoctorCardBinding, private val context: Context) : RecyclerView.ViewHolder(doctorCardBinding.root){
            fun bind(doc: Map<String, Any>, pos:Int) {
                Log.d("errorasd",doc.get("UID").toString())
                doctorCardBinding.ll.removeView(doctorCardBinding.btnBook)
                doctorCardBinding.tvName.text = (
                        doc.get("lname").toString()
                                + ", " + doc.get("fname").toString()
                                + " " + doc.get("mname")
                            .toString() + ".")

                doctorCardBinding.tvType.text = (
                        doc.get("type").toString())

                doctorCardBinding.tvFee.text = ("PHP " +
                        doc.get("rate").toString())

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = DoctorCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            return ViewHolder(binding,context)
        }

        override fun getItemCount(): Int {
            Log.d("aasda",itemcount.toString())
            return itemcount
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(documentList[position], position)
        }
    }
}
