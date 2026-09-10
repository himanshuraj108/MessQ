package com.messq.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.messq.app.data.MealCategory
import com.messq.app.data.MenuItem
import kotlinx.coroutines.tasks.await

class MenuRepository {
    private val db = FirebaseFirestore.getInstance()

    private fun categoryFromString(s: String): MealCategory = when (s.lowercase()) {
        "lunch" -> MealCategory.LUNCH
        "dinner" -> MealCategory.DINNER
        "snacks" -> MealCategory.SNACKS
        else -> MealCategory.BREAKFAST
    }

    suspend fun getMenuItems(): List<MenuItem> {
        return try {
            val snapshot = db.collection("menu_items").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    MenuItem(
                        id = doc.getLong("id")?.toInt() ?: 0,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = (doc.getLong("price") ?: 0).toInt(),
                        isVeg = doc.getBoolean("isVeg") ?: true,
                        quantity = doc.getString("quantity") ?: "1 plate",
                        category = categoryFromString(doc.getString("category") ?: "breakfast"),
                        colorHex = doc.getLong("colorHex") ?: 0xFFE8D5A3
                    )
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun seedMenuIfEmpty() {
        try {
            val existing = db.collection("menu_items").limit(1).get().await()
            if (!existing.isEmpty) return
            val items = listOf(
                mapOf("id" to 1L, "name" to "Aloo Paratha", "description" to "Classic stuffed paratha served with curd", "category" to "breakfast", "price" to 30L, "isVeg" to true, "quantity" to "2 pcs", "colorHex" to 0xFFE8D5A3L),
                mapOf("id" to 2L, "name" to "Poha", "description" to "Light flattened rice with peanuts", "category" to "breakfast", "price" to 20L, "isVeg" to true, "quantity" to "1 plate", "colorHex" to 0xFFFFF3C4L),
                mapOf("id" to 3L, "name" to "Idli Sambar", "description" to "Steamed idlis with hot sambar", "category" to "breakfast", "price" to 25L, "isVeg" to true, "quantity" to "4 pcs", "colorHex" to 0xFFE8E8E8L),
                mapOf("id" to 4L, "name" to "Masala Dosa", "description" to "Crispy dosa with spiced potato filling", "category" to "breakfast", "price" to 35L, "isVeg" to true, "quantity" to "1 pc", "colorHex" to 0xFFD4A853L),
                mapOf("id" to 5L, "name" to "Filter Coffee", "description" to "South Indian filter coffee", "category" to "breakfast", "price" to 15L, "isVeg" to true, "quantity" to "1 cup", "colorHex" to 0xFF6B3A2AL),
                mapOf("id" to 6L, "name" to "Dal Rice", "description" to "Yellow dal with steamed rice", "category" to "lunch", "price" to 45L, "isVeg" to true, "quantity" to "1 plate", "colorHex" to 0xFFE8C98AL),
                mapOf("id" to 7L, "name" to "Paneer Butter Masala", "description" to "Rich paneer in buttery tomato gravy", "category" to "lunch", "price" to 60L, "isVeg" to true, "quantity" to "1 plate", "colorHex" to 0xFFFF8C42L),
                mapOf("id" to 8L, "name" to "Chole Bhature", "description" to "Spiced chickpeas with fried bread", "category" to "lunch", "price" to 50L, "isVeg" to true, "quantity" to "2 pcs", "colorHex" to 0xFFC8A96EL),
                mapOf("id" to 9L, "name" to "Chicken Curry", "description" to "Spicy chicken in onion-tomato masala", "category" to "lunch", "price" to 80L, "isVeg" to false, "quantity" to "1 plate", "colorHex" to 0xFFB5451BL),
                mapOf("id" to 10L, "name" to "Rajma Chawal", "description" to "Red kidney bean curry with rice", "category" to "dinner", "price" to 50L, "isVeg" to true, "quantity" to "1 plate", "colorHex" to 0xFF8B4513L),
                mapOf("id" to 11L, "name" to "Roti Sabzi", "description" to "Whole wheat rotis with seasonal vegetable", "category" to "dinner", "price" to 40L, "isVeg" to true, "quantity" to "4 pcs", "colorHex" to 0xFF90C67CL),
                mapOf("id" to 12L, "name" to "Fried Rice", "description" to "Wok-tossed vegetable fried rice", "category" to "dinner", "price" to 55L, "isVeg" to true, "quantity" to "1 plate", "colorHex" to 0xFFFFF3C4L),
                mapOf("id" to 13L, "name" to "Samosa", "description" to "Crispy fried potato-filled pastry", "category" to "snacks", "price" to 10L, "isVeg" to true, "quantity" to "2 pcs", "colorHex" to 0xFFD4A853L),
                mapOf("id" to 14L, "name" to "Vada Pav", "description" to "Mumbai-style potato fritter in a bun", "category" to "snacks", "price" to 15L, "isVeg" to true, "quantity" to "1 pc", "colorHex" to 0xFFE8C98AL),
                mapOf("id" to 15L, "name" to "Cold Coffee", "description" to "Chilled blended coffee with ice cream", "category" to "snacks", "price" to 30L, "isVeg" to true, "quantity" to "1 glass", "colorHex" to 0xFF6B3A2AL)
            )
            items.forEach { db.collection("menu_items").add(it).await() }
        } catch (e: Exception) { }
    }
}
