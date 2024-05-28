package com.mab.buwisbuddyph

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mab.buwisbuddyph.home.HomeActivity

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetET: EditText
    private lateinit var enterBudgetButton: Button
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        budgetET = findViewById(R.id.budget_ET)
        enterBudgetButton = findViewById(R.id.enterBudgetButton)

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@BudgetActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        enterBudgetButton.setOnClickListener {
            val budgetStr = budgetET.text.toString()
            if (budgetStr.isNotEmpty()) {
                val budget = budgetStr.toDouble()
                saveBudgetToFirebase(budget)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Enter a valid budget", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBudgetToFirebase(budget: Double) {
        if (user != null) {
            val userRef = db.collection("users").document(user.uid)
            val data = hashMapOf<String, Any>(
                "userBudget" to budget,
                "userExpenseList" to emptyList<Map<String, String>>()
            )
            userRef.update(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Budget set and expenses cleared successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
        }
    }

}
