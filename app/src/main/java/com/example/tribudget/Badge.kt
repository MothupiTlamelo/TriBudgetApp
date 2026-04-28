package com.example.tribudget

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val requirement: BadgeRequirement,
    var isEarned: Boolean = false,
    var earnedDate: String = ""
)

enum class BadgeRequirement(val type: String) {
    STREAK_7("7-Day Streak"),
    STREAK_30("30-Day Streak"),
    CATEGORY_CRUSHER("Category Crusher"),
    PHOTO_FINISHER("Photo Finisher"),
    BUDGET_MASTER("Budget Master"),
    EARLY_BIRD("Early Bird"),
    NIGHT_OWL("Night Owl")
}

object Badges {
    val allBadges = listOf(
        Badge(
            id = "streak_7",
            name = "7-Day Streak",
            description = "Logged expenses for 7 consecutive days",
            emoji = "🔥",
            requirement = BadgeRequirement.STREAK_7
        ),
        Badge(
            id = "streak_30",
            name = "30-Day Streak",
            description = "Logged expenses for 30 consecutive days",
            emoji = "🏆",
            requirement = BadgeRequirement.STREAK_30
        ),
        Badge(
            id = "category_crusher",
            name = "Category Crusher",
            description = "Stayed under budget in all categories for a month",
            emoji = "📊",
            requirement = BadgeRequirement.CATEGORY_CRUSHER
        ),
        Badge(
            id = "photo_finisher",
            name = "Photo Finisher",
            description = "Attached photos to 10 consecutive expenses",
            emoji = "📷",
            requirement = BadgeRequirement.PHOTO_FINISHER
        ),
        Badge(
            id = "budget_master",
            name = "Budget Master",
            description = "Reached Financial Athlete level",
            emoji = "👑",
            requirement = BadgeRequirement.BUDGET_MASTER
        ),
        Badge(
            id = "early_bird",
            name = "Early Bird",
            description = "Logged an expense before 9 AM",
            emoji = "🌅",
            requirement = BadgeRequirement.EARLY_BIRD
        ),
        Badge(
            id = "night_owl",
            name = "Night Owl",
            description = "Logged an expense after 11 PM",
            emoji = "🦉",
            requirement = BadgeRequirement.NIGHT_OWL
        )
    )
}