package com.messq.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            db.collection("users").document(user.uid).set(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "walletBalance" to 500
                )
            ).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getUserName(): String {
        val uid = auth.currentUser?.uid ?: return "Student"
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.getString("name") ?: "Student"
        } catch (e: Exception) {
            "Student"
        }
    }

    suspend fun getWalletBalance(): Int {
        val uid = auth.currentUser?.uid ?: return 0
        return try {
            val doc = db.collection("users").document(uid).get().await()
            (doc.getLong("walletBalance") ?: 0).toInt()
        } catch (e: Exception) {
            0
        }
    }
}
