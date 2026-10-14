package com.messq.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.messq.app.ui.theme.*

@Composable
fun DietaryBadge(isVeg: Boolean, modifier: Modifier = Modifier) {
    val borderColor = if (isVeg) Color(0xFF16A34A) else Color(0xFFDC2626)
    val dotColor = if (isVeg) Color(0xFF16A34A) else Color(0xFFDC2626)

    Box(
        modifier = modifier
            .size(15.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(2.5.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Composable
fun FoodThumbnail(
    imageUrl: String,
    fallbackColor: Long,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(fallbackColor).copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ShimmeringSpecialTag(
    text: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "tagShimmer")
    val sweep by transition.animateFloat(
        initialValue = -100f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAnim"
    )

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = OrangePrimary,
        modifier = modifier
            .drawBehind {
                val brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    start = Offset(sweep, 0f),
                    end = Offset(sweep + 60f, size.height)
                )
                drawRect(brush = brush)
            }
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
        )
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = quantity > 0,
        transitionSpec = {
            (scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)) + fadeIn()) togetherWith
                    (scaleOut(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut())
        },
        label = "stepperTransition",
        modifier = modifier
    ) { hasItems ->
        if (!hasItems) {
            Box(
                modifier = Modifier
                    .bouncyClick(onClick = onAdd)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(OrangePrimary, OrangeDark)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ADD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = Color.White
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = CardBackground,
                border = BorderStroke(1.5.dp, OrangePrimary),
                shadowElevation = 3.dp,
                modifier = Modifier.height(34.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .bouncyClick(onClick = onRemove),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Remove,
                            contentDescription = "Remove",
                            tint = OrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    AnimatedContent(
                        targetState = quantity,
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height } + fadeOut())
                        },
                        label = "numberRoll"
                    ) { count ->
                        Text(
                            text = "$count",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangePrimary,
                            modifier = Modifier.padding(horizontal = 9.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                            .bouncyClick(onClick = onAdd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
