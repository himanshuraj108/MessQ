package com.messq.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.messq.app.data.CartItem
import kotlinx.coroutines.tasks.await

data class OrderData(
    val id: String = "",
    val items: String = "",
    val total: Int = 0,
    val status: String = "Confirmed",
    val slot: String = "",
    val date: String = "",
    val createdAt: Long = 0L
)

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun placeOrder(cartItems: List<CartItem>, slot: String, date: String, total: Int): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
        return try {
            val itemsSummary = cartItems.joinToString(", ") { "${it.menuItem.name} x${it.quantity}" }
            val ref = db.collection("orders").add(
                mapOf(
                    "userId" to uid,
                    "items" to itemsSummary,
                    "total" to total,
                    "status" to "Confirmed",
                    "slot" to slot,
                    "date" to date,
                    "createdAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(ref.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderHistory(): List<OrderData> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val snapshot = db.collection("orders")
                .whereEqualTo("userId", uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(20)
                .get().await()
            snapshot.documents.map { doc ->
                OrderData(
                    id = doc.id,
                    items = doc.getString("items") ?: "",
                    total = (doc.getLong("total") ?: 0).toInt(),
                    status = doc.getString("status") ?: "Confirmed",
                    slot = doc.getString("slot") ?: "",
                    date = doc.getString("date") ?: "",
                    createdAt = doc.getLong("createdAt") ?: 0L
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
