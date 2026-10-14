package com.messq.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.messq.app.ui.components.MessQBrandLogo
import com.messq.app.ui.theme.*

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFC2410C),
                                OrangeDark,
                                OrangePrimary,
                                Color(0xFFFB923C)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(top = 10.dp, bottom = 44.dp, start = 16.dp, end = 16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.TopStart)
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
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MessQBrandLogo(
                        badgeSize = 68.dp,
                        showTagline = false,
                        isDarkThemeOnHeader = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Password Recovery",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "We will send an instant recovery email",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .padding(horizontal = 18.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    AnimatedContent(
                        targetState = isSent,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(300)) },
                        label = "resetAnimatedState"
                    ) { sent ->
                        if (!sent) {
                            Column(
                                modifier = Modifier.padding(22.dp)
                            ) {
                                Text(
                                    text = "Reset Your Password",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enter your registered campus email address and we'll send you a password reset link.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        errorMsg = ""
                                    },
                                    label = { Text("Registered Email Address") },
                                    leadingIcon = {
                                        Icon(Icons.Outlined.Email, contentDescription = null, tint = OrangePrimary)
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = WarmWhite,
                                        unfocusedContainerColor = WarmWhite,
                                        focusedBorderColor = OrangePrimary,
                                        unfocusedBorderColor = Divider
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (errorMsg.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = errorMsg,
                                        fontSize = 12.sp,
                                        color = ErrorRed,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(22.dp))

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
                                                    errorMsg = task.exception?.message ?: "Failed to send reset link"
                                                }
                                            }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Color.White,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Text(
                                            text = "Send Recovery Email",
                                            fontSize = 15.sp,
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
                                        text = "Remember your password? ",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "Sign In",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary,
                                        modifier = Modifier.clickable(onClick = onBack)
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.padding(26.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.MarkEmailRead,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Reset Link Dispatched",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "We have sent password instructions to",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = email,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                                ) {
                                    Text("Back to Sign In", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Resend link",
                                    fontSize = 12.sp,
                                    color = OrangePrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { isSent = false }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
