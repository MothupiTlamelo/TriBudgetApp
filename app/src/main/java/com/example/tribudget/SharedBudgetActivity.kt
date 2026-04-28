package com.example.tribudget

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SharedBudgetActivity : AppCompatActivity() {

    private lateinit var budgetManager: SharedBudgetManager
    private lateinit var budgetsList: ListView
    private val budgets = mutableListOf<SharedBudgetItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shared_budget)

        budgetManager = SharedBudgetManager(this)

        setupViews()
        loadBudgets()
    }

    private fun setupViews() {
        budgetsList = findViewById(R.id.sharedBudgetsList)

        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddBudget).setOnClickListener {
            showAddBudgetDialog()
        }
    }

    private fun loadBudgets() {
        budgets.clear()
        budgets.addAll(budgetManager.getUserBudgets(AppData.currentUser))

        val adapter = BudgetAdapter(this, budgets)
        budgetsList.adapter = adapter

        budgetsList.setOnItemClickListener { _, _, position, _ ->
            showBudgetDetails(budgets[position])
        }
    }

    private fun showAddBudgetDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_shared_budget, null)
        val etBudgetName = dialogView.findViewById<EditText>(R.id.etBudgetName)
        val etTotalBudget = dialogView.findViewById<EditText>(R.id.etTotalBudget)
        val etMembers = dialogView.findViewById<EditText>(R.id.etMembers)

        AlertDialog.Builder(this)
            .setTitle("Create Shared Budget")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val name = etBudgetName.text.toString().trim()
                val totalBudget = etTotalBudget.text.toString().toDoubleOrNull() ?: 0.0
                val membersText = etMembers.text.toString().trim()
                val members = membersText.lines().map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

                if (name.isNotEmpty() && totalBudget > 0) {
                    members.add(AppData.currentUser)
                    budgetManager.createBudget(name, totalBudget, members.distinct())
                    loadBudgets()
                    Toast.makeText(this, "Budget created!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBudgetDetails(budget: SharedBudgetItem) {
        val spentByMember = budgetManager.getSpendingByMember(budget.id)
        val remaining = budget.totalBudget - budget.currentSpent

        val message = buildString {
            appendLine("📊 ${budget.name}")
            appendLine(String.format(Locale.US, "💰 Total: R%.2f", budget.totalBudget))
            appendLine(String.format(Locale.US, "💸 Spent: R%.2f", budget.currentSpent))
            appendLine(String.format(Locale.US, "✅ Remaining: R%.2f", remaining))
            appendLine("\n👥 Members: ${budget.members.joinToString()}")
            appendLine("\n💳 Spending by member:")
            if (spentByMember.isEmpty()) {
                appendLine("   No expenses yet")
            } else {
                spentByMember.forEach { (member, amount) ->
                    appendLine(String.format(Locale.US, "   %s: R%.2f", member, amount))
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Budget Details")
            .setMessage(message)
            .setPositiveButton("Add Expense") { _, _ ->
                val intent = Intent(this, AddExpense::class.java)
                intent.putExtra("SHARED_BUDGET_ID", budget.id)
                startActivity(intent)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadBudgets()
    }
}

class BudgetAdapter(
    private val context: Context,
    private val budgets: List<SharedBudgetItem>
) : BaseAdapter() {

    override fun getCount(): Int = budgets.size

    override fun getItem(position: Int): Any = budgets[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val budget = budgets[position]
        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = budget.name
        val percentage = if (budget.totalBudget > 0) {
            ((budget.currentSpent / budget.totalBudget) * 100).toInt()
        } else {
            0
        }

        val spentText = String.format(Locale.US, "💰 R%.2f / R%.2f | %d%% used",
            budget.currentSpent, budget.totalBudget, percentage)
        text2.text = spentText

        return view
    }
}