package com.messq.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.MessQData
import com.messq.app.navigation.CartState
import com.messq.app.ui.components.*
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.AuthViewModel

@Composable
fun ProfileScreen(
    cartState: CartState,
    authViewModel: AuthViewModel,
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var pureVegMode by remember { mutableStateOf(MessQData.studentProfile.isPureVeg) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var selectedMess by remember { mutableStateOf("Central Mess (Block A)") }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showQrModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Student Profile & Pass",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    IconButton(onClick = { showQrModal = true }) {
                        Icon(Icons.Outlined.QrCodeScanner, contentDescription = "QR Pass", tint = OrangePrimary)
                    }
                }
            }
        },
        bottomBar = {
            MessQBottomNav(
                currentRoute = currentRoute,
                cartItemCount = cartState.totalItems,
                onNavigate = onNavigate
            )
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DigitalMessPassCard(
                    profile = MessQData.studentProfile,
                    onQrClick = { showQrModal = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                ActivePlanCard()
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Dining Preferences",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Eco, contentDescription = null, tint = SuccessGreen)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Pure Veg Mode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Filter all non-veg items from menu", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = pureVegMode,
                                onCheckedChange = { pureVegMode = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Divider)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(OrangePrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = OrangePrimary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Meal Time Alerts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Daily notifications 30 min before mess opens", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OrangePrimary)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Account & Settings",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        ProfileOptionRow(
                            icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                            title = "Meal Receipts & Order History",
                            subtitle = "View digital tokens and past transactions",
                            onClick = onNavigateToOrderHistory
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Divider)
                        ProfileOptionRow(
                            icon = Icons.Outlined.LockReset,
                            title = "Security & Password Reset",
                            subtitle = "Update account login credentials",
                            onClick = onNavigateToForgotPassword
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Divider)
                        ProfileOptionRow(
                            icon = Icons.Outlined.HeadsetMic,
                            title = "Mess Help Desk & Warden Hotline",
                            subtitle = "Toll-free emergency campus support",
                            onClick = {}
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Divider)
                        ProfileOptionRow(
                            icon = Icons.AutoMirrored.Outlined.Logout,
                            title = "Sign Out",
                            subtitle = "Log out from this device",
                            iconTint = ErrorRed,
                            titleColor = ErrorRed,
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showQrModal) {
        AlertDialog(
            onDismissRequest = { showQrModal = false },
            confirmButton = {
                Button(
                    onClick = { showQrModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Mess Turnstile QR Pass",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hold this QR code against the scanner at the mess entry counter.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        color = Color.White,
                        border = BorderStroke(2.dp, OrangePrimary)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.QrCode2,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = MessQData.studentProfile.qrToken,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = OrangePrimary
                    )
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your BitePass campus account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Log Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun DigitalMessPassCard(
    profile: com.messq.app.data.StudentProfile,
    onQrClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name.take(1),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = profile.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Roll: ${profile.rollNo}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${profile.hostelBlock} • ${profile.roomNo}",
                            fontSize = 12.sp,
                            color = OrangePrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .bouncyClick(onClick = onQrClick),
                    color = OrangeLight,
                    border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode,
                        contentDescription = "Show QR",
                        tint = OrangePrimary,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Divider)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verified Student Mess Pass",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessGreen
                    )
                }

                Text(
                    text = "AY 2026-27",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextHint
                )
            }
        }
    }
}

@Composable
private fun ActivePlanCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE MEAL SUBSCRIPTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = OrangePrimary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Premium 3-Meal Dining Plan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Includes Breakfast, Lunch & Dinner at all campus messes",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TODAY'S TOKENS", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("3 of 3 Available", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("PLAN VALIDITY", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("84 Days Left", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = OrangePrimary,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bouncyClick(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextHint,
            modifier = Modifier.size(14.dp)
        )
    }
}
