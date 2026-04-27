package com.example.tribudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn = findViewById<Button>(R.id.loginSubmitButton)
        val userField = findViewById<EditText>(R.id.loginUsername)
        val passField = findViewById<EditText>(R.id.loginPassword)

        loginBtn.setOnClickListener {
            val user = userField.text.toString()
            val pass = passField.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                // Verify credentials
                val sharedPrefs = getSharedPreferences("TriBudgetPrefs", Context.MODE_PRIVATE)
                val savedPassword = sharedPrefs.getString("password_$user", "")

                if (pass == savedPassword) {
                    Toast.makeText(this, "Welcome back, $user", Toast.LENGTH_SHORT).show()

                    // Set current user in AppData
                    AppData.currentUser = user

                    // Load user's categories and expenses
                    AppData.loadCategories(this)
                    AppData.loadExpenses(this)

                    // Redirect to home screen
                    val intent = Intent(this, home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}