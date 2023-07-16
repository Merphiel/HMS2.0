package com.example.softeng2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.softeng2.databinding.ActivitySignup2Binding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import org.postgresql.Driver

class MainActivity : AppCompatActivity() {
    private lateinit var activitySignup2Binding: ActivitySignup2Binding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private val RC_SIGN_IN = 9001
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("129369011259-k34esgnm15v6fjqbjsck687lmmhcruvt.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.revokeAccess()

        val signInButton: Button = findViewById(R.id.signInBtn)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign-in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val uid = user?.uid

                    Log.d("Testcase","Success | " + uid)
                    val db = FirebaseFirestore.getInstance()
                    val collectionReference = db.collection("users")
                    val documentReference = collectionReference.document(uid?:"")

                    documentReference.get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val data = documentSnapshot.data
                                Log.d(TAG, "Document 'abc' exists: $data")
                                val selection= documentSnapshot.data?.get("userType")
                                if(selection=="patient") {
                                    intent = Intent(this@MainActivity, PatientHomeActivity::class.java)

                                    Log.d("aasdc", uid?:"")
                                    intent.putExtra("PUID", uid);
                                    startActivity(intent);
                                } else if (selection=="doctor") {
                                    val intent = Intent(this@MainActivity, DoctorHomeActivity::class.java)
                                    intent.putExtra("DUID", uid);
                                    startActivity(intent);
                                } else if (selection=="organization") {
                                    val intent = Intent(this@MainActivity, OrganizationHome::class.java)
                                    intent.putExtra("OUID", uid);
                                    startActivity(intent);
                                }
                            } else {

                                activitySignup2Binding =ActivitySignup2Binding.inflate(layoutInflater)
                                setContentView(activitySignup2Binding.root)

                                var autoCompleteTxt = findViewById<AutoCompleteTextView>(R.id.tv_users)
                                var adapterItems = ArrayAdapter<String>(this, R.layout.dropdown_users, arrayOf("Patient","Doctor","Organization"))
                                autoCompleteTxt.setAdapter(adapterItems)
                                autoCompleteTxt.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                                    val item = parent.getItemAtPosition(position).toString()
                                })
                                activitySignup2Binding.btnNext.setOnClickListener() {
                                    val uData = HashMap<String,Any>()
                                    val selection = activitySignup2Binding.tvUsers.text.toString().lowercase()
                                    uData["userType"]= selection
                                    db.collection("users")
                                        .document(uid?:"")
                                        .set(uData)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "User" + uid + " added with type" + uData)
                                            if(selection=="patient") {
                                                intent = Intent(this@MainActivity, PatientSignUpActivity::class.java)
                                                intent.putExtra("UID", uid);
                                                startActivity(intent);
                                            } else if (selection=="doctor") {
                                                val intent = Intent(this@MainActivity, DoctorSignUpActivity::class.java)
                                                intent.putExtra("UID", uid);
                                                startActivity(intent);
                                            } else if (selection=="organization") {
                                                val intent = Intent(this@MainActivity, OrganizationSignUpActivity::class.java)
                                                intent.putExtra("UID", uid);
                                                startActivity(intent);
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error writing document", e)
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle failure to retrieve the document
                            Log.e(TAG, "Error getting document 'abc': ", exception)
                        }

                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@MainActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }



    companion object {
        private const val TAG = "MainActivity"
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}