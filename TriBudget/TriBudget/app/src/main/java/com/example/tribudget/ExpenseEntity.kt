package com.example.tribudget

data class ExpenseEntity(
    val id: String = System.currentTimeMillis().toString(),
    val date: String,        // Format: "2025-04-26"
    val startTime: String,   // Format: "14:30"
    val endTime: String,     // Format: "15:30"
    val description: String,
    val category: String,
    val amount: Double,
    val photoPath: String    // Empty string if no photo
)