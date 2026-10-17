package com.messq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.CrowdLevel
import com.messq.app.data.MenuItem
import com.messq.app.data.MessLocation
import com.messq.app.data.MessQData
import com.messq.app.navigation.CartState
import com.messq.app.ui.components.*
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
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToStaff: () -> Unit = {},
    onLogout: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val queueStatus by homeViewModel.queueStatus.collectAsStateWithLifecycle()
    val userName by homeViewModel.userName.collectAsStateWithLifecycle()
    val walletBalance by homeViewModel.walletBalance.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedDetailDish by remember { mutableStateOf<MenuItem?>(null) }

    val mainMess = MessQData.messLocations.first { it.isMain }
    val featuredDishes = remember { MessQData.allMenuItems.filter { it.tag.isNotEmpty() }.take(6) }

    Scaffold(
        bottomBar = {
            MessQBottomNav(
                currentRoute = currentRoute,
                cartItemCount = cartState.totalItems,
                onNavigate = onNavigate
            )
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
                            colors = listOf(
                                Color(0xFFE85D00),
                                OrangePrimary,
                                Color(0xFFFB923C)
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.bouncyClick(onClick = onNavigateToProfile)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userName.take(1).ifEmpty { "H" },
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = OrangePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Welcome Back,",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = if (userName == "Student") "Himanshu Raj" else userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${MessQData.studentProfile.hostelBlock} • ${MessQData.studentProfile.roomNo}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.bouncyClick(onClick = onNavigateToWallet)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Rs. ${if (walletBalance == 0) 500 else walletBalance}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.QrCodeScanner,
                                    contentDescription = "QR Pass",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.bouncyClick(onClick = onNavigateToStaff)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Shield,
                                        contentDescription = "Staff",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Staff",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .bouncyClick(onClick = onNavigateToMenu),
                        shape = RoundedCornerShape(14.dp),
                        color = CardBackground,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Search dishes, thalis, snacks...",
                                fontSize = 13.sp,
                                color = TextHint
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LiveCrowdStatusHeroCard(
                location = mainMess,
                currentCount = if (queueStatus.currentCount > 0) queueStatus.currentCount else mainMess.currentOccupancy,
                maxCapacity = if (queueStatus.maxCapacity > 0) queueStatus.maxCapacity else mainMess.maxCapacity,
                waitTimeMin = mainMess.waitTimeMin,
                waitTimeMax = mainMess.waitTimeMax,
                onViewDetails = onNavigateToQueue
            )

            Spacer(modifier = Modifier.height(20.dp))

            QuickActionGrid(
                onNavigateToPreOrder = onNavigateToPreOrder,
                onNavigateToMenu = onNavigateToMenu,
                onNavigateToQueue = onNavigateToQueue,
                onNavigateToAssistant = onNavigateToAssistant,
                onNavigateToWallet = onNavigateToWallet,
                onNavigateToProfile = onNavigateToProfile
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Chef's Specials & Bestsellers",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Top picks prepared fresh for today's meal",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                TextButton(onClick = onNavigateToMenu) {
                    Text("See All", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(featuredDishes) { dish ->
                    FeaturedDishCard(
                        item = dish,
                        quantity = cartState.getQuantity(dish),
                        onAdd = { cartState.addItem(dish) },
                        onRemove = { cartState.removeItem(dish) },
                        onClick = { selectedDetailDish = dish }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Campus Dining Halls",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Live occupancy across all hostel complexes",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                TextButton(
                    onClick = onNavigateToQueue,
                    modifier = Modifier.bouncyClick(onClick = onNavigateToQueue)
                ) {
                    Text("Heatmap", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MessQData.messLocations.filter { !it.isMain }.forEach { loc ->
                    OtherMessCard(location = loc, onSelect = onNavigateToQueue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        selectedDetailDish?.let { dish ->
            DishDetailDialog(
                item = dish,
                quantity = cartState.getQuantity(dish),
                onAdd = { cartState.addItem(dish) },
                onRemove = { cartState.removeItem(dish) },
                onDismiss = { selectedDetailDish = null },
                onProceedToCheckout = onNavigateToPreOrder
            )
        }
    }
}

@Composable
private fun LiveCrowdStatusHeroCard(
    location: MessLocation,
    currentCount: Int,
    maxCapacity: Int,
    waitTimeMin: Int,
    waitTimeMax: Int,
    onViewDetails: () -> Unit
) {
    val percentage = (currentCount.toFloat() / maxCapacity.toFloat()).coerceIn(0f, 1f)
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(1200),
        label = "crowdArc"
    )

    val crowdColor = when {
        percentage < 0.45f -> Color(0xFF16A34A)
        percentage < 0.75f -> Color(0xFFEAB308)
        else -> Color(0xFFDC2626)
    }

    val crowdLabel = when {
        percentage < 0.45f -> "LOW RUSH"
        percentage < 0.75f -> "MODERATE"
        else -> "HEAVY RUSH"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .bouncyClick(onClick = onViewDetails),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PulsingRadarDot(color = crowdColor, dotSize = 7.dp, ringSize = 16.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = location.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = crowdColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = crowdLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = crowdColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        drawArc(
                            color = Color(0xFFE2E8F0),
                            startAngle = 140f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                            size = Size(size.width, size.height)
                        )
                        drawArc(
                            color = crowdColor,
                            startAngle = 140f,
                            sweepAngle = 260f * animatedPercentage,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round),
                            size = Size(size.width, size.height)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(percentage * 100).toInt()}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Occupancy",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f).padding(start = 18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.People, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$currentCount / $maxCapacity Students",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$waitTimeMin - $waitTimeMax Min Average Wait",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = OrangeLight,
                        modifier = Modifier.clickable(onClick = onViewDetails)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "View live hourly forecast",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionGrid(
    onNavigateToPreOrder: () -> Unit,
    onNavigateToMenu: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToAssistant: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "Pre-Order",
                subtitle = "Skip queue",
                icon = Icons.Outlined.ShoppingBag,
                iconColor = OrangePrimary,
                bgColor = OrangeLight,
                modifier = Modifier.weight(1f),
                onClick = onNavigateToPreOrder
            )
            ActionTile(
                title = "Today's Menu",
                subtitle = "4 categories",
                icon = Icons.Outlined.RestaurantMenu,
                iconColor = Color(0xFF2563EB),
                bgColor = Color(0xFFEFF6FF),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToMenu
            )
            ActionTile(
                title = "Queue Status",
                subtitle = "Live counters",
                icon = Icons.Outlined.Groups,
                iconColor = Color(0xFF059669),
                bgColor = Color(0xFFECFDF5),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToQueue
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ActionTile(
                title = "AI Assistant",
                subtitle = "Groq LLaMA",
                icon = Icons.Outlined.SupportAgent,
                iconColor = Color(0xFF7C3AED),
                bgColor = Color(0xFFF5F3FF),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToAssistant
            )
            ActionTile(
                title = "Mess Wallet",
                subtitle = "Card & UPI",
                icon = Icons.Outlined.AccountBalanceWallet,
                iconColor = Color(0xFFD97706),
                bgColor = Color(0xFFFFFBEB),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToWallet
            )
            ActionTile(
                title = "Digital Pass",
                subtitle = "Turnstile QR",
                icon = Icons.Outlined.QrCode,
                iconColor = Color(0xFF0284C7),
                bgColor = Color(0xFFF0F9FF),
                modifier = Modifier.weight(1f),
                onClick = onNavigateToProfile
            )
        }
    }
}

@Composable
private fun ActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .bouncyClick(onClick = onClick),
        color = CardBackground,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Divider)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FeaturedDishCard(
    item: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .bouncyClick(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(115.dp).fillMaxWidth()) {
                FoodThumbnail(
                    imageUrl = item.imageUrl,
                    fallbackColor = item.colorHex,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.tag.isNotEmpty()) {
                    ShimmeringSpecialTag(
                        text = item.tag,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    DietaryBadge(isVeg = item.isVeg)
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${item.calories} kcal • ${item.prepTimeMinutes} min",
                    fontSize = 10.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${item.price}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary
                    )

                    QuantityStepper(
                        quantity = quantity,
                        onAdd = onAdd,
                        onRemove = onRemove
                    )
                }
            }
        }
    }
}

@Composable
private fun OtherMessCard(
    location: MessLocation,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .bouncyClick(onClick = onSelect),
        color = CardBackground,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Divider)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(
                            when (location.crowdLevel) {
                                CrowdLevel.LOW -> Color(0xFF16A34A)
                                CrowdLevel.MODERATE -> Color(0xFFEAB308)
                                CrowdLevel.HIGH -> Color(0xFFDC2626)
                            }
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = location.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${location.block} • ${location.currentOccupancy}/${location.maxCapacity} Seats",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = OrangeLight
            ) {
                Text(
                    text = "${location.waitTimeMin}m wait",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
