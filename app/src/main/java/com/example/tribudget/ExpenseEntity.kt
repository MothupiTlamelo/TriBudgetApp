package com.example.tribudget

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val description: String,
    val amount: Double,
    val category: String,
    val date: Long,            //utilised for the SQL filtering
    val dateString: String,    //date in the format (yyyy-MM-dd)
    val startTime: String,
    val endTime: String,
    val photoPath: String = ""
)