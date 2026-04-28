package com.example.tribudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val loginBtn = findViewById<Button>(R.id.welcomeLogin)
        val signUpBtn = findViewById<Button>(R.id.welcomeSignup)

        //login page one login button is clicked
        loginBtn.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        //signup page one signup button is clicked
        signUpBtn.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }
    }
}