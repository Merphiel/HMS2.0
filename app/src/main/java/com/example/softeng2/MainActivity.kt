package com.example.softeng2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MainActivity : AppCompatActivity() {
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
                    // Sign-in successful
                    val user = mAuth.currentUser
                    val uid = user?.uid

                    // Proceed with connecting to PostgreSQL and performing operations based on the UID
                    Log.d("Testcase","Success | " + uid)
                    connectToPostgreSQL(uid)
                } else {
                    // Sign-in failed
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this@MainActivity, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun connectToPostgreSQL(uid: String?) {
        // Implement your PostgreSQL connection and operations based on the UID
        // Example:
        var connection: Connection? = null
        try {
            // Establish connection to PostgreSQL
            connection = DriverManager.getConnection("postgres://caizhiyongdev:TnajYXzkw3l8@ep-tiny-hat-657935.ap-southeast-1.aws.neon.tech/neondb", "caizhiyongdev", "oX3UhcBig1YI")

            // Use the UID to retrieve or manipulate data in PostgreSQL
            // Example: Execute a query to retrieve user-specific data
            val query = "SELECT * FROM your_table WHERE uid = ?"
            val statement = connection.prepareStatement(query)
            statement.setString(1, uid)
            val resultSet = statement.executeQuery()

            // Process the results
            while (resultSet.next()) {
                // Retrieve data from the result set
                // Example:
                val data = resultSet.getString("column_name")
                // Do something with the retrieved data
            }

            // Close the statement and result set
            statement.close()
            resultSet.close()
        } catch (e: SQLException) {
            // Handle any exceptions that occur during the database operations
            e.printStackTrace()
        } finally {
            // Close the database connection
            connection?.close()
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
