package com.example.softeng2
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import java.time.ZoneOffset

class PatientHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.rv_patients)

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
        mAdapter = MyAdapter()
        recyclerView.adapter = mAdapter
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
            R.id.home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            R.id.doctors -> {
                val uid= intent.getStringExtra("PUID")
                Log.d("aasdb", uid?:"")
                Toast.makeText(this, uid, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, DoctorsActivity::class.java)
                intent.putExtra("PUID",uid)
                startActivity(intent)
            }
            R.id.organizations -> {
                Toast.makeText(this, "Organizations", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, OrganizationActivity::class.java)
                startActivity(intent)
            }

            R.id.appointments -> {
                val uid= intent.getStringExtra("PUID")
                Toast.makeText(this, "Appointments", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, PatientCalendarAppointmentsActivity::class.java)
                intent.putExtra("PUID",uid)
                startActivity(intent)
            }

            R.id.myProfile -> Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.logOut ->{
                val intent = Intent(this@PatientHomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // Close the drawer after handling the click
        drawerLayout.closeDrawer(navigationView)
        return true
    }
    private inner class MyAdapter : RecyclerView.Adapter<PatientHomeActivity.MyAdapter.ViewHolder>() {

        // ViewHolder class
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.tv_patients)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_patients, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = "Item $position"
        }

        override fun getItemCount(): Int {
            return 10
        }
    }
}
