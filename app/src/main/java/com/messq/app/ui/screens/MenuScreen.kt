package com.messq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messq.app.data.MealCategory
import com.messq.app.data.MenuItem
import com.messq.app.data.MessQData
import com.messq.app.navigation.CartState
import com.messq.app.ui.components.DietaryBadge
import com.messq.app.ui.components.FoodThumbnail
import com.messq.app.ui.components.MessQBottomNav
import com.messq.app.ui.components.QuantityStepper
import com.messq.app.ui.theme.*
import com.messq.app.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    cartState: CartState,
    menuViewModel: MenuViewModel,
    onNavigateToPreOrder: () -> Unit,
    onBack: () -> Unit,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<MealCategory?>(null) }
    var vegOnlyFilter by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allItems = remember { MessQData.allMenuItems }

    val filteredItems = remember(selectedCategory, vegOnlyFilter, searchQuery) {
        allItems.filter { item ->
            val matchCategory = selectedCategory == null || item.category == selectedCategory
            val matchVeg = !vegOnlyFilter || item.isVeg
            val matchQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.tag.contains(searchQuery, ignoreCase = true)
            matchCategory && matchVeg && matchQuery
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "Campus Dining Menu",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${allItems.size} dishes prepared fresh daily",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (vegOnlyFilter) Color(0xFF16A34A).copy(alpha = 0.15f) else CardBackground,
                            border = BorderStroke(1.dp, if (vegOnlyFilter) Color(0xFF16A34A) else Divider),
                            modifier = Modifier.clickable { vegOnlyFilter = !vegOnlyFilter }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DietaryBadge(isVeg = true)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Veg Only",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (vegOnlyFilter) Color(0xFF16A34A) else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search dishes, meals, or tags...", fontSize = 13.sp, color = TextHint) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = OrangePrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WarmWhite,
                            unfocusedContainerColor = WarmWhite,
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = Divider
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            CategoryPill(
                                title = "All (${allItems.size})",
                                isSelected = selectedCategory == null,
                                onClick = { selectedCategory = null }
                            )
                        }
                        item {
                            CategoryPill(
                                title = "Breakfast (${MessQData.breakfastItems.size})",
                                isSelected = selectedCategory == MealCategory.BREAKFAST,
                                onClick = { selectedCategory = MealCategory.BREAKFAST }
                            )
                        }
                        item {
                            CategoryPill(
                                title = "Lunch (${MessQData.lunchItems.size})",
                                isSelected = selectedCategory == MealCategory.LUNCH,
                                onClick = { selectedCategory = MealCategory.LUNCH }
                            )
                        }
                        item {
                            CategoryPill(
                                title = "Dinner (${MessQData.dinnerItems.size})",
                                isSelected = selectedCategory == MealCategory.DINNER,
                                onClick = { selectedCategory = MealCategory.DINNER }
                            )
                        }
                        item {
                            CategoryPill(
                                title = "Snacks (${MessQData.snackItems.size})",
                                isSelected = selectedCategory == MealCategory.SNACKS,
                                onClick = { selectedCategory = MealCategory.SNACKS }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = cartState.totalItems > 0,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToPreOrder),
                        color = OrangePrimary,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${cartState.totalItems}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangePrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Rs. ${cartState.totalAmount}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Tap to choose pickup slot",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Book Slot",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                MessQBottomNav(
                    currentRoute = currentRoute,
                    cartItemCount = cartState.totalItems,
                    onNavigate = onNavigate
                )
            }
        },
        containerColor = WarmWhite
    ) { paddingValues ->
        if (filteredItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No dishes found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Try clearing search or filters",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { dish ->
                    MenuDishItemCard(
                        item = dish,
                        quantity = cartState.getQuantity(dish),
                        onAdd = { cartState.addItem(dish) },
                        onRemove = { cartState.removeItem(dish) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPill(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) OrangePrimary else CardBackground,
        border = if (isSelected) null else BorderStroke(1.dp, Divider),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun MenuDishItemCard(
    item: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(92.dp)
            ) {
                FoodThumbnail(
                    imageUrl = item.imageUrl,
                    fallbackColor = item.colorHex,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.tag.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(bottomEnd = 6.dp),
                        color = OrangePrimary,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = item.tag,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DietaryBadge(isVeg = item.isVeg)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${item.calories} kcal • ${item.proteinGrams}g protein • ${item.prepTimeMinutes}m prep",
                    fontSize = 10.sp,
                    color = TextHint
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rs. ${item.price}",
                        fontSize = 16.sp,
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
