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

    LaunchedEffect(Unit) { orderViewModel.loadHistory() }

    val filters = listOf("All", "Completed", "Upcoming", "Cancelled")

    val sampleOrders = remember {
        listOf(
            OrderData("MQ001", "Dal Rice x1, Paneer Curry x1", 105, "Completed", "12:00 - 12:30 PM", "10 Sep 2026", System.currentTimeMillis() - 3600000),
            OrderData("MQ002", "Aloo Paratha x2, Filter Coffee x1", 75, "Upcoming", "08:00 - 08:30 AM", "11 Sep 2026", System.currentTimeMillis()),
            OrderData("MQ003", "Rajma Chawal x1, Roti Sabzi x2", 130, "Completed", "07:00 - 07:30 PM", "09 Sep 2026", System.currentTimeMillis() - 86400000),
            OrderData("MQ004", "Samosa x3, Cold Coffee x1", 60, "Cancelled", "04:00 - 04:30 PM", "08 Sep 2026", System.currentTimeMillis() - 172800000)
        )
    }

    val displayOrders = if (orders.isEmpty()) sampleOrders else orders
    val filteredOrders = if (selectedFilter == "All") displayOrders
        else displayOrders.filter { it.status == selectedFilter }

    Scaffold(
        topBar = {
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1A1A1A)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Orders",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                text = "${displayOrders.size} orders total",
                                fontSize = 12.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Outlined.FilterList,
                                contentDescription = "Filter",
                                tint = OrangePrimary
                            )
                        }
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = filter == selectedFilter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) OrangePrimary else Color.White)
                                    .border(
                                        1.dp,
                                        if (isSelected) OrangePrimary else Color(0xFFEEEEEE),
                                        RoundedCornerShape(20.dp)
                                    )
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFF757575)
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Color(0xFFFFF8F0)
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
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderHistoryCard(order = order)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: OrderData) {
    val statusColor = when (order.status) {
        "Completed" -> Color(0xFF4CAF50)
        "Upcoming" -> OrangePrimary
        "Cancelled" -> Color(0xFFF44336)
        else -> Color(0xFF9E9E9E)
    }

    val statusBg = when (order.status) {
        "Completed" -> Color(0xFFE8F5E9)
        "Upcoming" -> Color(0xFFFFF3E0)
        "Cancelled" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }

    val orderIdShort = "#MQ" + order.id.take(4).uppercase()
    val dateDisplay = if (order.date.isNotEmpty()) order.date else {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(order.createdAt))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(140.dp)
                    .background(
                        color = statusColor,
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = orderIdShort,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = order.status,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = order.items,
                    fontSize = 13.sp,
                    color = Color(0xFF424242),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (order.slot.isNotEmpty()) "${order.slot} · $dateDisplay" else dateDisplay,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider(color = Color(0xFFF5F5F5))

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${order.total}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangePrimary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, OrangePrimary, RoundedCornerShape(20.dp))
                                .clickable { }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Reorder",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OrangePrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F6FA))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Receipt,
                                contentDescription = "Receipt",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF757575)
                            )
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
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.ShoppingBag,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = OrangePrimary.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "No orders yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Order your first meal from the mess\nand track it here",
            fontSize = 14.sp,
            color = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = {},
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            modifier = Modifier.height(48.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Outlined.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Browse Menu", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
