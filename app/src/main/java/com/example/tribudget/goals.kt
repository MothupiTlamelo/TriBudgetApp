package com.example.tribudget

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import java.util.*

class goals : AppCompatActivity() {

    private lateinit var tvTotalSpent: TextView
    private lateinit var tvMinGoalStatus: TextView
    private lateinit var tvMaxGoalStatus: TextView
    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var btnCheckStatus: Button
    private lateinit var btnViewExpenses: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        initializeViews()
        loadAndDisplayReport()
        setupClickListeners()
    }

    private fun initializeViews() {
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvMinGoalStatus = findViewById(R.id.tvMinGoalStatus)
        tvMaxGoalStatus = findViewById(R.id.tvMaxGoalStatus)
        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoals = findViewById(R.id.btnSaveGoals)
        btnCheckStatus = findViewById(R.id.btnCheckStatus)
        btnViewExpenses = findViewById(R.id.btnViewExpenses)

        btnViewExpenses.text = getString(R.string.view_all_expenses)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun loadAndDisplayReport() {
        val currentMonthTotal = AppData.getCurrentMonthTotal(this)
        tvTotalSpent.text = String.format(Locale.US, "R%.2f", currentMonthTotal)

        // Load goals
        val minGoal = AppData.getMinGoal(this)
        val maxGoal = AppData.getMaxGoal(this)

        etMinGoal.setText(if (minGoal > 0) minGoal.toString() else "")
        etMaxGoal.setText(if (maxGoal < 999999) maxGoal.toString() else "")

        updateGoalStatus(currentMonthTotal, minGoal, maxGoal)
    }

    private fun updateGoalStatus(currentTotal: Double, minGoal: Double, maxGoal: Double) {
        if (minGoal > 0) {
            if (currentTotal < minGoal) {
                val remaining = String.format(Locale.US, "%.2f", minGoal - currentTotal)
                tvMinGoalStatus.text = getString(R.string.minimum_goal_warning, remaining)
                tvMinGoalStatus.setTextColor("#FFA500".toColorInt())
            } else {
                tvMinGoalStatus.text = getString(R.string.minimum_goal_reached)
                tvMinGoalStatus.setTextColor("#4CAF50".toColorInt())
            }
        } else {
            tvMinGoalStatus.text = getString(R.string.no_min_goal_set)
        }

        if (maxGoal < 999999 && maxGoal > 0) {
            if (currentTotal > maxGoal) {
                val exceeded = String.format(Locale.US, "%.2f", currentTotal - maxGoal)
                tvMaxGoalStatus.text = getString(R.string.maximum_goal_alert, exceeded)
                tvMaxGoalStatus.setTextColor("#F44336".toColorInt())
            } else {
                val remaining = String.format(Locale.US, "%.2f", maxGoal - currentTotal)
                val maxGoalStr = String.format(Locale.US, "%.2f", maxGoal)
                tvMaxGoalStatus.text = getString(R.string.maximum_goal_on_track, remaining, maxGoalStr)
                tvMaxGoalStatus.setTextColor("#4CAF50".toColorInt())
            }
        } else {
            tvMaxGoalStatus.text = getString(R.string.no_max_goal_set)
        }
    }

    private fun setupClickListeners() {
        btnSaveGoals.setOnClickListener {
            val minText = etMinGoal.text.toString().trim()
            val maxText = etMaxGoal.text.toString().trim()

            val minGoal = if (minText.isEmpty()) 0.0 else minText.toDoubleOrNull() ?: 0.0
            val maxGoal = if (maxText.isEmpty()) 999999.0 else maxText.toDoubleOrNull() ?: 999999.0

            AppData.saveGoals(minGoal, maxGoal, this)
            loadAndDisplayReport()
            Toast.makeText(this, getString(R.string.goals_saved), Toast.LENGTH_SHORT).show()
        }

        btnCheckStatus.setOnClickListener {
            val currentTotal = AppData.getCurrentMonthTotal(this)
            val minGoal = AppData.getMinGoal(this)
            val maxGoal = AppData.getMaxGoal(this)

            val message = when {
                currentTotal < minGoal && minGoal > 0 -> {
                    val remaining = String.format(Locale.US, "%.2f", minGoal - currentTotal)
                    getString(R.string.need_to_spend_more, remaining)
                }
                currentTotal > maxGoal && maxGoal < 999999 -> {
                    val exceeded = String.format(Locale.US, "%.2f", currentTotal - maxGoal)
                    getString(R.string.exceeded_budget, exceeded)
                }
                else -> getString(R.string.on_track_message)
            }

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

        btnViewExpenses.setOnClickListener {
            startActivity(Intent(this, expensesList::class.java))
        }
    }
}