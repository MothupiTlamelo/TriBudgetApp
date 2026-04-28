package com.example.tribudget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpenseDao {
    @Insert
    fun insert(expense: ExpenseEntity)

    //view list for a user selectable period
    @Query("SELECT * FROM expenses WHERE userId = :user AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesByPeriod(user: String, start: Long, end: Long): List<ExpenseEntity>

    //the total amount spent on each category
    @Query("SELECT * FROM expenses WHERE userId = :user AND date BETWEEN :start AND :end")
    fun getAllForPeriod(user: String, start: Long, end: Long): List<ExpenseEntity>

    @Query("SELECT * FROM expenses WHERE userId = :user ORDER BY date DESC")
    fun getAllByUserId(user: String): List<ExpenseEntity>
}