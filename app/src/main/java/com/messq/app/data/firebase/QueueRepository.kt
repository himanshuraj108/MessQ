package com.messq.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class QueueStatusData(
    val messId: String = "main",
    val currentCount: Int = 0,
    val maxCapacity: Int = 200,
    val waitTimeMinutes: Int = 0
)

class QueueRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getQueueStatus(messId: String = "main"): QueueStatusData {
        return try {
            val doc = db.collection("queue_status").document(messId).get().await()
            if (doc.exists()) {
                QueueStatusData(
                    messId = messId,
                    currentCount = (doc.getLong("currentCount") ?: 0).toInt(),
                    maxCapacity = (doc.getLong("maxCapacity") ?: 200).toInt(),
                    waitTimeMinutes = (doc.getLong("waitTimeMinutes") ?: 0).toInt()
                )
            } else {
                seedQueueIfEmpty(messId)
                QueueStatusData(messId = messId, currentCount = 45, maxCapacity = 200, waitTimeMinutes = 5)
            }
        } catch (e: Exception) {
            QueueStatusData(messId = messId, currentCount = 45, maxCapacity = 200, waitTimeMinutes = 5)
        }
    }

    private suspend fun seedQueueIfEmpty(messId: String) {
        try {
            db.collection("queue_status").document(messId).set(
                mapOf("currentCount" to 45, "maxCapacity" to 200, "waitTimeMinutes" to 5)
            ).await()
        } catch (e: Exception) { }
    }

    suspend fun getAvailableSlots(date: String): List<String> {
        return listOf(
            "08:00 - 08:30 AM",
            "08:30 - 09:00 AM",
            "09:00 - 09:30 AM",
            "12:00 - 12:30 PM",
            "12:30 - 01:00 PM",
            "01:00 - 01:30 PM",
            "07:00 - 07:30 PM",
            "07:30 - 08:00 PM"
        )
    }
}
