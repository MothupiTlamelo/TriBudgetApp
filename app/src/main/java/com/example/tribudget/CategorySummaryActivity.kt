package com.example.tribudget

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CategorySummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)

        try {
            //getting dates safely from the Intent
            val startDate = intent.getStringExtra("START_DATE") ?: ""
            val endDate = intent.getStringExtra("END_DATE") ?: ""

            //finding view via IDs
            val titleTv = findViewById<TextView>(R.id.summaryTitle)
            val listView = findViewById<ListView>(R.id.categoryListView)

            titleTv.text = "Totals: $startDate to $endDate"

            //getting the totals from AppData using the correct variable names
            val totalsMap = if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                AppData.getCategoryTotalsForPeriod(this, startDate, endDate)
            } else {
                emptyMap()
            }

            if (totalsMap.isEmpty()) {
                listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOf("No data for this period"))
            } else {
                val displayList = totalsMap.entries.map { entry ->
                    val category = entry.key
                    val total = entry.value
                    "$category: R${String.format("%.2f", total)}"
                }
                listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}