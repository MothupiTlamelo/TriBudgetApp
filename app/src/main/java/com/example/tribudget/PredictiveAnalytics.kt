package com.example.tribudget

import android.content.Context
import java.util.*

class PredictiveAnalytics {

    fun predictMonthlySpending(expenses: List<ExpenseEntity>, context: Context? = null): SpendingPrediction {
        if (expenses.isEmpty()) {
            return SpendingPrediction(
                predictedTotal = 0.0,
                confidence = 0f,
                dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                message = "Not enough data for prediction. Add more expenses to see insights!"
            )
        }

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        //group expenses by day
        val dailySpending = mutableMapOf<Int, Double>()
        expenses.filter { isCurrentMonth(it.dateString) }.forEach { expense ->
            val parts = expense.dateString.split("-")
            if (parts.size == 3) {
                val day = parts[2].toInt()
                dailySpending[day] = (dailySpending[day] ?: 0.0) + expense.amount
            }
        }

        //if no expenses this month
        if (dailySpending.isEmpty()) {
            return SpendingPrediction(
                predictedTotal = 0.0,
                confidence = 0.2f,
                dayOfMonth = currentDay,
                message = "No expenses recorded yet this month. Start tracking to get predictions!"
            )
        }

        //calculate average daily spending
        val avgDailySpending = dailySpending.values.average()

        //calculate trend (last 7 days vs previous 7 days)
        val last7Days = dailySpending.filter { it.key > currentDay - 7 }.values.average()
        val previous7Days = dailySpending.filter { it.key in (currentDay - 14) until (currentDay - 7) }.values.average()

        val trendMultiplier = if (previous7Days > 0) last7Days / previous7Days else 1.0

        //predict remaining days
        val remainingDays = daysInMonth - currentDay
        val predictedRemaining = avgDailySpending * remainingDays * trendMultiplier
        val totalSpent = dailySpending.values.sum()
        val predictedTotal = totalSpent + predictedRemaining

        //get goals if context is provided
        var maxGoal = 999999.0
        var minGoal = 0.0
        context?.let {
            maxGoal = AppData.getMaxGoal(it)
            minGoal = AppData.getMinGoal(it)
        }

        //generate insight message with proper locale
        val message = when {
            predictedTotal > maxGoal && maxGoal < 999999 ->
                String.format(Locale.US, "⚠️ You're on track to exceed your monthly budget by R%.2f", predictedTotal - maxGoal)
            predictedTotal < minGoal && minGoal > 0 ->
                String.format(Locale.US, "📈 You need to spend R%.2f more to reach your minimum goal", minGoal - predictedTotal)
            trendMultiplier > 1.2 ->
                String.format(Locale.US, "📊 Your spending is increasing %.0f%% faster. Consider reviewing your budget!", (trendMultiplier - 1) * 100)
            else ->
                "✅ You're on track! Keep up the good financial habits."
        }

        return SpendingPrediction(
            predictedTotal = predictedTotal,
            confidence = calculateConfidence(expenses.size, currentDay),
            dayOfMonth = currentDay,
            message = message
        )
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

    private fun calculateConfidence(expenseCount: Int, currentDay: Int): Float {
        //more data = higher confidence
        val maxDataPoints = 30
        val dataConfidence = (expenseCount.toFloat() / maxDataPoints).coerceIn(0f, 1f)

        //later in month = higher confidence
        val daysInMonth = 30
        val dayConfidence = (currentDay.toFloat() / daysInMonth).coerceIn(0f, 1f)

        return (dataConfidence * 0.6f + dayConfidence * 0.4f)
    }
}

data class SpendingPrediction(
    val predictedTotal: Double,
    val confidence: Float,
    val dayOfMonth: Int,
    val message: String
)