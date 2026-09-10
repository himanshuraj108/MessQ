package com.messq.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messq.app.data.MessQData
import com.messq.app.data.SlotStatus
import com.messq.app.data.TimeSlot
import com.messq.app.navigation.CartState
import com.messq.app.ui.theme.*

@Composable
fun PreOrderScreen(
    cartState: CartState,
    onNavigateToOrderConfirmed: () -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var selectedDateIndex by remember { mutableStateOf(0) }
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    var specialInstructions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Text(
                        text = "Pre-Order",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                StepIndicator(currentStep = currentStep)
                HorizontalDivider(color = Divider)
            }
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        when (currentStep) {
            1 -> Step1SelectItems(
                cartState = cartState,
                specialInstructions = specialInstructions,
                onInstructionsChange = { specialInstructions = it },
                onContinue = { if (cartState.totalItems > 0) currentStep = 2 },
                paddingValues = paddingValues
            )
            2 -> Step2ChooseSlot(
                selectedDateIndex = selectedDateIndex,
                onDateSelected = { selectedDateIndex = it },
                selectedSlot = selectedSlot,
                onSlotSelected = { selectedSlot = it },
                onConfirm = { if (selectedSlot != null) currentStep = 3 },
                paddingValues = paddingValues
            )
            3 -> Step3Confirm(
                cartState = cartState,
                selectedSlot = selectedSlot,
                selectedDateIndex = selectedDateIndex,
                onConfirmOrder = {
                    onNavigateToOrderConfirmed()
                    cartState.clear()
                },
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    val steps = listOf("Select Items", "Choose Slot", "Confirm")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, label ->
            val stepNum = index + 1
            val isDone = stepNum < currentStep
            val isCurrent = stepNum == currentStep

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isDone -> OrangePrimary
                            isCurrent -> OrangePrimary
                            else -> Surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "$stepNum",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) Color.White else TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isCurrent) OrangePrimary else TextSecondary
            )
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .padding(horizontal = 6.dp)
                        .background(if (stepNum < currentStep) OrangePrimary else Divider)
                )
            }
        }
    }
}

@Composable
private fun Step1SelectItems(
    cartState: CartState,
    specialInstructions: String,
    onInstructionsChange: (String) -> Unit,
    onContinue: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selected Items",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (cartState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your cart is empty",
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Go to Menu to add items",
                        fontSize = 13.sp,
                        color = TextHint
                    )
                }
            }
        } else {
            cartState.items.forEach { cartItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(cartItem.menuItem.colorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartItem.menuItem.name.take(2).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cartItem.menuItem.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Rs. ${cartItem.menuItem.price}",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Surface)
                        ) {
                            IconButton(
                                onClick = { cartState.removeItem(cartItem.menuItem) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Remove",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = "${cartItem.quantity}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            IconButton(
                                onClick = { cartState.addItem(cartItem.menuItem) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Special Instructions (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = specialInstructions,
                    onValueChange = onInstructionsChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. less spicy, no onions...", color = TextHint, fontSize = 13.sp) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = Divider,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    minLines = 2,
                    maxLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            HorizontalDivider(color = Divider)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                Text(
                    text = "Rs. ${cartState.totalAmount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    text = "Continue to Slot Selection  →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun Step2ChooseSlot(
    selectedDateIndex: Int,
    onDateSelected: (Int) -> Unit,
    selectedSlot: TimeSlot?,
    onSlotSelected: (TimeSlot) -> Unit,
    onConfirm: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text("Select Date", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MessQData.pickupDates.forEachIndexed { index, (day, date, _) ->
                val isSelected = selectedDateIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) OrangePrimary else Surface)
                        .clickable { onDateSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = day,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                        Text(
                            text = date,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.85f) else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Available Slots", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        MessQData.timeSlots.forEach { slot ->
            val isSelected = selectedSlot?.id == slot.id
            val slotColor = when (slot.status) {
                SlotStatus.AVAILABLE -> SlotAvailable
                SlotStatus.FILLING_FAST -> SlotFillingFast
                SlotStatus.FULL -> TextSecondary
            }
            val statusText = when (slot.status) {
                SlotStatus.AVAILABLE -> "Available"
                SlotStatus.FILLING_FAST -> "Filling fast"
                SlotStatus.FULL -> "Full"
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable {
                        if (slot.status != SlotStatus.FULL) onSlotSelected(slot)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.08f) else CardBackground
                ),
                border = if (isSelected) BorderStroke(1.5.dp, OrangePrimary) else BorderStroke(1.dp, Divider)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = slot.timeRange,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        if (slot.status == SlotStatus.FILLING_FAST) {
                            Text(
                                text = "Few seats left",
                                fontSize = 12.sp,
                                color = SlotFillingFast,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = statusText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = slotColor
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isSelected) OrangePrimary else Divider, CircleShape)
                                .background(if (isSelected) OrangePrimary else Color.Transparent),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            enabled = selectedSlot != null
        ) {
            Text(
                text = "Confirm Slot",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Step3Confirm(
    cartState: CartState,
    selectedSlot: TimeSlot?,
    selectedDateIndex: Int,
    onConfirmOrder: () -> Unit,
    paddingValues: PaddingValues
) {
    val date = MessQData.pickupDates.getOrNull(selectedDateIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text("Order Summary", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Pickup Date", "${date?.first ?: ""}, ${date?.second ?: ""}")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Pickup Time", selectedSlot?.timeRange ?: "Not selected")
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Mess Location", "Main Mess, Block A")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Divider)
                Text("Items", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(8.dp))
                cartState.items.forEach { cartItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${cartItem.menuItem.name}  x${cartItem.quantity}",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Rs. ${cartItem.menuItem.price * cartItem.quantity}",
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Divider)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        text = "Rs. ${cartState.totalAmount}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = OrangeBackground),
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Payment via MessQ Wallet", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Available balance: Rs. 850", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConfirmOrder,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
        ) {
            Text("Confirm Order", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
