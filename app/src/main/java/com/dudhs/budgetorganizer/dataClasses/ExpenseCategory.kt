package com.dudhs.budgetorganizer.dataClasses

import androidx.compose.ui.graphics.Color
import com.dudhs.budgetorganizer.ui.theme.*

enum class ExpenseCategory(
    val label: String,
    val color: Color,
    val keywords: List<String>
) {
    SUPERMARKET("Supermarket", ColorSupermarket, listOf("lidl", "sklavenitis", "alphamega", "metro", "4 ways", "urban market")),
    CLOTHING("Clothing", ColorClothing, listOf("zara", "h&m", "bershka", "pull&bear", "sprider")),
    FUEL("Fuel", ColorFuel, listOf("petrolina", "eko ", "shell", "esso", "lukoil")),
    FOOD_OUT("Eating Out", ColorFoodOut, listOf("foody", "wolt", "pizza", "souvlaki", "cafe", "coffee", "starbucks", "costa", "kfc", "mcdonald")),
    SUBSCRIPTIONS("Subscriptions", ColorSubscriptions, listOf("google one", "netflix", "spotify", "youtube", "playstation", "cablenet", "cyta", "epic", "primetel")),
    ENTERTAINMENT("Entertainment", ColorEntertainment, listOf("cinema", "playpark", "bowling")),
    PHARMACY_HEALTH("Health", ColorHealth, listOf("pharmacy", "clinic", "hospital")),
    CONFECTIONERY("Sweets", ColorConfectionery, listOf("pralina", "confectionery", "zorbas", "bakery")),
    TRANSFERS("Transfers", ColorTransfers, listOf()),
    OTHER("Other", ColorOther, emptyList());

    companion object {
        fun fromMerchant(merchant: String): ExpenseCategory {
            val lower = merchant.lowercase()
            return values().firstOrNull { category ->
                category.keywords.any { keyword -> lower.contains(keyword) }
            } ?: OTHER
        }
    }
}