package com.example.tribudgetapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CategoryActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val categoryInput = findViewById<EditText>(R.id.categoryInput)
        val addBtn = findViewById<Button>(R.id.addBtn)
        val listView = findViewById<ListView>(R.id.categoryList)

        val categories = ArrayList<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)

        listView.adapter = adapter

        addBtn.setOnClickListener {

            val text = categoryInput.text.toString()

            if (text.isNotEmpty()) {
                categories.add(text)
                adapter.notifyDataSetChanged()
                categoryInput.text.clear()
            } else {
                Toast.makeText(this, getString(R.string.empty_category), Toast.LENGTH_SHORT).show()
            }
        }
    }
}