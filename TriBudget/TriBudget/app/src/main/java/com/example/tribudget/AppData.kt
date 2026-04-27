package com.example.tribudget

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

object AppData {
    // List to store the categories in memory
    val categoryList = mutableListOf<String>()

    // List to store expenses (will be loaded from SharedPreferences)
    val expenseList = mutableListOf<ExpenseEntity>()

    // Current logged-in user
    var currentUser: String = ""

    // SharedPreferences keys
    private const val PREFS_NAME = "TriBudgetPrefs"
    private const val KEY_EXPENSES = "expenses_"
    private const val KEY_MIN_GOAL = "min_goal_"
    private const val KEY_MAX_GOAL = "max_goal_"

    // Load expenses for current user from SharedPreferences
    fun loadExpenses(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString(KEY_EXPENSES + currentUser, "[]")
        val type = object : TypeToken<MutableList<ExpenseEntity>>() {}.type
        expenseList.clear()
        expenseList.addAll(Gson().fromJson(json, type))
    }

    // Save expenses for current user to SharedPreferences
    fun saveExpenses(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(expenseList)
        sharedPrefs.edit {
            putString(KEY_EXPENSES + currentUser, json)
        }
    }

    // Add a new expense
    fun addExpense(expense: ExpenseEntity, context: Context) {
        expenseList.add(expense)
        saveExpenses(context)
    }

    // Get minimum goal for current user
    fun getMinGoal(context: Context): Double {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_MIN_GOAL + currentUser, "0")?.toDoubleOrNull() ?: 0.0
    }

    // Get maximum goal for current user
    fun getMaxGoal(context: Context): Double {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_MAX_GOAL + currentUser, "999999")?.toDoubleOrNull() ?: 999999.0
    }

    // Save goals for current user
    fun saveGoals(minGoal: Double, maxGoal: Double, context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putString(KEY_MIN_GOAL + currentUser, minGoal.toString())
            putString(KEY_MAX_GOAL + currentUser, maxGoal.toString())
        }
    }

    // Load categories for current user
    fun loadCategories(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("categories_${currentUser}", "[]")
        val type = object : TypeToken<MutableList<String>>() {}.type
        categoryList.clear()
        categoryList.addAll(Gson().fromJson(json, type))

        // Add default categories if empty
        if (categoryList.isEmpty()) {
            categoryList.addAll(listOf("Food", "Transport", "Shopping", "Entertainment", "Bills"))
            saveCategories(context)
        }
    }

    // Save categories for current user
    fun saveCategories(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(categoryList)
        sharedPrefs.edit {
            putString("categories_${currentUser}", json)
        }
    }

    // Get total spent in current month
    fun getCurrentMonthTotal(): Double {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        return expenseList.filter { expense ->
            val parts = expense.date.split("-")
            if (parts.size == 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                year == currentYear && month == currentMonth
            } else false
        }.sumOf { it.amount }
    }
}