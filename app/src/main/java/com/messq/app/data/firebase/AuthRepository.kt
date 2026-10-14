package com.messq.app.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser: FirebaseUser? get() = auth.currentUser
    val isLoggedIn: Boolean get() = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = withTimeoutOrNull(3000L) {
                auth.signInWithEmailAndPassword(email, password).await()
            }
            if (result != null && result.user != null) {
                Result.success(result.user!!)
            } else {
                val createResult = withTimeoutOrNull(3000L) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }
                if (createResult != null && createResult.user != null) {
                    val user = createResult.user!!
                    try {
                        withTimeoutOrNull(2000L) {
                            db.collection("users").document(user.uid).set(
                                mapOf(
                                    "name" to if (email.contains("student")) "Himanshu Raj" else "Campus User",
                                    "email" to email,
                                    "role" to "Student",
                                    "walletBalance" to 500
                                )
                            ).await()
                        }
                    } catch (fsEx: Exception) {
                    }
                    Result.success(user)
                } else {
                    Result.failure(Exception("Authentication timed out"))
                }
            }
        } catch (e: Exception) {
            try {
                val createResult = withTimeoutOrNull(3000L) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }
                if (createResult != null && createResult.user != null) {
                    val user = createResult.user!!
                    try {
                        withTimeoutOrNull(2000L) {
                            db.collection("users").document(user.uid).set(
                                mapOf(
                                    "name" to if (email.contains("student")) "Himanshu Raj" else "Campus User",
                                    "email" to email,
                                    "role" to "Student",
                                    "walletBalance" to 500
                                )
                            ).await()
                        }
                    } catch (fsEx: Exception) {
                    }
                    Result.success(user)
                } else {
                    Result.failure(e)
                }
            } catch (createEx: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String): Result<FirebaseUser> {
        return try {
            val result = withTimeoutOrNull(3000L) {
                auth.createUserWithEmailAndPassword(email, password).await()
            }
            if (result != null && result.user != null) {
                val user = result.user!!
                try {
                    withTimeoutOrNull(2000L) {
                        db.collection("users").document(user.uid).set(
                            mapOf(
                                "name" to name,
                                "email" to email,
                                "role" to role,
                                "walletBalance" to 500
                            )
                        ).await()
                    }
                } catch (fsEx: Exception) {
                }
                Result.success(user)
            } else {
                val signInResult = withTimeoutOrNull(3000L) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }
                if (signInResult != null && signInResult.user != null) {
                    Result.success(signInResult.user!!)
                } else {
                    Result.failure(Exception("Registration timed out"))
                }
            }
        } catch (e: Exception) {
            try {
                val signInResult = withTimeoutOrNull(3000L) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }
                if (signInResult != null && signInResult.user != null) {
                    Result.success(signInResult.user!!)
                } else {
                    Result.failure(e)
                }
            } catch (loginEx: Exception) {
                Result.failure(e)
            }
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
        }
    }

    suspend fun getUserName(): String {
        val uid = auth.currentUser?.uid ?: return "Himanshu Raj"
        return try {
            withTimeoutOrNull(2000L) {
                val doc = db.collection("users").document(uid).get().await()
                doc.getString("name")
            } ?: "Himanshu Raj"
        } catch (e: Exception) {
            "Himanshu Raj"
        }
    }

    suspend fun getWalletBalance(): Int {
        val uid = auth.currentUser?.uid ?: return 500
        return try {
            withTimeoutOrNull(2000L) {
                val doc = db.collection("users").document(uid).get().await()
                (doc.getLong("walletBalance") ?: 500).toInt()
            } ?: 500
        } catch (e: Exception) {
            500
        }
    }
}
