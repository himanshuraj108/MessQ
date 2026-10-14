package com.messq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messq.app.data.MessQData
import com.messq.app.data.SlotStatus
import com.messq.app.data.TimeSlot
import com.messq.app.navigation.CartState
import com.messq.app.ui.components.*
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.OrderViewModel

@Composable
fun PreOrderScreen(
    cartState: CartState,
    orderViewModel: OrderViewModel,
    onNavigateToOrderConfirmed: (slot: String, date: String) -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(MessQData.timeSlots.firstOrNull { it.status != SlotStatus.FULL }) }
    var selectedMess by remember { mutableStateOf(MessQData.messLocations.first()) }
    var instructions by remember { mutableStateOf("") }

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
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (currentStep > 1) currentStep-- else onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (currentStep) {
                                1 -> "Review Meal Order"
                                2 -> "Choose Pickup Slot"
                                else -> "Confirm & Pay"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    PreOrderStepIndicator(currentStep = currentStep)
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL PAYABLE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "Rs. ${cartState.totalAmount}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangePrimary
                        )
                    }

                    Button(
                        onClick = {
                            when (currentStep) {
                                1 -> if (cartState.totalItems > 0) currentStep = 2
                                2 -> if (selectedSlot != null) currentStep = 3
                                3 -> {
                                    val slotTime = selectedSlot?.timeRange ?: "12:30 - 01:00 PM"
                                    orderViewModel.placeOrder(
                                        cartItems = cartState.items.toList(),
                                        slot = slotTime,
                                        date = selectedDate,
                                        total = cartState.totalAmount
                                    )
                                    onNavigateToOrderConfirmed(slotTime, selectedDate)
                                }
                            }
                        },
                        enabled = (currentStep == 1 && cartState.totalItems > 0) || (currentStep == 2 && selectedSlot != null) || currentStep == 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier
                            .height(48.dp)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = when (currentStep) {
                                1 -> "Select Slot (${cartState.totalItems})"
                                2 -> "Review Payment"
                                else -> "Pay & Generate Token"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        when (currentStep) {
            1 -> Step1CartItems(
                cartState = cartState,
                instructions = instructions,
                onInstructionsChange = { instructions = it },
                paddingValues = paddingValues
            )
            2 -> Step2SlotPicker(
                selectedDate = selectedDate,
                onDateSelect = { selectedDate = it },
                selectedMess = selectedMess,
                onMessSelect = { selectedMess = it },
                selectedSlot = selectedSlot,
                onSlotSelect = { selectedSlot = it },
                paddingValues = paddingValues
            )
            3 -> Step3PaymentSummary(
                cartState = cartState,
                selectedSlot = selectedSlot,
                selectedDate = selectedDate,
                selectedMess = selectedMess,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun PreOrderStepIndicator(currentStep: Int) {
    val steps = listOf("Items", "Slot", "Payment")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, title ->
            val stepNumber = index + 1
            val isDone = stepNumber < currentStep
            val isCurrent = stepNumber == currentStep

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCurrent) {
                        PulsingRadarDot(color = OrangePrimary, dotSize = 8.dp, ringSize = 28.dp)
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isDone || isCurrent) OrangePrimary else Divider),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        } else {
                            Text(
                                text = "$stepNumber",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = if (isCurrent) OrangePrimary else TextSecondary
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .padding(horizontal = 8.dp)
                        .background(if (stepNumber < currentStep) OrangePrimary else Divider)
                )
            }
        }
    }
}

@Composable
private fun Step1CartItems(
    cartState: CartState,
    instructions: String,
    onInstructionsChange: (String) -> Unit,
    paddingValues: PaddingValues
) {
    if (cartState.items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = TextHint, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Your mess cart is empty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Add dishes from Today's Menu to pre-order", fontSize = 13.sp, color = TextSecondary)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Selected Dishes (${cartState.totalItems})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            cartState.items.forEach { cartItem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(modifier = Modifier.size(54.dp)) {
                                FoodThumbnail(
                                    imageUrl = cartItem.menuItem.imageUrl,
                                    fallbackColor = cartItem.menuItem.colorHex,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DietaryBadge(isVeg = cartItem.menuItem.isVeg)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cartItem.menuItem.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Rs. ${cartItem.menuItem.price} each",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        QuantityStepper(
                            quantity = cartItem.quantity,
                            onAdd = { cartState.addItem(cartItem.menuItem) },
                            onRemove = { cartState.removeItem(cartItem.menuItem) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Special Kitchen Instructions (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = instructions,
                onValueChange = onInstructionsChange,
                placeholder = { Text("e.g., Less spicy, pack in eco-container, extra chutney", fontSize = 12.sp, color = TextHint) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Divider
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Step2SlotPicker(
    selectedDate: String,
    onDateSelect: (String) -> Unit,
    selectedMess: com.messq.app.data.MessLocation,
    onMessSelect: (com.messq.app.data.MessLocation) -> Unit,
    selectedSlot: TimeSlot?,
    onSlotSelect: (TimeSlot) -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("1. Select Pickup Dining Hall", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        MessQData.messLocations.forEach { loc ->
            val isSelected = loc.id == selectedMess.id
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .bouncyClick { onMessSelect(loc) },
                color = if (isSelected) OrangeLight.copy(alpha = 0.5f) else CardBackground,
                border = BorderStroke(1.5.dp, if (isSelected) OrangePrimary else Divider),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(loc.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(loc.block, fontSize = 12.sp, color = TextSecondary)
                    }
                    if (isSelected) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = OrangePrimary)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("2. Select Pickup Day", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Today", "Tomorrow").forEach { day ->
                val isSelected = selectedDate == day
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) OrangePrimary else CardBackground,
                    border = if (isSelected) null else BorderStroke(1.dp, Divider),
                    modifier = Modifier
                        .weight(1f)
                        .bouncyClick { onDateSelect(day) }
                ) {
                    Text(
                        text = day,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else TextPrimary,
                        modifier = Modifier.padding(vertical = 12.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("3. Select Time Window", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Counters enforce strict 30-min pickup windows to eliminate lines", fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))

        MessQData.timeSlots.forEach { slot ->
            val isSelected = selectedSlot?.id == slot.id
            val isFull = slot.status == SlotStatus.FULL

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .bouncyClick(enabled = !isFull) { onSlotSelect(slot) },
                color = when {
                    isFull -> Color(0xFFF1F5F9)
                    isSelected -> OrangeLight.copy(alpha = 0.5f)
                    else -> CardBackground
                },
                border = BorderStroke(
                    1.5.dp,
                    if (isSelected) OrangePrimary else Divider
                ),
                shadowElevation = if (isSelected) 2.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = if (isSelected) OrangePrimary else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = slot.timeRange,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isFull) TextHint else TextPrimary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (slot.status) {
                            SlotStatus.AVAILABLE -> Color(0xFFDCFCE7)
                            SlotStatus.FILLING_FAST -> Color(0xFFFEF3C7)
                            SlotStatus.FULL -> Color(0xFFFEE2E2)
                        }
                    ) {
                        Text(
                            text = when (slot.status) {
                                SlotStatus.AVAILABLE -> "${slot.seatsLeft} slots left"
                                SlotStatus.FILLING_FAST -> "Filling fast (${slot.seatsLeft})"
                                SlotStatus.FULL -> "Sold Out"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (slot.status) {
                                SlotStatus.AVAILABLE -> Color(0xFF16A34A)
                                SlotStatus.FILLING_FAST -> Color(0xFFD97706)
                                SlotStatus.FULL -> Color(0xFFDC2626)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun Step3PaymentSummary(
    cartState: CartState,
    selectedSlot: TimeSlot?,
    selectedDate: String,
    selectedMess: com.messq.app.data.MessLocation,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Order & Token Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pickup Mess:", fontSize = 13.sp, color = TextSecondary)
                    Text(selectedMess.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pickup Window:", fontSize = 13.sp, color = TextSecondary)
                    Text("$selectedDate • ${selectedSlot?.timeRange}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Divider)

                cartState.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.menuItem.name} x${item.quantity}", fontSize = 13.sp, color = TextPrimary)
                        Text("Rs. ${item.menuItem.price * item.quantity}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Divider)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Eco Packaging Fee", fontSize = 13.sp, color = TextSecondary)
                    Text("FREE", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Rs. ${cartState.totalAmount}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = OrangePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Payment Method", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .holographicSweep(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0F172A),
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = OrangePrimary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("BitePass Digital Wallet", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Available Balance: Rs. 500", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
                Icon(Icons.Filled.Check, contentDescription = null, tint = OrangePrimary)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
