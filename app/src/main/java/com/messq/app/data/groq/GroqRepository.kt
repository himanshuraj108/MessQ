package com.messq.app.data.groq

import com.messq.app.Constants
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class GroqMessage(val role: String, val content: String)

class GroqRepository {
    private val client = OkHttpClient()

    suspend fun chat(messages: List<GroqMessage>): String {
        return suspendCoroutine { continuation ->
            val messagesArray = JSONArray()
            messagesArray.put(JSONObject().apply {
                put("role", "system")
                put("content", Constants.MESSQ_SYSTEM_PROMPT)
            })
            messages.takeLast(10).forEach { msg ->
                messagesArray.put(JSONObject().apply {
                    put("role", msg.role)
                    put("content", msg.content)
                })
            }
            val body = JSONObject().apply {
                put("model", Constants.GROQ_MODEL)
                put("messages", messagesArray)
                put("max_tokens", 200)
                put("temperature", 0.7)
            }
            val request = Request.Builder()
                .url("${Constants.GROQ_BASE_URL}chat/completions")
                .addHeader("Authorization", "Bearer ${Constants.GROQ_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resume("Sorry, I am unable to connect right now. Please try again.")
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val json = JSONObject(response.body?.string() ?: "")
                        val content = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        continuation.resume(content.trim())
                    } catch (e: Exception) {
                        continuation.resume("I could not process that. Could you rephrase your question?")
                    }
                }
            })
        }
    }
}
