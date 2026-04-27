package com.example.tribudget

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class GamificationManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("gamification", Context.MODE_PRIVATE)
    private var userStats: UserStats
    private var earnedBadges: MutableList<Badge>

    init {
        userStats = loadUserStats()
        earnedBadges = loadBadges()
    }

    private fun loadUserStats(): UserStats {
        val json = prefs.getString("user_stats", "") ?: ""
        return if (json.isNotEmpty()) {
            Gson().fromJson(json, UserStats::class.java)
        } else {
            UserStats()
        }
    }

    private fun loadBadges(): MutableList<Badge> {
        val json = prefs.getString("badges", "") ?: ""
        val type = object : TypeToken<MutableList<Badge>>() {}.type
        return if (json.isNotEmpty()) {
            Gson().fromJson(json, type)
        } else {
            Badges.allBadges.toMutableList()
        }
    }

    fun recordExpenseEntry(hasPhoto: Boolean = false, hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        userStats.totalExpensesLogged++

        // Check for Early Bird and Night Owl badges
        if (hourOfDay < 9) {
            awardBadge("early_bird")
        }
        if (hourOfDay >= 23) {
            awardBadge("night_owl")
        }

        // Update streak - FIXED with proper null handling
        val lastEntryDate = prefs.getString("last_entry_date", "") ?: ""
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        // FIXED Line 34: Proper null safety
        if (lastEntryDate.isNotEmpty() && lastEntryDate == getYesterdayDate()) {
            userStats.currentStreak++
        } else if (lastEntryDate.isNotEmpty() && lastEntryDate != today) {
            userStats.currentStreak = 1
        } else if (lastEntryDate.isEmpty()) {
            userStats.currentStreak = 1
        }

        if (userStats.currentStreak > userStats.longestStreak) {
            userStats.longestStreak = userStats.currentStreak
        }

        // Track photos
        if (hasPhoto) {
            userStats.totalPhotosAttached++
        }

        // Add experience points
        var xpGained = 10
        if (hasPhoto) xpGained += 5
        if (userStats.currentStreak % 7 == 0) xpGained += 20

        userStats.addExperience(xpGained)

        // Update last entry date - FIXED Line 57 using KTX extension
        prefs.edit {
            putString("last_entry_date", today)
        }

        // Check and award badges
        checkBadges()
        saveStats()
    }

    private fun getYesterdayDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
    }

    private fun checkBadges() {
        // Check 7-day streak
        if (userStats.currentStreak >= 7) {
            awardBadge("streak_7")
        }

        // Check 30-day streak
        if (userStats.currentStreak >= 30) {
            awardBadge("streak_30")
        }

        // Check photo finisher
        if (userStats.totalPhotosAttached >= 10) {
            awardBadge("photo_finisher")
        }

        // Check budget master
        if (userStats.currentLevel == FitnessLevel.BUDGET_MASTER) {
            awardBadge("budget_master")
        }
    }

    private fun awardBadge(badgeId: String) {
        val badge = earnedBadges.find { it.id == badgeId }
        if (badge != null && !badge.isEarned) {
            badge.isEarned = true
            badge.earnedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            saveBadges()
        }
    }

    fun getUserStats(): UserStats = userStats

    fun getEarnedBadges(): List<Badge> = earnedBadges.filter { it.isEarned }

    fun getAllBadges(): List<Badge> = earnedBadges

    fun getNextLevel(): FitnessLevel? {
        val current = userStats.currentLevel
        return when (current) {
            FitnessLevel.SAVER -> FitnessLevel.BUDGETER
            FitnessLevel.BUDGETER -> FitnessLevel.STRATEGIST
            FitnessLevel.STRATEGIST -> FitnessLevel.FINANCIAL_ATHLETE
            FitnessLevel.FINANCIAL_ATHLETE -> FitnessLevel.BUDGET_MASTER
            else -> null
        }
    }

    fun getProgressToNextLevel(): Int {
        val next = getNextLevel() ?: return 100
        val currentExpenses = userStats.totalExpensesLogged
        val requiredExpenses = next.minExpenses
        val targetExpenses = requiredExpenses - FitnessLevel.SAVER.minExpenses
        val achievedExpenses = currentExpenses - FitnessLevel.SAVER.minExpenses

        if (targetExpenses <= 0) return 100
        return (achievedExpenses.toFloat() / targetExpenses * 100).toInt().coerceIn(0, 100)
    }

    private fun saveStats() {
        val json = Gson().toJson(userStats)
        prefs.edit {
            putString("user_stats", json)
        }
    }

    private fun saveBadges() {
        val json = Gson().toJson(earnedBadges)
        prefs.edit {
            putString("badges", json)
        }
    }
}