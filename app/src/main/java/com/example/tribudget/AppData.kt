package com.example.tribudget

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

object AppData {
    var currentUser: String = ""
    val categoryList = mutableListOf<String>()

    val expenseList = mutableListOf<ExpenseEntity>()

    private const val PREFS_NAME = "TriBudgetPrefs"
    private const val KEY_MIN_GOAL = "min_goal_"
    private const val KEY_MAX_GOAL = "max_goal_"

    //helper for the database
    private fun getDao(context: Context) = AppDatabase.getDatabase(context).expenseDao()

    //room db expenses method

    fun loadExpenses(context: Context) {
        //pull from sqlite
        val loadedData = getDao(context).getAllByUserId(currentUser)
        expenseList.clear()
        expenseList.addAll(loadedData)
    }

    fun addExpense(expense: ExpenseEntity, context: Context) {
        //save to database
        getDao(context).insert(expense)
        expenseList.add(expense)
    }

    fun getFilteredExpenses(context: Context, startDateStr: String, endDateStr: String): List<ExpenseEntity> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val start = sdf.parse(startDateStr)?.time ?: 0L
            val end = sdf.parse(endDateStr)?.time ?: Long.MAX_VALUE

            //utilizing the dao's SQL query for fast filtering
            getDao(context).getExpensesByPeriod(currentUser, start, end)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCategoryTotalsForPeriod(context: Context, startDateStr: String, endDateStr: String): Map<String, Double> {
        return try {
            val filteredList = getFilteredExpenses(context, startDateStr, endDateStr)
            val totals = mutableMapOf<String, Double>()

            for (expense in filteredList) {
                val cat = expense.category
                val currentTotal = totals.getOrDefault(cat, 0.0)
                totals[cat] = currentTotal + expense.amount
            }
            totals
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getCurrentMonthTotal(context: Context): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        val startOfMonth = calendar.timeInMillis
        val endOfMonth = System.currentTimeMillis()

        val list = getDao(context).getAllForPeriod(currentUser, startOfMonth, endOfMonth)
        return list.sumOf { it.amount }
    }

    //goals and categories utilize shared preferences
    fun getMinGoal(context: Context): Double {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_MIN_GOAL + currentUser, "0")?.toDoubleOrNull() ?: 0.0
    }

    fun getMaxGoal(context: Context): Double {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_MAX_GOAL + currentUser, "999999")?.toDoubleOrNull() ?: 999999.0
    }

    fun saveGoals(minGoal: Double, maxGoal: Double, context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit {
            putString(KEY_MIN_GOAL + currentUser, minGoal.toString())
            putString(KEY_MAX_GOAL + currentUser, maxGoal.toString())
        }
    }

    fun loadCategories(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = sharedPrefs.getString("categories_${currentUser}", "[]")
        val type = object : TypeToken<MutableList<String>>() {}.type
        categoryList.clear()
        val loadedCats: MutableList<String>? = Gson().fromJson(json, type)
        if (loadedCats != null) {
            categoryList.addAll(loadedCats)
        }

        if (categoryList.isEmpty()) {
            categoryList.addAll(listOf("Food", "Transport", "Shopping", "Entertainment", "Bills"))
            saveCategories(context)
        }
    }

    fun saveCategories(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(categoryList)
        sharedPrefs.edit {
            putString("categories_${currentUser}", json)
        }
    }
}