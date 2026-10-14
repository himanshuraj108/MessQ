package com.messq.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messq.app.ui.theme.OrangeDark
import com.messq.app.ui.theme.OrangePrimary

@Composable
fun MessQBrandLogo(
    badgeSize: Dp = 86.dp,
    showTagline: Boolean = true,
    isDarkThemeOnHeader: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(badgeSize + 22.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkThemeOnHeader) Color.White.copy(alpha = 0.12f)
                        else OrangePrimary.copy(alpha = 0.10f)
                    )
            )

            Box(
                modifier = Modifier
                    .size(badgeSize + 10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkThemeOnHeader) Color.White.copy(alpha = 0.22f)
                        else OrangePrimary.copy(alpha = 0.18f)
                    )
            )

            Surface(
                modifier = Modifier.size(badgeSize),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 14.dp,
                border = BorderStroke(2.dp, Color.White)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "M",
                            fontSize = (badgeSize.value * 0.40f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = OrangePrimary,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Q",
                            fontSize = (badgeSize.value * 0.40f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.SansSerif,
                            color = OrangeDark,
                            letterSpacing = (-1).sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = (badgeSize.value * 0.14f).dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                    )
                }
            }
        }

        if (showTagline) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "MESSQ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                color = if (isDarkThemeOnHeader) Color.White else OrangePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "SMART CAMPUS DINING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = if (isDarkThemeOnHeader) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)
            )
        }
    }
}
