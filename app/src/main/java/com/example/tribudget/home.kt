package com.example.tribudget

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val categoriesBtn = findViewById<Button>(R.id.categoryButton)
        categoriesBtn.setOnClickListener {
            startActivity(Intent(this, addCategory::class.java))
        }

        //setup menu button
        setupMenuButtons()
    }

    private fun setupMenuButtons() {
        val hamburgerMenu = findViewById<ImageView>(R.id.hamburgerMenu)
        hamburgerMenu.setOnClickListener {
            showMenuDialog()
        }
    }

    private fun showMenuDialog() {
        val options = arrayOf(
            "➕ Add Expense",
            "📷 Scan Receipt",
            "📊 Dashboard",
            "👥 Shared Budgets",
            "🏆 Badges",
            "🎯 Monthly Goals",
            "📋 View All Expenses",
            "🌙 Dark Mode",
            "🚪 Logout"
        )

        android.app.AlertDialog.Builder(this)
            .setTitle("Menu")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, AddExpense::class.java))
                    1 -> startActivity(Intent(this, ReceiptScannerActivity::class.java))
                    2 -> startActivity(Intent(this, DashboardActivity::class.java))
                    3 -> startActivity(Intent(this, SharedBudgetActivity::class.java))
                    4 -> startActivity(Intent(this, BadgesActivity::class.java))
                    5 -> startActivity(Intent(this, goals::class.java))
                    6 -> startActivity(Intent(this, expensesList::class.java))
                    7 -> toggleDarkMode()
                    8 -> logout()
                }
            }
            .show()
    }

    private fun toggleDarkMode() {
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            android.content.res.Configuration.UI_MODE_NIGHT_NO -> {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark mode enabled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light mode enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logout() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
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