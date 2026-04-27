package com.example.tribudget

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class addCategory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val input = findViewById<EditText>(R.id.categoryName)
        val saveBtn = findViewById<Button>(R.id.saveCategoryButton)

        saveBtn.setOnClickListener {
            val name = input.text.toString().trim()

            if (name.isNotEmpty()) {
                // Add to AppData and save persistently
                AppData.categoryList.add(name)
                AppData.saveCategories(this)

                Toast.makeText(this, "$name added!", Toast.LENGTH_SHORT).show()
                input.text.clear()

                // Go back to home screen
                finish()
            } else {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}