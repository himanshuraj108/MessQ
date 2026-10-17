package com.messq.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.MenuItem
import com.messq.app.data.MessQData
import com.messq.app.ui.components.PulsingRadarDot
import com.messq.app.ui.components.bouncyClick
import com.messq.app.ui.components.holographicSweep
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.HomeViewModel

data class StaffScannedToken(
    val tokenId: String,
    val studentName: String,
    val rollNo: String,
    val hostelRoom: String,
    val mealType: String,
    val planName: String,
    val isPureVeg: Boolean,
    val itemsSummary: String,
    val scanTime: String,
    var isServed: Boolean = false
)

data class StaffStudentEntry(
    val rollNo: String,
    val name: String,
    val hostelBlock: String,
    val roomNo: String,
    val mealPlan: String,
    val isPureVeg: Boolean,
    val lastMeal: String,
    var isCheckedIn: Boolean
)

@Composable
fun StaffPanelScreen(
    homeViewModel: HomeViewModel,
    onNavigateToStudentView: () -> Unit,
    onLogout: () -> Unit
) {
    val queueStatus by homeViewModel.queueStatus.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var tokenInput by remember { mutableStateOf("") }
    var activeScannedRecord by remember {
        mutableStateOf<StaffScannedToken?>(
            StaffScannedToken(
                tokenId = "BP-PASS-12104892-2026",
                studentName = "Himanshu Raj",
                rollNo = "12104892",
                hostelRoom = "Block A • Room 304",
                mealType = "Lunch (Slot 12:30 - 01:00 PM)",
                planName = "Premium 3-Meal Plan",
                isPureVeg = false,
                itemsSummary = "Paneer Butter Masala Thali x1, Cold Coffee x1",
                scanTime = "12:44 PM",
                isServed = false
            )
        )
    }

    val recentScans = remember {
        mutableStateListOf(
            StaffScannedToken(
                tokenId = "BP-PASS-12104892-2026",
                studentName = "Himanshu Raj",
                rollNo = "12104892",
                hostelRoom = "Block A • Room 304",
                mealType = "Lunch",
                planName = "Premium Plan",
                isPureVeg = false,
                itemsSummary = "Paneer Butter Masala Thali",
                scanTime = "12:44 PM",
                isServed = false
            ),
            StaffScannedToken(
                tokenId = "BP-8192",
                studentName = "Aarav Sharma",
                rollNo = "12104901",
                hostelRoom = "Block B • Room 102",
                mealType = "Lunch",
                planName = "Standard 2-Meal",
                isPureVeg = true,
                itemsSummary = "Dal Makhani with Jeera Rice",
                scanTime = "12:38 PM",
                isServed = true
            ),
            StaffScannedToken(
                tokenId = "BP-4412",
                studentName = "Ananya Sen",
                rollNo = "12105120",
                hostelRoom = "Block H • Room 208",
                mealType = "Lunch",
                planName = "Premium Plan",
                isPureVeg = true,
                itemsSummary = "Chole Bhature Platter",
                scanTime = "12:31 PM",
                isServed = true
            ),
            StaffScannedToken(
                tokenId = "BP-9031",
                studentName = "Priyanshu Verma",
                rollNo = "12103844",
                hostelRoom = "Block C • Room 415",
                mealType = "Lunch",
                planName = "Flexi Plan",
                isPureVeg = false,
                itemsSummary = "Chicken Curry Meal",
                scanTime = "12:22 PM",
                isServed = true
            )
        )
    }

    val menuStockMap = remember {
        mutableStateMapOf<Int, Pair<Boolean, Int>>().apply {
            MessQData.allMenuItems.forEach { item ->
                put(item.id, Pair(true, 45))
            }
            put(1, Pair(true, 28))
            put(7, Pair(true, 52))
            put(8, Pair(true, 14))
            put(9, Pair(false, 0))
            put(12, Pair(true, 36))
            put(18, Pair(false, 0))
        }
    }

    val studentRoster = remember {
        mutableStateListOf(
            StaffStudentEntry("12104892", "Himanshu Raj", "Block A", "304", "Premium 3-Meal", false, "Lunch (Active)", true),
            StaffStudentEntry("12104901", "Aarav Sharma", "Block B", "102", "Standard 2-Meal", true, "Lunch (Served)", true),
            StaffStudentEntry("12105120", "Ananya Sen", "Block H", "208", "Premium 3-Meal", true, "Lunch (Served)", true),
            StaffStudentEntry("12103844", "Priyanshu Verma", "Block C", "415", "Flexi Dining", false, "Lunch (Served)", true),
            StaffStudentEntry("12104677", "Priya Nair", "Block H", "112", "Standard 2-Meal", true, "Breakfast (Served)", false),
            StaffStudentEntry("12104239", "Vikram Patel", "Block A", "210", "Premium 3-Meal", false, "Yesterday Dinner", false),
            StaffStudentEntry("12105561", "Rhea Kapoor", "Block D", "108", "Standard 2-Meal", true, "Breakfast (Served)", false),
            StaffStudentEntry("12103390", "Kunal Mehra", "Block B", "401", "Premium 3-Meal", false, "Not Arrived", false)
        )
    }

    var bannerMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "COMMAND CENTER",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangePrimary,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    PulsingRadarDot(color = Color(0xFF16A34A), dotSize = 8.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                }
                                Text(
                                    text = "Central Dining Complex • Station 01",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Chief Warden: Dr. K. Sharma",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = OrangeLight,
                                modifier = Modifier.bouncyClick(onClick = onNavigateToStudentView)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = OrangePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Student View",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OrangePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(3.dp)
                    ) {
                        listOf(
                            "Turnstile QR" to Icons.Outlined.QrCodeScanner,
                            "Queue & Rush" to Icons.Outlined.Groups,
                            "Kitchen Stock" to Icons.Outlined.RestaurantMenu,
                            "Meal Roster" to Icons.Outlined.Badge
                        ).forEachIndexed { idx, tab ->
                            val isSelected = selectedTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) OrangePrimary else Color.Transparent)
                                    .bouncyClick { selectedTab = idx }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = tab.second,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tab.first,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            bannerMessage?.let { msg ->
                Surface(
                    color = Color(0xFF16A34A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text(
                            text = "DISMISS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.clickable { bannerMessage = null }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    StaffMetricsStrip(
                        currentOccupancy = queueStatus.currentCount,
                        maxCapacity = queueStatus.maxCapacity,
                        servedCount = 1428,
                        totalExpected = 1800,
                        todayRevenue = 48250,
                        pendingOrders = 19
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                when (selectedTab) {
                    0 -> {
                        item {
                            StaffTurnstileTab(
                                activeRecord = activeScannedRecord,
                                tokenInput = tokenInput,
                                onTokenInputChange = { tokenInput = it },
                                onSearchToken = { query ->
                                    val match = recentScans.find {
                                        it.tokenId.contains(query, ignoreCase = true) ||
                                                it.rollNo.contains(query, ignoreCase = true) ||
                                                it.studentName.contains(query, ignoreCase = true)
                                    }
                                    if (match != null) {
                                        activeScannedRecord = match
                                        bannerMessage = "Found Pass: ${match.studentName} (${match.tokenId})"
                                    } else {
                                        activeScannedRecord = StaffScannedToken(
                                            tokenId = if (query.startsWith("BP-")) query else "BP-$query",
                                            studentName = "Registered Student",
                                            rollNo = query,
                                            hostelRoom = "Hostel Complex",
                                            mealType = "Lunch Access",
                                            planName = "Semester Plan",
                                            isPureVeg = false,
                                            itemsSummary = "Standard Meal Entitlement",
                                            scanTime = "Just Now",
                                            isServed = false
                                        )
                                        bannerMessage = "Loaded Verified Token $query"
                                    }
                                },
                                onSelectPreset = { record ->
                                    activeScannedRecord = record
                                    tokenInput = record.tokenId
                                },
                                onMarkServed = { record ->
                                    record.isServed = true
                                    val idx = recentScans.indexOfFirst { it.tokenId == record.tokenId }
                                    if (idx != -1) {
                                        recentScans[idx] = record.copy(isServed = true)
                                    } else {
                                        recentScans.add(0, record.copy(isServed = true))
                                    }
                                    activeScannedRecord = record.copy(isServed = true)
                                    bannerMessage = "Turnstile #01 Unlocked: ${record.studentName} Marked Served"
                                },
                                recentScans = recentScans
                            )
                        }
                    }
                    1 -> {
                        item {
                            StaffQueueControllerTab(
                                queueStatus = queueStatus,
                                onUpdateCount = { newCount, waitTime ->
                                    homeViewModel.updateQueueCount(newCount, waitTime)
                                    bannerMessage = "Queue Synced: $newCount Students in Dining Hall"
                                },
                                onBroadcastAlert = {
                                    bannerMessage = "Push broadcast dispatched to 4,200 connected campus devices"
                                }
                            )
                        }
                    }
                    2 -> {
                        item {
                            StaffKitchenStockTab(
                                menuStockMap = menuStockMap,
                                onToggleAvailability = { id ->
                                    val current = menuStockMap[id] ?: Pair(true, 40)
                                    val updated = Pair(!current.first, if (!current.first) 40 else 0)
                                    menuStockMap[id] = updated
                                    bannerMessage = if (updated.first) "Dish Marked IN STOCK (+40 portions)" else "Dish Marked SOLD OUT"
                                },
                                onAdjustPortions = { id, delta ->
                                    val current = menuStockMap[id] ?: Pair(true, 40)
                                    val newPortions = (current.second + delta).coerceAtLeast(0)
                                    menuStockMap[id] = Pair(newPortions > 0, newPortions)
                                }
                            )
                        }
                    }
                    3 -> {
                        item {
                            StaffRosterTab(
                                rosterList = studentRoster,
                                onToggleCheckIn = { student ->
                                    val idx = studentRoster.indexOfFirst { it.rollNo == student.rollNo }
                                    if (idx != -1) {
                                        val newState = !student.isCheckedIn
                                        studentRoster[idx] = student.copy(
                                            isCheckedIn = newState,
                                            lastMeal = if (newState) "Lunch (Manual Check-in)" else "Pending Arrival"
                                        )
                                        bannerMessage = if (newState) "${student.name} Checked In Manually" else "${student.name} Check-In Reverted"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffMetricsStrip(
    currentOccupancy: Int,
    maxCapacity: Int,
    servedCount: Int,
    totalExpected: Int,
    todayRevenue: Int,
    pendingOrders: Int
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Meals Served",
                value = "$servedCount",
                subtext = "of $totalExpected expected (79%)",
                icon = Icons.Outlined.CheckCircle,
                accentColor = Color(0xFF16A34A),
                bgColor = Color(0xFFECFDF5)
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Hall Occupancy",
                value = "$currentOccupancy",
                subtext = "Capacity $maxCapacity (${(currentOccupancy * 100 / maxCapacity.coerceAtLeast(1))}%)",
                icon = Icons.Outlined.Groups,
                accentColor = OrangePrimary,
                bgColor = OrangeLight
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Digital Collections",
                value = "Rs. $todayRevenue",
                subtext = "Wallet, UPI, Pre-Orders",
                icon = Icons.Outlined.AccountBalanceWallet,
                accentColor = Color(0xFF2563EB),
                bgColor = Color(0xFFEFF6FF)
            )
            MetricCard(
                modifier = Modifier.weight(1f),
                title = "Pending Pre-Orders",
                value = "$pendingOrders",
                subtext = "Lunch slot ready for counter",
                icon = Icons.Outlined.Fastfood,
                accentColor = Color(0xFF9333EA),
                bgColor = Color(0xFFFAF5FF)
            )
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtext: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StaffTurnstileTab(
    activeRecord: StaffScannedToken?,
    tokenInput: String,
    onTokenInputChange: (String) -> Unit,
    onSearchToken: (String) -> Unit,
    onSelectPreset: (StaffScannedToken) -> Unit,
    onMarkServed: (StaffScannedToken) -> Unit,
    recentScans: List<StaffScannedToken>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PulsingRadarDot(color = Color(0xFF22C55E), dotSize = 8.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TURNSTILE OPTICAL VIEWFINDER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E),
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "SENSOR ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF1E293B))
                        .border(BorderStroke(2.dp, OrangePrimary), RoundedCornerShape(18.dp))
                        .holographicSweep(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Scan Token / Tap NFC",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Auto-Verifies in 50ms",
                            fontSize = 9.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Quick-Test Instant Verification Scans:",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentScans.take(3)) { scan ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF334155),
                            modifier = Modifier.bouncyClick { onSelectPreset(scan) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = scan.studentName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = scan.tokenId,
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = onTokenInputChange,
                        placeholder = { Text("Enter Token / Roll #", fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color(0xFF475569),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (tokenInput.isNotBlank()) onSearchToken(tokenInput.trim())
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Verify", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        activeRecord?.let { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.5.dp, if (record.isServed) Color(0xFF94A3B8) else Color(0xFF16A34A))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (record.isServed) Color(0xFFF1F5F9) else Color(0xFFECFDF5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (record.isServed) Icons.Outlined.Check else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = if (record.isServed) Color(0xFF64748B) else Color(0xFF16A34A),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = record.studentName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Roll: ${record.rollNo} • ${record.hostelRoom}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (record.isServed) Color(0xFFF1F5F9) else Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = if (record.isServed) "SERVED" else "VALIDATED",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (record.isServed) Color(0xFF64748B) else Color(0xFF16A34A),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "MEAL PLAN", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(text = record.planName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "SLOT WINDOW", fontSize = 10.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            Text(text = record.mealType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "ORDER ENTITLEMENT / PRE-ORDER ITEMS:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = record.itemsSummary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onMarkServed(record) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .bouncyClick { onMarkServed(record) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (record.isServed) Color(0xFF64748B) else Color(0xFF16A34A)
                        ),
                        enabled = !record.isServed
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (record.isServed) Icons.Filled.Check else Icons.Outlined.LockOpen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (record.isServed) "Meal Delivered • Gate Opened" else "Mark as Served & Open Turnstile",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Today's Verified Scan Ledger",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Real-time audit log of students granted turnstile entry",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        recentScans.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (item.isServed) Color(0xFFDCFCE7) else OrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.studentName.take(1),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isServed) Color(0xFF16A34A) else OrangePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.studentName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${item.rollNo} • ${item.itemsSummary}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = item.scanTime,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.isServed) Color(0xFFDCFCE7) else OrangeLight
                        ) {
                            Text(
                                text = if (item.isServed) "SERVED" else "WAITING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isServed) Color(0xFF16A34A) else OrangePrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffQueueControllerTab(
    queueStatus: com.messq.app.data.firebase.QueueStatusData,
    onUpdateCount: (Int, Int) -> Unit,
    onBroadcastAlert: () -> Unit
) {
    var customOccupancy by remember(queueStatus.currentCount) { mutableIntStateOf(queueStatus.currentCount) }
    val maxCap = if (queueStatus.maxCapacity > 0) queueStatus.maxCapacity else 200
    val occupancyFraction = (customOccupancy.toFloat() / maxCap.toFloat()).coerceIn(0f, 1f)

    val currentRushLevel = when {
        occupancyFraction < 0.45f -> "LOW RUSH"
        occupancyFraction < 0.75f -> "MODERATE"
        else -> "PEAK RUSH"
    }

    val rushColor = when {
        occupancyFraction < 0.45f -> Color(0xFF16A34A)
        occupancyFraction < 0.75f -> Color(0xFFEAB308)
        else -> Color(0xFFDC2626)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Central Dining Hall Live Rush Level",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Controls live headcount and estimated wait times for all 40,000 students",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(rushColor.copy(alpha = 0.12f))
                        .border(BorderStroke(4.dp, rushColor), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$customOccupancy",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = rushColor
                        )
                        Text(
                            text = "of $maxCap max",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(
                            text = currentRushLevel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = rushColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Instant Stepper Adjustments",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(-20, -5, -1, 1, 5, 20).forEach { delta ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .bouncyClick {
                                    val next = (customOccupancy + delta).coerceIn(0, maxCap)
                                    customOccupancy = next
                                    val calculatedWait = when {
                                        next < maxCap * 0.45f -> 4
                                        next < maxCap * 0.75f -> 10
                                        else -> 20
                                    }
                                    onUpdateCount(next, calculatedWait)
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (delta > 0) OrangeLight else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (delta > 0) OrangePrimary.copy(alpha = 0.4f) else Color(0xFFCBD5E1))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (delta > 0) "+$delta" else "$delta",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (delta > 0) OrangePrimary else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preset Rush Profiles (One-Tap Override)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("LOW", 45, Color(0xFF16A34A)),
                        Triple("MODERATE", 134, Color(0xFFEAB308)),
                        Triple("PEAK", 188, Color(0xFFDC2626))
                    ).forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .bouncyClick {
                                    customOccupancy = preset.second
                                    val wait = when (preset.first) {
                                        "LOW" -> 4
                                        "MODERATE" -> 10
                                        else -> 22
                                    }
                                    onUpdateCount(preset.second, wait)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = preset.third.copy(alpha = 0.1f),
                            border = BorderStroke(1.5.dp, preset.third)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = preset.first,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = preset.third
                                )
                                Text(
                                    text = "${preset.second} students",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Broadcast Peak Crowd Alert",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Sends notification to advise students to delay lunch by 15 mins",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBroadcastAlert,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .bouncyClick(onClick = onBroadcastAlert),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(imageVector = Icons.Outlined.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Announce Peak Rush Notification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StaffKitchenStockTab(
    menuStockMap: MutableMap<Int, Pair<Boolean, Int>>,
    onToggleAvailability: (Int) -> Unit,
    onAdjustPortions: (Int, Int) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Breakfast", "Lunch", "Dinner", "Snacks")

    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == "All") {
            MessQData.allMenuItems
        } else {
            MessQData.allMenuItems.filter { it.category.name.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Live Kitchen Stock & Menu Availability",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Toggle Sold-Out status to prevent student over-ordering",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) OrangePrimary else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) OrangePrimary else Color(0xFFE2E8F0)),
                    modifier = Modifier.bouncyClick { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        filteredItems.forEach { item ->
            val stockInfo = menuStockMap[item.id] ?: Pair(true, 40)
            val isInStock = stockInfo.first
            val remainingPortions = stockInfo.second

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isInStock) CardBackground else Color(0xFFF8FAFC)),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isInStock) 2.dp else 0.dp),
                border = BorderStroke(1.dp, if (isInStock) Color(0xFFF1F5F9) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (item.isVeg) Color(0xFF16A34A) else Color(0xFFDC2626))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInStock) TextPrimary else Color(0xFF94A3B8)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Rs. ${item.price} • ${item.category.name} • Prep: ${item.prepTimeMinutes}m",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isInStock) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                            ) {
                                Text(
                                    text = if (isInStock) "$remainingPortions plates left" else "SOLD OUT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInStock) Color(0xFF16A34A) else Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (isInStock) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+10 plates",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary,
                                    modifier = Modifier
                                        .bouncyClick { onAdjustPortions(item.id, 10) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "-10 plates",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier
                                        .bouncyClick { onAdjustPortions(item.id, -10) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Switch(
                            checked = isInStock,
                            onCheckedChange = { onToggleAvailability(item.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF16A34A),
                                uncheckedThumbColor = Color(0xFF64748B),
                                uncheckedTrackColor = Color(0xFFCBD5E1)
                            )
                        )
                        Text(
                            text = if (isInStock) "In Stock" else "Sold Out",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isInStock) Color(0xFF16A34A) else Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StaffRosterTab(
    rosterList: List<StaffStudentEntry>,
    onToggleCheckIn: (StaffStudentEntry) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterPureVeg by remember { mutableStateOf(false) }

    val filteredRoster = remember(searchQuery, filterPureVeg, rosterList) {
        rosterList.filter { student ->
            val matchQuery = searchQuery.isBlank() ||
                    student.name.contains(searchQuery, ignoreCase = true) ||
                    student.rollNo.contains(searchQuery, ignoreCase = true) ||
                    student.hostelBlock.contains(searchQuery, ignoreCase = true)
            val matchVeg = !filterPureVeg || student.isPureVeg
            matchQuery && matchVeg
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Student Dining Roster & Audit",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Verify registered meal plans and mark manual check-ins",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, roll no, or block...", fontSize = 12.sp, color = TextHint) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground
                ),
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (filterPureVeg) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
                border = BorderStroke(1.dp, if (filterPureVeg) Color(0xFF16A34A) else Color.Transparent),
                modifier = Modifier
                    .height(48.dp)
                    .bouncyClick { filterPureVeg = !filterPureVeg }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pure Veg",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (filterPureVeg) Color(0xFF16A34A) else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        filteredRoster.forEach { student ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (student.isCheckedIn) Color(0xFFDCFCE7) else OrangeLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(1),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (student.isCheckedIn) Color(0xFF16A34A) else OrangePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (student.isPureVeg) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFDCFCE7)
                                    ) {
                                        Text(
                                            text = "VEG",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF16A34A),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Roll: ${student.rollNo} • ${student.hostelBlock} Rm ${student.roomNo}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Text(
                                text = "${student.mealPlan} • Status: ${student.lastMeal}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (student.isCheckedIn) Color(0xFF16A34A) else Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (student.isCheckedIn) Color(0xFFF1F5F9) else OrangeLight,
                        border = BorderStroke(1.dp, if (student.isCheckedIn) Color(0xFFCBD5E1) else OrangePrimary),
                        modifier = Modifier.bouncyClick { onToggleCheckIn(student) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (student.isCheckedIn) Icons.Filled.Check else Icons.Filled.Add,
                                contentDescription = null,
                                tint = if (student.isCheckedIn) Color(0xFF64748B) else OrangePrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (student.isCheckedIn) "Checked In" else "Check In",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.isCheckedIn) Color(0xFF64748B) else OrangePrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
