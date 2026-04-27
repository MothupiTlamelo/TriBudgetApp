package com.example.tribudget

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val categoriesBtn = findViewById<Button>(R.id.categoryButton)
        categoriesBtn.setOnClickListener {
            startActivity(Intent(this, addCategory::class.java))
        }

        // Add a button to view expenses (you can add this to your XML later)
        // For now, let's add a button to go to Goals screen
        setupMenuButtons()
    }

    private fun setupMenuButtons() {
        // You can add these as additional buttons in your home screen XML
        // For demonstration, we'll use the hamburger menu to show options
        val hamburgerMenu = findViewById<android.widget.ImageView>(R.id.hamburgerMenu)
        hamburgerMenu.setOnClickListener {
            showMenuDialog()
        }
    }

    private fun showMenuDialog() {
        val options = arrayOf("Add Expense", "Monthly Goals", "View All Expenses", "Logout")
        android.app.AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, AddExpense::class.java))
                    1 -> startActivity(Intent(this, goals::class.java))
                    2 -> startActivity(Intent(this, expensesList::class.java))
                    3 -> logout()
                }
            }
            .show()
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()

        val listView = findViewById<ListView>(R.id.categoryListView)

        // Refresh categories from AppData
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            AppData.categoryList
        )

        listView.adapter = adapter

        // Handle category click to add expense with that category
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = AppData.categoryList[position]
            val intent = Intent(this, AddExpense::class.java)
            intent.putExtra("SELECTED_CATEGORY", selectedCategory)
            startActivity(intent)
        }
    }
}