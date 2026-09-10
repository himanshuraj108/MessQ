package com.messq.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.CrowdLevel
import com.messq.app.data.MessQData
import com.messq.app.navigation.CartState
import com.messq.app.ui.components.MessQBottomNav
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    cartState: CartState,
    homeViewModel: HomeViewModel,
    onNavigateToQueue: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToPreOrder: () -> Unit,
    onNavigateToOrderHistory: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onLogout: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val queueStatus by homeViewModel.queueStatus.collectAsStateWithLifecycle()
    val userName by homeViewModel.userName.collectAsStateWithLifecycle()
    val walletBalance by homeViewModel.walletBalance.collectAsStateWithLifecycle()
    val mainMess = MessQData.messLocations.first { it.isMain }

    Scaffold(
        bottomBar = {
            MessQBottomNav(currentRoute = currentRoute, onNavigate = onNavigate)
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(OrangePrimary, OrangeDark),
                            endY = 200f
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "H",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = OrangePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Good Morning,",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "Himanshu",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {}) {
                            Box {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(HighCrowd)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "H",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-1).dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for food, menu, or category...", color = TextHint, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Divider,
                        focusedBorderColor = OrangePrimary,
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(140.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = OrangePrimary)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = 30.dp, y = (-30).dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Good Food",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "Fuels Great Ideas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Healthy Meals for a Brighter Tomorrow",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable { onNavigateToMenu() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "View Today's Menu  →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangePrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Current Mess Crowd",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LowCrowd.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(LowCrowd)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Live",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LowCrowd
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.People,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${mainMess.waitTimeMin} – ${mainMess.waitTimeMax} min",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Estimated wait time • Main Mess, Block A",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    MiniCrowdBars()
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Quick Actions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            val actions = listOf(
                Triple("Pre-Order", Icons.Outlined.ShoppingCart, { onNavigateToPreOrder() }),
                Triple("Book Slot", Icons.Outlined.CalendarMonth, { onNavigateToQueue() }),
                Triple("Menu", Icons.AutoMirrored.Outlined.MenuBook, { onNavigateToMenu() }),
                Triple("Order History", Icons.Outlined.History, { onNavigateToOrderHistory() }),
                Triple("Feedback", Icons.Outlined.StarOutline, {}),
                Triple("Assistant", Icons.Outlined.SupportAgent, { onNavigateToAssistant() })
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                actions.chunked(3).forEach { rowActions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowActions.forEach { (label, icon, action) ->
                            QuickActionCard(
                                label = label,
                                icon = icon,
                                onClick = action,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowActions.size < 3) {
                            repeat(3 - rowActions.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(OrangePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (label) {
                    "Pre-Order" -> "Skip the queue"
                    "Book Slot" -> "Reserve your time"
                    "Menu" -> "Today's meals"
                    "Order History" -> "Past orders"
                    "Feedback" -> "Help us improve"
                    "Assistant" -> "Ask anything"
                    else -> ""
                },
                fontSize = 10.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MiniCrowdBars() {
    val heights = listOf(0.3f, 0.5f, 0.9f, 0.6f, 0.4f, 0.7f, 0.45f)
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.height(40.dp)
    ) {
        heights.forEachIndexed { index, fraction ->
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(fraction)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        when {
                            index == 2 -> OrangePrimary
                            fraction > 0.7f -> HighCrowd.copy(alpha = 0.5f)
                            else -> OrangePrimary.copy(alpha = 0.3f)
                        }
                    )
            )
        }
    }
}
