package com.messq.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.messq.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFF9500), OrangePrimary, Color(0xFFE85D00))
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .offset(x = (-60).dp, y = (-60).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                )
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 50.dp, y = 50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.LockReset,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Reset Password",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We'll send you a reset link",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
            ) {
                AnimatedContent(
                    targetState = isSent,
                    transitionSpec = {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(300))
                    },
                    label = "resetContent"
                ) { sent ->
                    if (!sent) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)
                            ) {
                                Text(
                                    text = "Enter your email",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "We'll send a password reset link to your registered email address.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9E9E9E),
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF5F6FA))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Outlined.Email,
                                            contentDescription = null,
                                            tint = Color(0xFFBDBDBD),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        BasicTextField(
                                            value = email,
                                            onValueChange = { newVal ->
                                                email = newVal
                                                errorMsg = ""
                                            },
                                            modifier = Modifier.weight(1f),
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                fontSize = 15.sp,
                                                color = Color(0xFF1A1A1A)
                                            ),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                            decorationBox = { innerTextField ->
                                                if (email.isEmpty()) {
                                                    Text(
                                                        "student@messq.app",
                                                        color = Color(0xFFBDBDBD),
                                                        fontSize = 15.sp
                                                    )
                                                }
                                                innerTextField()
                                            },
                                            singleLine = true
                                        )
                                    }
                                }

                                if (errorMsg.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = errorMsg,
                                        fontSize = 13.sp,
                                        color = Color(0xFFE53935),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        if (email.isBlank()) {
                                            errorMsg = "Please enter your email address"
                                            return@Button
                                        }
                                        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                            errorMsg = "Please enter a valid email address"
                                            return@Button
                                        }
                                        isLoading = true
                                        FirebaseAuth.getInstance()
                                            .sendPasswordResetEmail(email.trim())
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                if (task.isSuccessful) {
                                                    isSent = true
                                                } else {
                                                    errorMsg = task.exception?.message
                                                        ?: "Failed to send reset email"
                                                }
                                            }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    enabled = !isLoading,
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Send Reset Link",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Remember your password?  ",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9E9E9E)
                                    )
                                    Text(
                                        text = "Sign In",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary,
                                        modifier = Modifier.clickable { onBack() }
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 36.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.MarkEmailRead,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Text(
                                    text = "Email Sent!",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1A1A1A)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "We've sent a password reset link to",
                                    fontSize = 14.sp,
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = email,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = OrangePrimary,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Check your inbox and follow the link to reset your password.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF9E9E9E),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                ) {
                                    Text(
                                        text = "Back to Sign In",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Didn't receive it? Check spam or resend",
                                    fontSize = 13.sp,
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.clickable {
                                        isSent = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
