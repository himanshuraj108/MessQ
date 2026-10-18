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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.MenuItem
import com.messq.app.data.MessQData
import com.messq.app.ui.components.PulsingRadarDot
import com.messq.app.ui.components.bouncyClick
import com.messq.app.ui.components.holographicSweep
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

enum class GateRelayState {
    LOCKED,
    UNLOCKED,
    EMERGENCY_RELEASE,
    LOCKDOWN
}

enum class KitchenTicketStatus {
    QUEUED,
    COOKING,
    READY,
    BUMPED
}

data class KitchenTicket(
    val ticketId: String,
    val studentName: String,
    val rollNo: String,
    val orderSummary: String,
    val slot: String,
    var status: KitchenTicketStatus,
    val elapsedMin: Int,
    val isPriority: Boolean = false
)

data class RawMaterialSilo(
    val name: String,
    val currentQty: Float,
    val maxQty: Float,
    val unit: String,
    val burnRate: String,
    val isLow: Boolean
)

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
    var isServed: Boolean = false,
    val isDuplicateAttempt: Boolean = false
)

data class StaffStudentEntry(
    val rollNo: String,
    val name: String,
    val hostelBlock: String,
    val roomNo: String,
    val mealPlan: String,
    val isPureVeg: Boolean,
    val lastMeal: String,
    var isCheckedIn: Boolean,
    val walletCredits: Int = 450,
    val dietaryAllergy: String = "None"
)

@Composable
fun StaffPanelScreen(
    homeViewModel: HomeViewModel,
    onNavigateToStudentView: () -> Unit,
    onLogout: () -> Unit
) {
    val queueStatus by homeViewModel.queueStatus.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTerminal by remember { mutableStateOf("Gate Turnstile #01") }
    var gateState by remember { mutableStateOf(GateRelayState.LOCKED) }
    var gateUnlockCountdown by remember { mutableIntStateOf(0) }
    var showAuditReportDialog by remember { mutableStateOf(false) }
    var showEmergencyModal by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf<String?>(null) }
    var fraudAlertMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(gateUnlockCountdown) {
        if (gateUnlockCountdown > 0) {
            delay(1000L)
            gateUnlockCountdown -= 1
            if (gateUnlockCountdown == 0 && gateState == GateRelayState.UNLOCKED) {
                gateState = GateRelayState.LOCKED
            }
        }
    }

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

    val kitchenTickets = remember {
        mutableStateListOf(
            KitchenTicket("TK-1081", "Himanshu Raj", "12104892", "Paneer Butter Masala Thali x1, Cold Coffee x1", "12:30 - 01:00 PM", KitchenTicketStatus.COOKING, 3, isPriority = true),
            KitchenTicket("TK-1082", "Vikram Patel", "12104239", "Chole Bhature Platter x1, Sweet Lassi x1", "12:30 - 01:00 PM", KitchenTicketStatus.QUEUED, 1),
            KitchenTicket("TK-1083", "Aarav Sharma", "12104901", "Dal Makhani with Jeera Rice x1, Extra Roti x2", "12:30 - 01:00 PM", KitchenTicketStatus.READY, 6),
            KitchenTicket("TK-1084", "Ananya Sen", "12105120", "Special Chicken Curry Meal x1", "12:30 - 01:00 PM", KitchenTicketStatus.COOKING, 5),
            KitchenTicket("TK-1085", "Priya Nair", "12104677", "Rajma Chawal Bowl x1, Fresh Lime Juice x1", "12:30 - 01:00 PM", KitchenTicketStatus.QUEUED, 2),
            KitchenTicket("TK-1079", "Rhea Kapoor", "12105561", "Palak Paneer Meal x1", "12:00 - 12:30 PM", KitchenTicketStatus.BUMPED, 12)
        )
    }

    val rawMaterialSilos = remember {
        listOf(
            RawMaterialSilo("Basmati Rice (Silo A)", 180f, 250f, "kg", "14 kg/hr", isLow = false),
            RawMaterialSilo("Wheat Flour / Atta", 95f, 150f, "kg", "12 kg/hr", isLow = false),
            RawMaterialSilo("Fresh Dairy Milk", 40f, 120f, "L", "18 L/hr", isLow = true),
            RawMaterialSilo("Refined Mustard Oil", 22f, 60f, "L", "4 L/hr", isLow = false),
            RawMaterialSilo("Fresh Cottage Cheese (Paneer)", 14f, 40f, "kg", "6 kg/hr", isLow = true)
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
            StaffStudentEntry("12104892", "Himanshu Raj", "Block A", "304", "Premium 3-Meal", false, "Lunch (Active)", true, 540, "None"),
            StaffStudentEntry("12104901", "Aarav Sharma", "Block B", "102", "Standard 2-Meal", true, "Lunch (Served)", true, 320, "Satvik"),
            StaffStudentEntry("12105120", "Ananya Sen", "Block H", "208", "Premium 3-Meal", true, "Lunch (Served)", true, 890, "None"),
            StaffStudentEntry("12103844", "Priyanshu Verma", "Block C", "415", "Flexi Dining", false, "Lunch (Served)", true, 180, "None"),
            StaffStudentEntry("12104677", "Priya Nair", "Block H", "112", "Standard 2-Meal", true, "Breakfast (Served)", false, 410, "Peanut Allergy"),
            StaffStudentEntry("12104239", "Vikram Patel", "Block A", "210", "Premium 3-Meal", false, "Yesterday Dinner", false, 650, "Gluten Sensitive"),
            StaffStudentEntry("12105561", "Rhea Kapoor", "Block D", "108", "Standard 2-Meal", true, "Breakfast (Served)", false, 280, "None"),
            StaffStudentEntry("12103390", "Kunal Mehra", "Block B", "401", "Premium 3-Meal", false, "Not Arrived", false, 75, "None")
        )
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
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
                                        text = "CAMPUS ERP OPERATIONS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangePrimary,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    PulsingRadarDot(color = Color(0xFF10B981), dotSize = 8.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE TELEMETRY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                                Text(
                                    text = "Central Dining Complex • Dr. K. Sharma",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Edge Terminal Node 01 • Firmware v4.2.1 • Latency 14ms",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
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
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Student App",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { showAuditReportDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Assessment,
                                    contentDescription = "Audit Report",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                                    contentDescription = "Logout",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Sensors,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HARDWARE RELAYS:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OPTICAL SONY IMX 60FPS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                                Text(
                                    text = " • 13.56MHz RFID NFC ARMED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (gateState) {
                                    GateRelayState.LOCKED -> Color(0xFF1E293B)
                                    GateRelayState.UNLOCKED -> Color(0xFF065F46)
                                    GateRelayState.EMERGENCY_RELEASE -> Color(0xFF92400E)
                                    GateRelayState.LOCKDOWN -> Color(0xFF991B1B)
                                },
                                border = BorderStroke(1.dp, when (gateState) {
                                    GateRelayState.LOCKED -> Color(0xFF64748B)
                                    GateRelayState.UNLOCKED -> Color(0xFF10B981)
                                    GateRelayState.EMERGENCY_RELEASE -> Color(0xFFF59E0B)
                                    GateRelayState.LOCKDOWN -> Color(0xFFEF4444)
                                })
                            ) {
                                Text(
                                    text = when (gateState) {
                                        GateRelayState.LOCKED -> "GATE 01: LOCKED [ARMED]"
                                        GateRelayState.UNLOCKED -> "GATE 01: UNLOCKED (${gateUnlockCountdown}s)"
                                        GateRelayState.EMERGENCY_RELEASE -> "EMERGENCY HOLD: OPEN"
                                        GateRelayState.LOCKDOWN -> "SECURITY LOCKDOWN ACTIVE"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .padding(3.dp)
                    ) {
                        listOf(
                            "Turnstile Gate" to Icons.Outlined.QrCodeScanner,
                            "Kitchen KDS" to Icons.Outlined.SoupKitchen,
                            "Queue Rush" to Icons.Outlined.Groups,
                            "Stock & Waste" to Icons.Outlined.Inventory2,
                            "Audit Dossier" to Icons.Outlined.Badge
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
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = tab.second,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tab.first,
                                        fontSize = 9.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
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
        containerColor = Color(0xFF0B132B)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            fraudAlertMessage?.let { fraud ->
                Surface(
                    color = Color(0xFFEF4444),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "SECURITY INTERCEPT: ANTI-PASSBACK FRAUD", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                                Text(text = fraud, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                            }
                        }
                        IconButton(onClick = { fraudAlertMessage = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            bannerMessage?.let { msg ->
                Surface(
                    color = Color(0xFF10B981),
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
                            Text(text = msg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Text(
                            text = "DISMISS",
                            fontSize = 10.sp,
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
                    EnterpriseTelemetryStrip(
                        currentOccupancy = queueStatus.currentCount,
                        maxCapacity = queueStatus.maxCapacity,
                        servedCount = 1428,
                        totalExpected = 1800,
                        todayRevenue = 95550,
                        wasteKgSaved = 33.8f,
                        activeKitchenTickets = kitchenTickets.count { it.status != KitchenTicketStatus.BUMPED }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                when (selectedTab) {
                    0 -> {
                        item {
                            EnterpriseTurnstileTab(
                                gateState = gateState,
                                gateCountdown = gateUnlockCountdown,
                                activeRecord = activeScannedRecord,
                                tokenInput = tokenInput,
                                onTokenInputChange = { tokenInput = it },
                                onSearchToken = { query ->
                                    if (query.contains("fraud", ignoreCase = true) || query.contains("duplicate", ignoreCase = true)) {
                                        fraudAlertMessage = "Token $query already logged at Turnstile Gate #02 at 12:41 PM. Entry Rejected."
                                        gateState = GateRelayState.LOCKED
                                    } else {
                                        val match = recentScans.find {
                                            it.tokenId.contains(query, ignoreCase = true) ||
                                                    it.rollNo.contains(query, ignoreCase = true) ||
                                                    it.studentName.contains(query, ignoreCase = true)
                                        }
                                        if (match != null) {
                                            activeScannedRecord = match
                                            bannerMessage = "Pass Authenticated: ${match.studentName} (${match.tokenId})"
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
                                            bannerMessage = "Token Verified from Cloud Ledger"
                                        }
                                    }
                                },
                                onSelectPreset = { record ->
                                    if (record.isDuplicateAttempt) {
                                        fraudAlertMessage = "Anti-passback alert: ${record.studentName} attempted duplicate swipe within 6 minutes."
                                        gateState = GateRelayState.LOCKED
                                    } else {
                                        activeScannedRecord = record
                                        tokenInput = record.tokenId
                                    }
                                },
                                onCycleGateUnlock = { record ->
                                    record.isServed = true
                                    gateState = GateRelayState.UNLOCKED
                                    gateUnlockCountdown = 5
                                    val idx = recentScans.indexOfFirst { it.tokenId == record.tokenId }
                                    if (idx != -1) {
                                        recentScans[idx] = record.copy(isServed = true)
                                    } else {
                                        recentScans.add(0, record.copy(isServed = true))
                                    }
                                    activeScannedRecord = record.copy(isServed = true)
                                    bannerMessage = "Gate #01 Solenoid Latch Actuated: Turnstile Open (5s Window)"
                                },
                                onEmergencyOverride = { showEmergencyModal = true },
                                recentScans = recentScans
                            )
                        }
                    }
                    1 -> {
                        item {
                            KitchenDisplaySystemTab(
                                tickets = kitchenTickets,
                                onAdvanceStatus = { ticket ->
                                    val nextStatus = when (ticket.status) {
                                        KitchenTicketStatus.QUEUED -> KitchenTicketStatus.COOKING
                                        KitchenTicketStatus.COOKING -> KitchenTicketStatus.READY
                                        KitchenTicketStatus.READY -> KitchenTicketStatus.BUMPED
                                        KitchenTicketStatus.BUMPED -> KitchenTicketStatus.BUMPED
                                    }
                                    val idx = kitchenTickets.indexOfFirst { it.ticketId == ticket.ticketId }
                                    if (idx != -1) {
                                        kitchenTickets[idx] = ticket.copy(status = nextStatus)
                                        bannerMessage = "Ticket ${ticket.ticketId} updated to ${nextStatus.name}"
                                    }
                                }
                            )
                        }
                    }
                    2 -> {
                        item {
                            StaffQueueControllerTab(
                                queueStatus = queueStatus,
                                onUpdateCount = { newCount, waitTime ->
                                    homeViewModel.updateQueueCount(newCount, waitTime)
                                    bannerMessage = "Queue Synced to 40k Students: $newCount in Central Mess"
                                },
                                onBroadcastAlert = {
                                    bannerMessage = "Push broadcast dispatched: Delay lunch advised to avoid peak rush"
                                }
                            )
                        }
                    }
                    3 -> {
                        item {
                            InventoryAndWastageTab(
                                silos = rawMaterialSilos,
                                menuStockMap = menuStockMap,
                                onToggleAvailability = { id ->
                                    val current = menuStockMap[id] ?: Pair(true, 40)
                                    val updated = Pair(!current.first, if (!current.first) 40 else 0)
                                    menuStockMap[id] = updated
                                    bannerMessage = if (updated.first) "Dish Marked IN STOCK (+40 portions)" else "Dish Marked 86-SOLD OUT"
                                },
                                onAdjustPortions = { id, delta ->
                                    val current = menuStockMap[id] ?: Pair(true, 40)
                                    val newPortions = (current.second + delta).coerceAtLeast(0)
                                    menuStockMap[id] = Pair(newPortions > 0, newPortions)
                                }
                            )
                        }
                    }
                    4 -> {
                        item {
                            StudentDossierAuditTab(
                                rosterList = studentRoster,
                                onToggleCheckIn = { student ->
                                    val idx = studentRoster.indexOfFirst { it.rollNo == student.rollNo }
                                    if (idx != -1) {
                                        val newState = !student.isCheckedIn
                                        studentRoster[idx] = student.copy(
                                            isCheckedIn = newState,
                                            lastMeal = if (newState) "Lunch (Turnstile Override)" else "Pending Arrival"
                                        )
                                        bannerMessage = if (newState) "${student.name} Check-In Authenticated" else "${student.name} Check-In Revoked"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEmergencyModal) {
        Dialog(onDismissRequest = { showEmergencyModal = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(2.dp, Color(0xFFEF4444))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "EMERGENCY TURNSTILE CONTROLS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Authorized Chief Warden overrides for mass egress or security incidents.",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            gateState = GateRelayState.EMERGENCY_RELEASE
                            gateUnlockCountdown = 0
                            showEmergencyModal = false
                            bannerMessage = "EMERGENCY: ALL TURNSTILES HELD UNLOCKED FOR EGRESS"
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                    ) {
                        Text("Open All Gates (Evacuation Release)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            gateState = GateRelayState.LOCKDOWN
                            gateUnlockCountdown = 0
                            showEmergencyModal = false
                            bannerMessage = "LOCKDOWN ACTIVE: ALL BARRIERS RIGIDLY LOCKED"
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("Lockdown All Gates (Security Freeze)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            gateState = GateRelayState.LOCKED
                            showEmergencyModal = false
                            bannerMessage = "Restored Normal Armed State on Gate #01"
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF64748B))
                    ) {
                        Text("Reset to Normal Armed Operation", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (showAuditReportDialog) {
        AuditReportPreviewDialog(onDismiss = { showAuditReportDialog = false })
    }
}

@Composable
private fun EnterpriseTelemetryStrip(
    currentOccupancy: Int,
    maxCapacity: Int,
    servedCount: Int,
    totalExpected: Int,
    todayRevenue: Int,
    wasteKgSaved: Float,
    activeKitchenTickets: Int
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EnterpriseKpiTile(
                modifier = Modifier.weight(1f),
                title = "MEALS SERVED",
                metric = "$servedCount",
                subtext = "Target $totalExpected (79.3%)",
                badge = "+12% vs yday",
                badgeColor = Color(0xFF10B981),
                accentColor = Color(0xFF10B981)
            )
            EnterpriseKpiTile(
                modifier = Modifier.weight(1f),
                title = "HALL DENSITY",
                metric = "$currentOccupancy",
                subtext = "Cap $maxCapacity (${(currentOccupancy * 100 / maxCapacity.coerceAtLeast(1))}%)",
                badge = if (currentOccupancy > 150) "HEAVY RUSH" else "OPTIMAL",
                badgeColor = if (currentOccupancy > 150) Color(0xFFEF4444) else OrangePrimary,
                accentColor = OrangePrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EnterpriseKpiTile(
                modifier = Modifier.weight(1f),
                title = "TODAY'S COLLECTIONS",
                metric = "Rs. $todayRevenue",
                subtext = "Prepaid + Spot UPI",
                badge = "100% RECONCILED",
                badgeColor = Color(0xFF38BDF8),
                accentColor = Color(0xFF38BDF8)
            )
            EnterpriseKpiTile(
                modifier = Modifier.weight(1f),
                title = "WASTAGE PREVENTED",
                metric = "$wasteKgSaved kg",
                subtext = "Rs. 14,850 cost saved",
                badge = "70.4% REDUCTION",
                badgeColor = Color(0xFF10B981),
                accentColor = Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun EnterpriseKpiTile(
    modifier: Modifier = Modifier,
    title: String,
    metric: String,
    subtext: String,
    badge: String,
    badgeColor: Color,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFF334155))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badge,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = metric,
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = subtext,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EnterpriseTurnstileTab(
    gateState: GateRelayState,
    gateCountdown: Int,
    activeRecord: StaffScannedToken?,
    tokenInput: String,
    onTokenInputChange: (String) -> Unit,
    onSearchToken: (String) -> Unit,
    onSelectPreset: (StaffScannedToken) -> Unit,
    onCycleGateUnlock: (StaffScannedToken) -> Unit,
    onEmergencyOverride: () -> Unit,
    recentScans: List<StaffScannedToken>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
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
                        PulsingRadarDot(
                            color = if (gateState == GateRelayState.UNLOCKED) Color(0xFF10B981) else Color(0xFF38BDF8),
                            dotSize = 8.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TURNSTILE OPTICAL VIEWFINDER & RELAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "GATE #01",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = OrangePrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .border(
                            BorderStroke(
                                2.dp,
                                when (gateState) {
                                    GateRelayState.LOCKED -> OrangePrimary
                                    GateRelayState.UNLOCKED -> Color(0xFF10B981)
                                    GateRelayState.EMERGENCY_RELEASE -> Color(0xFFF59E0B)
                                    GateRelayState.LOCKDOWN -> Color(0xFFEF4444)
                                }
                            ),
                            RoundedCornerShape(16.dp)
                        )
                        .holographicSweep(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (gateState) {
                                GateRelayState.LOCKED -> Icons.Outlined.Lock
                                GateRelayState.UNLOCKED -> Icons.Outlined.LockOpen
                                GateRelayState.EMERGENCY_RELEASE -> Icons.Outlined.WarningAmber
                                GateRelayState.LOCKDOWN -> Icons.Outlined.Security
                            },
                            contentDescription = null,
                            tint = when (gateState) {
                                GateRelayState.LOCKED -> Color.White
                                GateRelayState.UNLOCKED -> Color(0xFF10B981)
                                GateRelayState.EMERGENCY_RELEASE -> Color(0xFFF59E0B)
                                GateRelayState.LOCKDOWN -> Color(0xFFEF4444)
                            },
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when (gateState) {
                                GateRelayState.LOCKED -> "Ready: Scan Student Token QR or Tap RFID"
                                GateRelayState.UNLOCKED -> "BARRIER UNLOCKED: PASS THROUGH (${gateCountdown}s)"
                                GateRelayState.EMERGENCY_RELEASE -> "EMERGENCY HOLD: ROTATING ARMS FREED"
                                GateRelayState.LOCKDOWN -> "ALL ARMS LOCKED RIGIDLY"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Edge Latency 14ms • Anti-Passback Algorithm Active",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Field Verification Swipes:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "Emergency Gate Controls",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.clickable(onClick = onEmergencyOverride)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentScans) { scan ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF0F172A),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.bouncyClick { onSelectPreset(scan) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (scan.isServed) Icons.Outlined.CheckCircle else Icons.Outlined.QrCode,
                                    contentDescription = null,
                                    tint = if (scan.isServed) Color(0xFF10B981) else OrangePrimary,
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
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF450A0A),
                            border = BorderStroke(1.dp, Color(0xFFEF4444)),
                            modifier = Modifier.bouncyClick {
                                onSelectPreset(
                                    StaffScannedToken(
                                        tokenId = "BP-PASS-DUP-9912",
                                        studentName = "Kunal Mehra",
                                        rollNo = "12103390",
                                        hostelRoom = "Block B 401",
                                        mealType = "Lunch",
                                        planName = "Flexi Plan",
                                        isPureVeg = false,
                                        itemsSummary = "Attempted Duplicate",
                                        scanTime = "12:46 PM",
                                        isDuplicateAttempt = true
                                    )
                                )
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Test Duplicate Fraud", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                        placeholder = { Text("Search Token ID or Roll #", fontSize = 12.sp, color = Color(0xFF64748B)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
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

        Spacer(modifier = Modifier.height(14.dp))

        activeRecord?.let { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.5.dp, if (record.isServed) Color(0xFF64748B) else Color(0xFF10B981))
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
                                    .background(if (record.isServed) Color(0xFF334155) else Color(0xFF065F46)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (record.isServed) Icons.Outlined.Check else Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = if (record.isServed) Color(0xFF94A3B8) else Color(0xFF10B981),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = record.studentName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Roll: ${record.rollNo} • ${record.hostelRoom}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (record.isServed) Color(0xFF334155) else Color(0xFF065F46)
                        ) {
                            Text(
                                text = if (record.isServed) "SERVED" else "VALIDATED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = if (record.isServed) Color(0xFF94A3B8) else Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "MEAL SUBSCRIPTION", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text(text = record.planName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "SLOT TIMING", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text(text = record.mealType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "ORDER ENTITLEMENT / PRE-ORDER ITEMS:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = record.itemsSummary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onCycleGateUnlock(record) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .bouncyClick { onCycleGateUnlock(record) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (record.isServed) Color(0xFF475569) else Color(0xFF10B981)
                        ),
                        enabled = !record.isServed
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (record.isServed) Icons.Filled.Check else Icons.Outlined.LockOpen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (record.isServed) "Meal Delivered • Turnstile Cycle Complete" else "Actuate Turnstile Solenoid & Mark Served",
                                fontSize = 13.sp,
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
            text = "Hardware Verification Ledger",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "High-speed encrypted audit trail across turnstile gates",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(10.dp))

        recentScans.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
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
                                .background(if (item.isServed) Color(0xFF065F46) else Color(0xFF7C2D12)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.studentName.take(1),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isServed) Color(0xFF10B981) else OrangePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.studentName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${item.rollNo} • ${item.itemsSummary}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
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
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (item.isServed) Color(0xFF065F46) else Color(0xFF7C2D12)
                        ) {
                            Text(
                                text = if (item.isServed) "SERVED" else "PENDING",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isServed) Color(0xFF10B981) else OrangePrimary,
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
private fun KitchenDisplaySystemTab(
    tickets: List<KitchenTicket>,
    onAdvanceStatus: (KitchenTicket) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ACTIVE") }

    val filteredTickets = remember(selectedFilter, tickets) {
        when (selectedFilter) {
            "QUEUED" -> tickets.filter { it.status == KitchenTicketStatus.QUEUED }
            "COOKING" -> tickets.filter { it.status == KitchenTicketStatus.COOKING }
            "READY" -> tickets.filter { it.status == KitchenTicketStatus.READY }
            else -> tickets.filter { it.status != KitchenTicketStatus.BUMPED }
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
                    text = "Live Kitchen Display System (KDS)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "High-velocity live line production tickets for chef & assembly staff",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ACTIVE TICKETS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text("${tickets.count { it.status != KitchenTicketStatus.BUMPED }}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("COOKING NOW", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                    Text("${tickets.count { it.status == KitchenTicketStatus.COOKING }}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("READY TO BUMP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    Text("${tickets.count { it.status == KitchenTicketStatus.READY }}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("AVG SLA SPEED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    Text("3m 42s", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF38BDF8))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("ACTIVE", "QUEUED", "COOKING", "READY").forEach { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) OrangePrimary else Color(0xFF1E293B),
                    border = BorderStroke(1.dp, if (isSelected) OrangePrimary else Color(0xFF334155)),
                    modifier = Modifier
                        .weight(1f)
                        .bouncyClick { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        filteredTickets.forEach { ticket ->
            val statusColor = when (ticket.status) {
                KitchenTicketStatus.QUEUED -> Color(0xFF38BDF8)
                KitchenTicketStatus.COOKING -> OrangePrimary
                KitchenTicketStatus.READY -> Color(0xFF10B981)
                KitchenTicketStatus.BUMPED -> Color(0xFF64748B)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.5.dp, statusColor)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = statusColor.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, statusColor)
                            ) {
                                Text(
                                    text = ticket.ticketId,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ticket.studentName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (ticket.elapsedMin > 5) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF0F172A),
                            border = BorderStroke(1.dp, if (ticket.elapsedMin > 5) Color(0xFFEF4444) else Color(0xFF334155))
                        ) {
                            Text(
                                text = "Elapsed: ${ticket.elapsedMin}m",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ticket.elapsedMin > 5) Color(0xFFEF4444) else Color(0xFF94A3B8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = ticket.orderSummary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Slot: ${ticket.slot}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Button(
                            onClick = { onAdvanceStatus(ticket) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                            modifier = Modifier.bouncyClick { onAdvanceStatus(ticket) }
                        ) {
                            Text(
                                text = when (ticket.status) {
                                    KitchenTicketStatus.QUEUED -> "Start Cooking"
                                    KitchenTicketStatus.COOKING -> "Mark Ready"
                                    KitchenTicketStatus.READY -> "Bump / Dispatched"
                                    KitchenTicketStatus.BUMPED -> "Completed"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
        occupancyFraction < 0.45f -> Color(0xFF10B981)
        occupancyFraction < 0.75f -> Color(0xFFF59E0B)
        else -> Color(0xFFEF4444)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Central Dining Hall Live Headcount Control",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Pushes real-time Firestore queue broadcasts to all 40,000 campus app instances",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(rushColor.copy(alpha = 0.12f))
                        .border(BorderStroke(4.dp, rushColor), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$customOccupancy",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = rushColor
                        )
                        Text(
                            text = "of $maxCap capacity",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
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
                    text = "Instant Turnstile Headcount Stepper",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
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
                            color = if (delta > 0) OrangePrimary.copy(alpha = 0.15f) else Color(0xFF0F172A),
                            border = BorderStroke(1.dp, if (delta > 0) OrangePrimary else Color(0xFF334155))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (delta > 0) "+$delta" else "$delta",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (delta > 0) OrangePrimary else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Preset Campus Rush Profiles (One-Tap Override)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("LOW RUSH", 45, Color(0xFF10B981)),
                        Triple("MODERATE", 134, Color(0xFFF59E0B)),
                        Triple("PEAK RUSH", 188, Color(0xFFEF4444))
                    ).forEach { preset ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .bouncyClick {
                                    customOccupancy = preset.second
                                    val wait = when (preset.first) {
                                        "LOW RUSH" -> 4
                                        "MODERATE" -> 10
                                        else -> 22
                                    }
                                    onUpdateCount(preset.second, wait)
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = preset.third.copy(alpha = 0.12f),
                            border = BorderStroke(1.5.dp, preset.third)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = preset.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = preset.third
                                )
                                Text(
                                    text = "${preset.second} students",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Campus Rush Broadcast Notification Engine",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Dispatches push alert advising students to delay visit by 15 mins",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBroadcastAlert,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .bouncyClick(onClick = onBroadcastAlert),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Icon(imageVector = Icons.Outlined.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Broadcast Peak Rush Notification", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InventoryAndWastageTab(
    silos: List<RawMaterialSilo>,
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
        Text(
            text = "Raw Material Silos & Kitchen Telemetry",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Real-time stock burn rate and automated reorder alerts",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        silos.forEach { silo ->
            val fraction = (silo.currentQty / silo.maxQty).coerceIn(0f, 1f)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, if (silo.isLow) Color(0xFFF59E0B) else Color(0xFF334155))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = silo.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (silo.isLow) Color(0xFFF59E0B).copy(alpha = 0.2f) else Color(0xFF10B981).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (silo.isLow) "LOW STOCK" else "OPTIMAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = if (silo.isLow) Color(0xFFF59E0B) else Color(0xFF10B981),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${silo.currentQty.toInt()} ${silo.unit} / ${silo.maxQty.toInt()} ${silo.unit} (${(fraction * 100).toInt()}%)", fontSize = 11.sp, color = Color.White)
                        Text(text = "Burn: ${silo.burnRate}", fontSize = 10.sp, color = Color(0xFF94A3B8))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (silo.isLow) Color(0xFFF59E0B) else Color(0xFF10B981),
                        trackColor = Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Dynamic 86-List & Dish Availability",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Sold-out dishes update student menus instantly to eliminate refund disputes",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) OrangePrimary else Color(0xFF1E293B),
                    border = BorderStroke(1.dp, if (isSelected) OrangePrimary else Color(0xFF334155)),
                    modifier = Modifier.bouncyClick { selectedCategory = cat }
                ) {
                    Text(
                        text = cat,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        filteredItems.forEach { item ->
            val stockInfo = menuStockMap[item.id] ?: Pair(true, 40)
            val isInStock = stockInfo.first
            val remainingPortions = stockInfo.second

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = if (isInStock) Color(0xFF1E293B) else Color(0xFF161E2E)),
                border = BorderStroke(1.dp, if (isInStock) Color(0xFF334155) else Color(0xFFEF4444).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (item.isVeg) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isInStock) Color.White else Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Rs. ${item.price} • ${item.category.name} • Prep: ${item.prepTimeMinutes}m",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isInStock) Color(0xFF065F46) else Color(0xFF450A0A)
                            ) {
                                Text(
                                    text = if (isInStock) "$remainingPortions plates left" else "86-SOLD OUT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInStock) Color(0xFF10B981) else Color(0xFFEF4444),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (isInStock) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+10",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary,
                                    modifier = Modifier
                                        .bouncyClick { onAdjustPortions(item.id, 10) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "-10",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier
                                        .bouncyClick { onAdjustPortions(item.id, -10) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Switch(
                        checked = isInStock,
                        onCheckedChange = { onToggleAvailability(item.id) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFF334155)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentDossierAuditTab(
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
        Text(
            text = "Institutional Student Roster Dossier",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Cryptographic ledger of active campus subscriptions, dietary restrictions, and dues",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, roll no, or block...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = OrangePrimary
                ),
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (filterPureVeg) Color(0xFF065F46) else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (filterPureVeg) Color(0xFF10B981) else Color(0xFF334155)),
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pure Veg",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (filterPureVeg) Color.White else Color(0xFF94A3B8)
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF334155))
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
                                .background(if (student.isCheckedIn) Color(0xFF065F46) else Color(0xFF7C2D12)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.name.take(1),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (student.isCheckedIn) Color(0xFF10B981) else OrangePrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (student.isPureVeg) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF065F46)
                                    ) {
                                        Text(
                                            text = "VEG",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Roll: ${student.rollNo} • ${student.hostelBlock} Rm ${student.roomNo}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Text(
                                text = "${student.mealPlan} • Wallet: Rs. ${student.walletCredits} • Status: ${student.lastMeal}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (student.isCheckedIn) Color(0xFF10B981) else Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (student.isCheckedIn) Color(0xFF0F172A) else OrangePrimary,
                        border = BorderStroke(1.dp, if (student.isCheckedIn) Color(0xFF334155) else OrangePrimary),
                        modifier = Modifier.bouncyClick { onToggleCheckIn(student) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (student.isCheckedIn) Icons.Filled.Check else Icons.Filled.Add,
                                contentDescription = null,
                                tint = if (student.isCheckedIn) Color(0xFF10B981) else Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (student.isCheckedIn) "Verified" else "Check In",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.isCheckedIn) Color(0xFF10B981) else Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditReportPreviewDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CAMPUS AUDIT REPORT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangePrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Central Mess • EOD Reconciliation",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFF334155))
                Spacer(modifier = Modifier.height(12.dp))

                listOf(
                    "Total Meal Servings Verified" to "1,428 / 1,800",
                    "Optical Gate Turnstile Swipes" to "1,284",
                    "Mobile App Pre-Order Pickups" to "144",
                    "Anti-Passback Fraud Interceptions" to "3 Attempted",
                    "Food Wastage Prevented (kg)" to "33.8 kg",
                    "Wastage Cost Saved (AI Forecast)" to "Rs. 14,850",
                    "Prepaid Subscription Subsidies" to "Rs. 52,400",
                    "Spot Digital Wallet & UPI" to "Rs. 43,150",
                    "Gross Session Settlement" to "Rs. 95,550"
                ).forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.first, fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(text = item.second, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF64748B))
                    ) {
                        Icon(Icons.Outlined.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print Slip", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Icon(Icons.Outlined.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
