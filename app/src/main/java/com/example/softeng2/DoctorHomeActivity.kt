package com.example.softeng2

import android.os.Bundle
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

class DoctorHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)
        recyclerView = findViewById(R.id.rv_doctors)

        navigationView.setNavigationItemSelectedListener(this)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        mAdapter = MyAdapter()
        recyclerView.adapter = mAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            R.id.patients -> Toast.makeText(this, "Patients", Toast.LENGTH_SHORT).show()
            R.id.medicalRecords -> Toast.makeText(this, "Medical Records", Toast.LENGTH_SHORT).show()
            R.id.hospital -> Toast.makeText(this, "Hospital", Toast.LENGTH_SHORT).show()
            R.id.myProfile -> Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            R.id.logOut -> Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show()

        }

        drawerLayout.closeDrawer(navigationView)
        return true
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
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
