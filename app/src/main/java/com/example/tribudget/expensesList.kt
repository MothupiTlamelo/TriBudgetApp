package com.example.tribudget

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class expensesList : AppCompatActivity() {

    private var filterStartDate: String = ""
    private var filterEndDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses_list)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpense::class.java))
        }

        //navigation to category summary
        val btnViewSummary = findViewById<Button>(R.id.btnViewSummary)
        btnViewSummary.setOnClickListener {
            // Requirement: Selectable period check
            if (filterStartDate.isNotEmpty() && filterEndDate.isNotEmpty()) {
                val intent = Intent(this, CategorySummaryActivity::class.java)
                intent.putExtra("START_DATE", filterStartDate)
                intent.putExtra("END_DATE", filterEndDate)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select both Start and End dates first", Toast.LENGTH_SHORT).show()
            }
        }

        setupFilterButtons()
        displayExpenses()
    }

    override fun onResume() {
        super.onResume()
        AppData.loadExpenses(this)
        displayExpenses()
    }

    private fun setupFilterButtons() {
        val btnFrom = findViewById<Button>(R.id.btnFilterFrom)
        val btnTo = findViewById<Button>(R.id.btnFilterTo)

        btnFrom.setOnClickListener {
            showDatePicker { date ->
                filterStartDate = date
                btnFrom.text = "From: $date"
                displayExpenses()
            }
        }

        btnTo.setOnClickListener {
            showDatePicker { date ->
                filterEndDate = date
                btnTo.text = "To: $date"
                displayExpenses()
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun displayExpenses() {
        val listView = findViewById<ListView>(R.id.expensesListView)

        //pass 'this' to the filtered call
        val dataToShow = if (filterStartDate.isNotEmpty() && filterEndDate.isNotEmpty()) {
            AppData.getFilteredExpenses(this, filterStartDate, filterEndDate)
        } else {
            AppData.expenseList
        }

        if (dataToShow.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("No expenses found for this period."))
            listView.adapter = adapter
            return
        }

        //utilizing .date (long) for a more accurate sort
        val sortedList = dataToShow.sortedByDescending { it.date }

        val expenseStrings = sortedList.map { expense ->
            "${expense.dateString} - ${expense.description}\n" +
                    "Category: ${expense.category} | Amount: R${String.format("%.2f", expense.amount)}" +
                    if (expense.photoPath.isNotEmpty()) "\n📷 [Tap to view Receipt]" else ""
        }

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, expenseStrings)

        listView.setOnItemClickListener { _, _, position, _ ->
            val expense = sortedList[position]
            if (expense.photoPath.isNotEmpty()) {
                showPhotoDialog(expense.photoPath)
            } else {
                Toast.makeText(this, "No photo attached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPhotoDialog(photoPath: String) {
        val builder = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setPadding(20, 20, 20, 20)
        imageView.setImageURI(Uri.parse(photoPath))
        imageView.adjustViewBounds = true // Keeps aspect ratio

        builder.setView(imageView)
        builder.setTitle("Receipt Photo")
        builder.setPositiveButton("Close", null)
        builder.show()
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            val date = String.format("%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(date)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }
}