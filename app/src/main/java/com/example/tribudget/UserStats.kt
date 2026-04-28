package com.example.tribudget

enum class FitnessLevel(val level: Int, val title: String, val minStreak: Int, val minExpenses: Int) {
    SAVER(1, "Saver", 3, 5),
    BUDGETER(2, "Budgeter", 7, 20),
    STRATEGIST(3, "Strategist", 14, 50),
    FINANCIAL_ATHLETE(4, "Financial Athlete", 30, 100),
    BUDGET_MASTER(5, "Budget Master", 60, 200)
}

data class UserStats(
    var currentLevel: FitnessLevel = FitnessLevel.SAVER,
    var currentStreak: Int = 0,
    var longestStreak: Int = 0,
    var totalExpensesLogged: Int = 0,
    var totalPhotosAttached: Int = 0,
    var categoriesUnderBudget: Int = 0,
    var experiencePoints: Int = 0,
    var unlockedFeatures: MutableList<String> = mutableListOf()
) {
    fun calculateLevel(): FitnessLevel {
        return when {
            totalExpensesLogged >= FitnessLevel.BUDGET_MASTER.minExpenses &&
                    longestStreak >= FitnessLevel.BUDGET_MASTER.minStreak -> FitnessLevel.BUDGET_MASTER

            totalExpensesLogged >= FitnessLevel.FINANCIAL_ATHLETE.minExpenses &&
                    longestStreak >= FitnessLevel.FINANCIAL_ATHLETE.minStreak -> FitnessLevel.FINANCIAL_ATHLETE

            totalExpensesLogged >= FitnessLevel.STRATEGIST.minExpenses &&
                    longestStreak >= FitnessLevel.STRATEGIST.minStreak -> FitnessLevel.STRATEGIST

            totalExpensesLogged >= FitnessLevel.BUDGETER.minExpenses &&
                    longestStreak >= FitnessLevel.BUDGETER.minStreak -> FitnessLevel.BUDGETER

            else -> FitnessLevel.SAVER
        }
    }

    fun addExperience(points: Int) {
        experiencePoints += points
        val newLevel = calculateLevel()
        if (newLevel.level > currentLevel.level) {
            currentLevel = newLevel
            unlockFeaturesForLevel()
        }
    }

    private fun unlockFeaturesForLevel() {
        when (currentLevel) {
            FitnessLevel.BUDGETER -> unlockedFeatures.add("receipt_scanner")
            FitnessLevel.STRATEGIST -> unlockedFeatures.add("predictive_insights")
            FitnessLevel.FINANCIAL_ATHLETE -> unlockedFeatures.add("collaborative_budgeting")
            FitnessLevel.BUDGET_MASTER -> unlockedFeatures.add("advanced_reports")
            else -> { /* No new features */ }
        }
    }
}