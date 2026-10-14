package com.messq.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.firebase.OrderData
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    onBack: () -> Unit
) {
    val orders: List<OrderData> by orderViewModel.orders.collectAsStateWithLifecycle()
    val isLoading: Boolean by orderViewModel.isLoading.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedReceiptOrder by remember { mutableStateOf<OrderData?>(null) }

    LaunchedEffect(Unit) { orderViewModel.loadHistory() }

    val filters = listOf("All", "Completed", "Upcoming", "Cancelled")

    val sampleOrders = remember {
        listOf(
            OrderData("MQ8492", "Paneer Butter Masala Thali x1, Cold Coffee x1", 110, "Completed", "12:30 - 01:00 PM", "Today", System.currentTimeMillis() - 3600000),
            OrderData("MQ7291", "Aloo Paratha with Curd x2, Filter Coffee x1", 75, "Upcoming", "08:00 - 08:30 AM", "Tomorrow", System.currentTimeMillis()),
            OrderData("MQ6104", "Special Chicken Curry Meal x1, Tawa Roti x2", 120, "Completed", "07:30 - 08:00 PM", "Yesterday", System.currentTimeMillis() - 86400000),
            OrderData("MQ4922", "Cheese Masala Maggi x2, Fruit Juice x1", 85, "Cancelled", "04:30 - 05:00 PM", "12 Oct 2026", System.currentTimeMillis() - 172800000)
        )
    }

    val displayOrders = if (orders.isEmpty()) sampleOrders else orders
    val filteredOrders = if (selectedFilter == "All") displayOrders
    else displayOrders.filter { it.status == selectedFilter }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Order Receipts & History",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${displayOrders.size} meals recorded on campus ledger",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = filter == selectedFilter
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) OrangePrimary else CardBackground,
                                border = if (isSelected) null else BorderStroke(1.dp, Divider),
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier.clickable { selectedFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (filteredOrders.isEmpty()) {
            EmptyOrdersState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderHistoryCard(
                        order = order,
                        onShowReceipt = { selectedReceiptOrder = order }
                    )
                }
                item { Spacer(modifier = Modifier.height(10.dp)) }
            }
        }
    }

    selectedReceiptOrder?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedReceiptOrder = null },
            confirmButton = {
                Button(
                    onClick = { selectedReceiptOrder = null },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            title = {
                Text(
                    text = "Digital Meal Token Receipt",
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
                        text = "#MQ-${order.id.takeLast(4).uppercase()}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, Divider)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.QrCode2,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(110.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = order.items,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Window: ${order.slot} • Total: Rs. ${order.total}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            },
            containerColor = CardBackground,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun OrderHistoryCard(
    order: OrderData,
    onShowReceipt: () -> Unit
) {
    val statusColor = when (order.status) {
        "Completed" -> SuccessGreen
        "Upcoming" -> OrangePrimary
        "Cancelled" -> ErrorRed
        else -> TextHint
    }

    val statusBg = when (order.status) {
        "Completed" -> SuccessGreen.copy(alpha = 0.12f)
        "Upcoming" -> OrangeLight
        "Cancelled" -> ErrorRed.copy(alpha = 0.12f)
        else -> Surface
    }

    val orderIdShort = "#MQ" + order.id.takeLast(4).uppercase()
    val dateDisplay = if (order.date.isNotEmpty()) order.date else {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(order.createdAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowReceipt),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(130.dp)
                    .background(
                        color = statusColor,
                        shape = RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = orderIdShort,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg
                    ) {
                        Text(
                            text = order.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = order.items,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (order.slot.isNotEmpty()) "${order.slot} • $dateDisplay" else dateDisplay,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Divider)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${order.total}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = OrangeLight,
                            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.clickable(onClick = onShowReceipt)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.QrCode,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "View Pass",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(OrangeLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                tint = OrangePrimary
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "No meal orders yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Pre-order meals from the menu and track your digital tokens here",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
