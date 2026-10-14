package com.messq.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.QueryBuilder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messq.app.data.CrowdLevel
import com.messq.app.data.MessLocation
import com.messq.app.data.MessQData
import com.messq.app.ui.components.*
import com.messq.app.ui.theme.*

@Composable
fun QueueScreen(onBack: () -> Unit) {
    val locations = MessQData.messLocations
    var selectedLocation by remember { mutableStateOf(locations.first()) }

    val percentage = (selectedLocation.currentOccupancy.toFloat() / selectedLocation.maxCapacity.toFloat()).coerceIn(0f, 1f)
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(1000),
        label = "queueArc"
    )

    val crowdColor = when {
        percentage < 0.45f -> Color(0xFF16A34A)
        percentage < 0.75f -> Color(0xFFEAB308)
        else -> Color(0xFFDC2626)
    }

    val crowdStatusText = when {
        percentage < 0.45f -> "LOW TRAFFIC"
        percentage < 0.75f -> "MODERATE CROWD"
        else -> "PEAK RUSH"
    }

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
                        Column {
                            Text(
                                text = "Live Queue & Crowd",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Real-time AI camera sensor feeds",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = crowdColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PulsingRadarDot(color = crowdColor, dotSize = 6.dp, ringSize = 14.dp)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = crowdStatusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = crowdColor
                            )
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(locations) { loc ->
                    val isSelected = loc.id == selectedLocation.id
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) OrangePrimary else CardBackground,
                        border = if (isSelected) null else BorderStroke(1.dp, Divider),
                        shadowElevation = if (isSelected) 3.dp else 0.dp,
                        modifier = Modifier.bouncyClick { selectedLocation = loc }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color.White
                                        else when (loc.crowdLevel) {
                                            CrowdLevel.LOW -> Color(0xFF16A34A)
                                            CrowdLevel.MODERATE -> Color(0xFFEAB308)
                                            CrowdLevel.HIGH -> Color(0xFFDC2626)
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loc.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .holographicSweep(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedLocation.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = selectedLocation.block,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(
                        modifier = Modifier.size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(170.dp)) {
                            drawArc(
                                color = Color(0xFFE2E8F0),
                                startAngle = 140f,
                                sweepAngle = 260f,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                            drawArc(
                                color = crowdColor,
                                startAngle = 140f,
                                sweepAngle = 260f * animatedPercentage,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round),
                                size = Size(size.width, size.height)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(percentage * 100).toInt()}%",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "${selectedLocation.currentOccupancy} / ${selectedLocation.maxCapacity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "Occupied",
                                fontSize = 10.sp,
                                color = TextHint
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatPill(
                            title = "Est. Wait Time",
                            value = "${selectedLocation.waitTimeMin}-${selectedLocation.waitTimeMax}m",
                            icon = Icons.Outlined.Timer
                        )
                        StatPill(
                            title = "Serving Speed",
                            value = "45 / min",
                            icon = Icons.Outlined.Groups
                        )
                        StatPill(
                            title = "Peak Time",
                            value = "1:00 PM",
                            icon = Icons.Outlined.Schedule
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                border = BorderStroke(1.dp, Color(0xFF86EFAC))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF16A34A).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.DirectionsWalk, contentDescription = null, tint = Color(0xFF16A34A))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Smart Recommendation",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "North Wing Cafeteria is 4 min walking distance and has under 3 min wait right now.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Live Counter Queues",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            CounterQueueRow(name = "Counter 1: Regular Thali & Rice", wait = "8 min wait", students = "14 in line", isFast = false)
            Spacer(modifier = Modifier.height(8.dp))
            CounterQueueRow(name = "Counter 2: Diet & Boiled Eggs", wait = "2 min wait", students = "3 in line", isFast = true)
            Spacer(modifier = Modifier.height(8.dp))
            CounterQueueRow(name = "Counter 3: Chai, Coffee & Snacks", wait = "3 min wait", students = "5 in line", isFast = true)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Today's Crowd Forecast",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        MessQData.hourlyCrowd.forEach { (time, fraction) ->
                            val barHeight = (100 * fraction).dp
                            val barColor = when {
                                fraction < 0.45f -> Color(0xFF16A34A)
                                fraction < 0.75f -> Color(0xFFEAB308)
                                else -> Color(0xFFDC2626)
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(barHeight)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(barColor)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = time,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Divider)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Best time to visit: 3:00 PM - 5:00 PM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                        Text(
                            text = "AI Forecast",
                            fontSize = 11.sp,
                            color = TextHint
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun StatPill(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(title, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun CounterQueueRow(
    name: String,
    wait: String,
    students: String,
    isFast: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Divider),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(students, fontSize = 11.sp, color = TextSecondary)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isFast) Color(0xFFDCFCE7) else OrangeLight
            ) {
                Text(
                    text = wait,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isFast) Color(0xFF16A34A) else OrangePrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
