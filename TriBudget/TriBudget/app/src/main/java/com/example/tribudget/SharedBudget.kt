package com.example.tribudget

data class SharedBudget(
    val id: String,
    val name: String,
    val members: MutableList<String>,
    val totalBudget: Double,
    val currentSpent: Double = 0.0,
    val expenses: MutableList<ExpenseEntity> = mutableListOf(),
    val categoryLimits: MutableMap<String, Double> = mutableMapOf()
)

data class SharedExpense(
    val id: String,
    val description: String,
    val amount: Double,
    val paidBy: String,
    val splitAmong: List<String>,
    val date: String,
    val category: String
)