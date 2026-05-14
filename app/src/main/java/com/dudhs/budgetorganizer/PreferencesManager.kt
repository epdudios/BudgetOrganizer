package com.dudhs.budgetorganizer

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)

    // Salary overrides the previous value
    fun getSalary(): Float = prefs.getFloat("salary", 1500f)
    fun setSalary(value: Float) = prefs.edit().putFloat("salary", value).apply()

    fun getMonthlyTarget(): Float = prefs.getFloat("monthly_target", 0f)
    fun setMonthlyTarget(value: Float) = prefs.edit().putFloat("monthly_target", value).apply()


    // Sudden income/expense add to a running tally
    fun getSuddenIncome(): Float = prefs.getFloat("sudden_income", 0f)
    fun addSuddenIncome(value: Float) {
        prefs.edit().putFloat("sudden_income", getSuddenIncome() + value).apply()
    }

    fun getSuddenExpense(): Float = prefs.getFloat("sudden_expense", 0f)
    fun addSuddenExpense(value: Float) {
        prefs.edit().putFloat("sudden_expense", getSuddenExpense() + value).apply()
    }

    private fun getCurrentMonthKey(): String {
        val cal = java.util.Calendar.getInstance()
        return "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.MONTH)}"
    }

    fun getTrackedSpentThisMonth(): Float {
        val savedMonth = prefs.getString("tracked_month", "")
        if (savedMonth != getCurrentMonthKey()) {
            return 0f
        }
        return prefs.getFloat("tracked_spent", 0f)
    }

    fun setTrackedSpentThisMonth(value: Float) {
        prefs.edit()
            .putFloat("tracked_spent", value)
            .putString("tracked_month", getCurrentMonthKey())
            .apply()
    }

    fun addTrackedExpense(amount: Float) {
        val current = getTrackedSpentThisMonth()
        setTrackedSpentThisMonth(current + amount)
    }


    fun isHalfwayWarningSent(): Boolean {
        val savedMonth = prefs.getString("tracked_month", "")
        if (savedMonth != getCurrentMonthKey()) return false
        return prefs.getBoolean("warning_sent", false)
    }

    fun setHalfwayWarningSent(sent: Boolean) {
        prefs.edit().putBoolean("warning_sent", sent).apply()
    }
    fun getChartStyle(): String {
        return prefs.getString("chart_style", "PIE") ?: "PIE"
    }

    fun setChartStyle(styleName: String) {
        prefs.edit().putString("chart_style", styleName).apply()
    }
}