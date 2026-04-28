package com.example.tribudget

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val signupBtn = findViewById<Button>(R.id.signupSubmitButton)
        val userField = findViewById<EditText>(R.id.signupUsername)
        val passField = findViewById<EditText>(R.id.signupPassword)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        //redirect to login page when "Already have an account?" is clicked
        loginLink.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)

            finish()
        }

        signupBtn.setOnClickListener {
            val user = userField.text.toString()
            val pass = passField.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                //save user credentials
                val sharedPrefs = getSharedPreferences("TriBudgetPrefs", Context.MODE_PRIVATE)
                sharedPrefs.edit {
                    putString("password_$user", pass)
                }

                Toast.makeText(this, "Account created! Please login.", Toast.LENGTH_SHORT).show()

                //navigate to login screen
                val intent = Intent(this, login::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}