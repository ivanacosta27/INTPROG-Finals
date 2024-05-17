package com.mab.buwisbuddyph

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val appCheck = FirebaseAppCheck.getInstance()
        appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Check if userId is available in SharedPreferences
        val userId = sharedPreferences.getString("userId", "")
        if (!userId.isNullOrEmpty()) {
            // Redirect to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Finish current activity to prevent going back to SignInActivity
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { view ->
            onLoginClick(view)
        }

        val signUpTV = findViewById<TextView>(R.id.signUpTV)
        signUpTV.setOnClickListener{ view ->
            onSignUp(view)
        }

        val forgotPasswordTV = findViewById<TextView>(R.id.forgotPasswordTV)
        forgotPasswordTV.setOnClickListener{ view ->
            onForgotPassword(view)
        }
    }

    private fun onLoginClick(view: View) {
        val email = findViewById<EditText>(R.id.emailET).text.toString()
        val password = findViewById<EditText>(R.id.passwordET).text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(view, "Please fill in all fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val userFullName = document.getString("userFullName") ?: ""
                                val userProfileImage = document.getString("userProfileImage") ?: ""

                                // Save data to SharedPreferences
                                val editor = sharedPreferences.edit()
                                editor.putString("userId", userId)
                                editor.putString("userFullName", userFullName)
                                editor.putString("userProfileImage", userProfileImage)
                                editor.apply()

                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                            } else {
                                Snackbar.make(view, "User document not found", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Snackbar.make(view, "Error retrieving user document: ${exception.message}", Snackbar.LENGTH_SHORT).show()
                        }
                } else {
                    Snackbar.make(view, "Authentication failed: ${task.exception?.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
    }

    private fun onSignUp(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun onForgotPassword(view: View){
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }
}
