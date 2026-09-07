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
    // --- REFRESH LOGIC ---
    fun isManualRefreshEnabled(): Boolean = prefs.getBoolean("manual_refresh", false)

    fun setManualRefreshEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("manual_refresh", enabled).apply()
    }

    fun performManualReset() {
        prefs.edit()
            .putFloat("sudden_income", 0f)
            .putFloat("sudden_expense", 0f)
            .putFloat("tracked_spent", 0f)
            .putBoolean("warning_sent", false)
            .putLong("manual_reset_timestamp", System.currentTimeMillis())
            .apply()
    }

    fun getEffectiveStartMillis(): Long {
        return if (isManualRefreshEnabled()) {
            prefs.getLong("manual_reset_timestamp", 0L)
        } else {
            java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.DAY_OF_MONTH, 1)
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
    fun getLastNotifiedTier(): Int {
        return prefs.getInt("last_notified_tier", 0)
    }

    fun setLastNotifiedTier(tier: Int) {
        prefs.edit().putInt("last_notified_tier", tier).apply()
    }

    fun getManualBalanceAdjustment(): Float = prefs.getFloat("balance_adjustment", 0f)

    fun setBalanceToTarget(targetValue: Float, currentCalculated: Float) {
        val delta = targetValue - currentCalculated
        prefs.edit().putFloat("balance_adjustment", getManualBalanceAdjustment() + delta).apply()
    }

    fun getAppTheme(): String = prefs.getString("app_theme", "LIGHT") ?: "LIGHT"
    fun setAppTheme(themeName: String) {
        prefs.edit().putString("app_theme", themeName).apply()
    }

    fun getManualMemberships(): Set<String> =
        prefs.getStringSet("manual_memberships", emptySet()) ?: emptySet()

    fun addManualMembership(merchant: String) {
        val updated = getManualMemberships() + merchant.trim()
        prefs.edit().putStringSet("manual_memberships", updated).apply()
    }

    fun removeManualMembership(merchant: String) {
        val updated = getManualMemberships() - merchant.trim()
        prefs.edit().putStringSet("manual_memberships", updated).apply()
    }

    fun getIgnoredMemberships(): Set<String> =
        prefs.getStringSet("ignored_memberships", emptySet()) ?: emptySet()

    fun addIgnoredMembership(merchant: String) {
        val updated = getIgnoredMemberships() + merchant.trim().uppercase()
        prefs.edit().putStringSet("ignored_memberships", updated).apply()
    }

    // --- Onboarding & banks ---
    fun isOnboardingComplete(): Boolean = prefs.getBoolean("onboarding_complete", false)
    fun setOnboardingComplete(complete: Boolean) {
        prefs.edit().putBoolean("onboarding_complete", complete).apply()
    }

    fun getBankNames(): List<String> {
        val raw = prefs.getString("bank_names", "ALPHA") ?: "ALPHA"
        return raw.split(",").map { it.trim() }.filter { it.isNotBlank() }
    }

    fun addBankName(name: String) {
        val updated = (getBankNames() + name.trim()).distinct()
        prefs.edit().putString("bank_names", updated.joinToString(",")).apply()
    }

    fun setBankNames(names: List<String>) {
        prefs.edit().putString("bank_names", names.joinToString(",")).apply()
    }
}