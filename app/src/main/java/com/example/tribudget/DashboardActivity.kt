package com.example.tribudget

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var gamificationManager: GamificationManager
    private lateinit var predictiveAnalytics: PredictiveAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        gamificationManager = GamificationManager(this)
        predictiveAnalytics = PredictiveAnalytics()

        setupBackButton()
        loadFitnessLevel()
        loadBudgetStatus()
        loadPredictiveInsights()
        loadCategoryBreakdown()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    private fun loadFitnessLevel() {
        val stats = gamificationManager.getUserStats()
        val tvFitnessLevel = findViewById<TextView>(R.id.tvFitnessLevel)
        val progressBar = findViewById<ProgressBar>(R.id.progressToNextLevel)
        val tvStreakInfo = findViewById<TextView>(R.id.tvStreakInfo)

        tvFitnessLevel.text = stats.currentLevel.title

        val progress = gamificationManager.getProgressToNextLevel()
        progressBar.progress = progress

        val streakText = String.format(Locale.US, "🔥 %d day streak | 📝 %d expenses logged",
            stats.currentStreak, stats.totalExpensesLogged)
        tvStreakInfo.text = streakText

        val color = when (stats.currentLevel) {
            FitnessLevel.SAVER -> "#9E9E9E".toColorInt()
            FitnessLevel.BUDGETER -> "#4CAF50".toColorInt()
            FitnessLevel.STRATEGIST -> "#2196F3".toColorInt()
            FitnessLevel.FINANCIAL_ATHLETE -> "#FF9800".toColorInt()
            FitnessLevel.BUDGET_MASTER -> "#FFC107".toColorInt()
        }
        tvFitnessLevel.setTextColor(color)
    }

    private fun loadBudgetStatus() {
        val currentTotal = AppData.getCurrentMonthTotal(this)
        val maxGoal = AppData.getMaxGoal(this)
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        val tvBudgetMessage = findViewById<TextView>(R.id.tvBudgetMessage)

        val percentage = if (maxGoal > 0 && maxGoal < 999999) {
            ((currentTotal / maxGoal) * 100).toFloat().coerceIn(0f, 100f)
        } else {
            0f
        }

        val (message, color, chartColor) = when {
            percentage >= 100 -> Triple("🚨 OVER BUDGET!", "#F44336", "#F44336")
            percentage >= 85 -> Triple("⚠️ APPROACHING LIMIT", "#FF9800", "#FF9800")
            else -> Triple("✅ ON TRACK", "#4CAF50", "#4CAF50")
        }

        tvBudgetMessage.text = message
        tvBudgetMessage.setTextColor(color.toColorInt())

        //create pie chart entries
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(percentage, "Spent"))
        entries.add(PieEntry(100f - percentage, "Remaining"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(chartColor.toColorInt(), "#E0E0E0".toColorInt())
        dataSet.valueTextSize = 14f
        dataSet.setDrawValues(true)

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())

        pieChart.data = data
        pieChart.description.isEnabled = false
        // FIXED: Using the correct method names from MPAndroidChart
        pieChart.centerText = String.format(Locale.US, "%.0f%%", percentage)
        pieChart.setCenterTextSize(16f)
        pieChart.holeRadius = 40f
        pieChart.transparentCircleRadius = 45f
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun loadPredictiveInsights() {

        // With context (uses user's goals)
        val prediction = predictiveAnalytics.predictMonthlySpending(AppData.expenseList, this)
        val tvPrediction = findViewById<TextView>(R.id.tvPrediction)

        val predictedText = String.format(Locale.US,
            "📅 Day %d of month\n💰 Projected total: R%.2f\n🎯 %s\nConfidence: %d%%",
            prediction.dayOfMonth,
            prediction.predictedTotal,
            prediction.message,
            (prediction.confidence * 100).toInt()
        )

        tvPrediction.text = predictedText
    }

    private fun loadCategoryBreakdown() {
        val categorySpending = mutableMapOf<String, Double>()

        AppData.expenseList.filter { isCurrentMonth(it.dateString) }.forEach { expense ->
            categorySpending[expense.category] = (categorySpending[expense.category] ?: 0.0) + expense.amount
        }

        val listView = findViewById<ListView>(R.id.categoryBreakdownList)

        if (categorySpending.isEmpty()) {
            val emptyList = listOf("No expenses this month")
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emptyList)
            listView.adapter = adapter
            return
        }

        val items = categorySpending.map { (category, amount) ->
            String.format(Locale.US, "%s: R%.2f", category, amount)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter
    }

    private fun isCurrentMonth(date: String): Boolean {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1

        val parts = date.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            return year == currentYear && month == currentMonth
        }
        return false
    }
}