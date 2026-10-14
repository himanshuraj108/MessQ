package com.messq.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messq.app.navigation.Screen
import com.messq.app.ui.theme.CardBackground
import com.messq.app.ui.theme.OrangePrimary
import com.messq.app.ui.theme.TextPrimary
import com.messq.app.ui.theme.TextSecondary

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)

@Composable
fun MessQBottomNav(
    currentRoute: String,
    cartItemCount: Int = 0,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem("Home", Icons.Outlined.Home, Screen.Home.route),
        NavItem("Menu", Icons.AutoMirrored.Outlined.MenuBook, Screen.Menu.route),
        NavItem("Pre-Order", Icons.Outlined.ShoppingBag, Screen.PreOrder.route, badgeCount = cartItemCount),
        NavItem("Wallet", Icons.Outlined.AccountBalanceWallet, Screen.Wallet.route),
        NavItem("Profile", Icons.Outlined.Person, Screen.Profile.route)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 18.dp,
        color = CardBackground
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) OrangePrimary else TextSecondary,
                    animationSpec = spring(),
                    label = "iconTint"
                )
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.12f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "iconScale"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .bouncyClick { onNavigate(item.route) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .scale(iconScale)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) OrangePrimary.copy(alpha = 0.14f) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        if (item.badgeCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(17.dp)
                                    .offset(x = 5.dp, y = (-2).dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (item.badgeCount > 9) "9+" else item.badgeCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                        color = if (isSelected) OrangePrimary else TextSecondary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Box(
                        modifier = Modifier
                            .size(width = if (isSelected) 14.dp else 0.dp, height = 2.5.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) OrangePrimary else Color.Transparent)
                    )
                }
            }
        }
    }
}
