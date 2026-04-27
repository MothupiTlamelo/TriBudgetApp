package com.example.tribudget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedBudgetManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("shared_budgets", Context.MODE_PRIVATE)
    private val budgets = mutableListOf<SharedBudgetItem>()

    init {
        loadBudgets()
    }

    private fun loadBudgets() {
        val json = prefs.getString("budgets", "[]") ?: "[]"
        val type = object : TypeToken<MutableList<SharedBudgetItem>>() {}.type
        budgets.clear()
        budgets.addAll(Gson().fromJson(json, type))
    }

    private fun saveBudgets() {
        val json = Gson().toJson(budgets)
        prefs.edit {
            putString("budgets", json)
        }
    }

    fun createBudget(name: String, totalBudget: Double, members: List<String>): SharedBudgetItem {
        val newBudget = SharedBudgetItem(
            id = java.util.UUID.randomUUID().toString(),
            name = name,
            members = members.toMutableList(),
            totalBudget = totalBudget
        )
        budgets.add(newBudget)
        saveBudgets()
        return newBudget
    }

    fun getUserBudgets(username: String): List<SharedBudgetItem> {
        return budgets.filter { it.members.contains(username) }
    }

    fun getSpendingByMember(budgetId: String): Map<String, Double> {
        val budget = budgets.find { it.id == budgetId } ?: return emptyMap()
        val result = mutableMapOf<String, Double>()
        for (expense in budget.expenses) {
            val currentAmount = result[expense.paidBy] ?: 0.0
            result[expense.paidBy] = currentAmount + expense.amount
        }
        return result
    }
}

// Renamed to avoid redeclaration conflict
data class SharedBudgetItem(
    val id: String,
    val name: String,
    val members: MutableList<String>,
    val totalBudget: Double,
    val currentSpent: Double = 0.0,
    val expenses: MutableList<SharedExpenseItem> = mutableListOf(),
    val categoryLimits: MutableMap<String, Double> = mutableMapOf()
)

data class SharedExpenseItem(
    val id: String,
    val description: String,
    val amount: Double,
    val paidBy: String,
    val splitAmong: List<String>,
    val date: String,
    val category: String
)