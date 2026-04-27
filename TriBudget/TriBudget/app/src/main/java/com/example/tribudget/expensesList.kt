package com.example.tribudget

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class expensesList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }

        displayExpenses()
    }

    override fun onResume() {
        super.onResume()
        displayExpenses()
    }

    @SuppressLint("DefaultLocale")
    private fun displayExpenses() {
        val listView = findViewById<ListView>(R.id.expensesListView)

        if (AppData.expenseList.isEmpty()) {
            // Show empty state
            val emptyMessage = listOf("No expenses yet. Tap + to add one!")
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                emptyMessage
            )
            listView.adapter = adapter
            return
        }

        // Create display strings for each expense (sorted by date, newest first)
        val expenseStrings = AppData.expenseList.sortedByDescending { it.date }.map { expense ->
            "${expense.date} - ${expense.description}\n" +
                    "Category: ${expense.category} | Amount: R${String.format("%.2f", expense.amount)}\n" +
                    "Time: ${expense.startTime} - ${expense.endTime}"
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            expenseStrings
        )

        listView.adapter = adapter

        // Handle item click to view expense details
        listView.setOnItemClickListener { _, _, position, _ ->
            val expense = AppData.expenseList.sortedByDescending { it.date }[position]
            Toast.makeText(
                this,
                "💰 ${expense.description}: R${String.format("%.2f", expense.amount)}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}