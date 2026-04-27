package com.example.tribudget

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BadgesActivity : AppCompatActivity() {

    private lateinit var gamificationManager: GamificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        gamificationManager = GamificationManager(this)

        setupBackButton()
        displayBadges()
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayBadges() {
        val allBadges = gamificationManager.getAllBadges()
        val earnedBadges = gamificationManager.getEarnedBadges()

        val tvStats = findViewById<TextView>(R.id.tvBadgeStats)
        tvStats.text = "🏅 Earned ${earnedBadges.size}/${allBadges.size} Badges"

        val listView = findViewById<ListView>(R.id.badgesListView)

        // Use emojis instead of icons
        val badgeStrings = allBadges.map { badge ->
            val status = if (badge.isEarned) "✅" else "🔒"
            val emoji = when (badge.id) {
                "streak_7", "streak_30" -> "🔥"
                "category_crusher" -> "🏆"
                "photo_finisher" -> "📷"
                "budget_master" -> "👑"
                else -> "⭐"
            }
            "$status $emoji ${badge.name}\n   ${badge.description}"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, badgeStrings)
        listView.adapter = adapter
    }
}