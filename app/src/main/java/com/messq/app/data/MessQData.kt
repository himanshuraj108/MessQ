package com.messq.app.data

data class MenuItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val isVeg: Boolean,
    val quantity: String,
    val category: MealCategory,
    val colorHex: Long,
    val imageUrl: String = "",
    val calories: Int = 300,
    val proteinGrams: Int = 10,
    val prepTimeMinutes: Int = 5,
    val tag: String = "",
    val isAvailable: Boolean = true
)

enum class MealCategory { BREAKFAST, LUNCH, DINNER, SNACKS }

data class CartItem(
    val menuItem: MenuItem,
    var quantity: Int = 1
)

data class MessLocation(
    val id: Int,
    val name: String,
    val block: String,
    val crowdLevel: CrowdLevel,
    val currentOccupancy: Int,
    val maxCapacity: Int,
    val waitTimeMin: Int,
    val waitTimeMax: Int,
    val isMain: Boolean = false
)

enum class CrowdLevel { LOW, MODERATE, HIGH }

data class TimeSlot(
    val id: Int,
    val timeRange: String,
    val status: SlotStatus,
    val seatsLeft: Int = 0
)

enum class SlotStatus { AVAILABLE, FILLING_FAST, FULL }

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = "Just now"
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Int,
    val isCredit: Boolean,
    val date: String,
    val time: String,
    val status: String = "Successful"
)

data class StudentProfile(
    val name: String,
    val rollNo: String,
    val hostelBlock: String,
    val roomNo: String,
    val mealPlan: String,
    val isPureVeg: Boolean,
    val qrToken: String
)

object MessQData {

    val studentProfile = StudentProfile(
        name = "Himanshu Raj",
        rollNo = "12104892",
        hostelBlock = "Block A (Boys Hostel)",
        roomNo = "Room 304",
        mealPlan = "Premium 3-Meal Plan",
        isPureVeg = false,
        qrToken = "MQ-PASS-12104892-2026"
    )

    val sampleTransactions = listOf(
        WalletTransaction("TXN-9021", "Breakfast Pre-Order (#MQ-8192)", 50, false, "Today", "08:15 AM"),
        WalletTransaction("TXN-8842", "Wallet Auto-Topup via UPI", 1000, true, "Yesterday", "06:40 PM"),
        WalletTransaction("TXN-7612", "Dinner - Paneer Thali (#MQ-7721)", 95, false, "14 Oct", "08:30 PM"),
        WalletTransaction("TXN-6510", "Snacks - Cold Coffee & Samosa", 45, false, "13 Oct", "05:10 PM"),
        WalletTransaction("TXN-5421", "Mess Security Deposit Refund", 500, true, "10 Oct", "11:20 AM")
    )

    val breakfastItems = listOf(
        MenuItem(
            1, "Aloo Paratha with Curd", "Two golden crisp stuffed flatbreads with fresh curd and mint pickle",
            30, true, "2 pcs", MealCategory.BREAKFAST, 0xFFE8D5A3,
            "https://images.unsplash.com/photo-1626074353765-517a681e40be?w=600&auto=format&fit=crop&q=80",
            320, 8, 5, "Bestseller"
        ),
        MenuItem(
            2, "Idli Sambar with Chutney", "Steamed fermented rice cakes with hot aromatic lentil sambar and coconut chutney",
            25, true, "3 pcs", MealCategory.BREAKFAST, 0xFFF5F0E8,
            "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=80",
            195, 6, 3, "Healthy Pick"
        ),
        MenuItem(
            3, "Indori Poha", "Light flattened rice tossed with turmeric, mustard seeds, crunchy peanuts and sev",
            20, true, "1 plate", MealCategory.BREAKFAST, 0xFFFFF3CD,
            "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=600&auto=format&fit=crop&q=80",
            210, 5, 2, "Quick Bite"
        ),
        MenuItem(
            4, "Crispy Masala Dosa", "Thin rice crepe stuffed with mashed spiced potatoes served with sambar",
            35, true, "1 pc", MealCategory.BREAKFAST, 0xFFD4A76A,
            "https://images.unsplash.com/photo-1668236543090-82eba5ee5976?w=600&auto=format&fit=crop&q=80",
            280, 7, 7, "Chef Special"
        ),
        MenuItem(
            5, "Boiled Eggs (Double)", "Farm fresh organic boiled eggs sprinkled with black pepper and rock salt",
            20, false, "2 pcs", MealCategory.BREAKFAST, 0xFFFFF8DC,
            "https://images.unsplash.com/photo-1582169296194-e4d644c48063?w=600&auto=format&fit=crop&q=80",
            155, 13, 2, "High Protein"
        ),
        MenuItem(
            6, "South Indian Filter Coffee", "Authentic strong chicory blend brewed coffee with frothed milk",
            15, true, "1 cup", MealCategory.BREAKFAST, 0xFF8B6914,
            "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=600&auto=format&fit=crop&q=80",
            90, 3, 2, "Must Try"
        )
    )

    val lunchItems = listOf(
        MenuItem(
            7, "Paneer Butter Masala Thali", "Rich cottage cheese in creamy tomato gravy, dal tadka, 3 rotis, rice, salad",
            75, true, "1 thali", MealCategory.LUNCH, 0xFFFF8A65,
            "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=600&auto=format&fit=crop&q=80",
            580, 22, 8, "Bestseller"
        ),
        MenuItem(
            8, "Special Chicken Curry Meal", "Homestyle chicken simmered in onion-tomato gravy with jeera rice and 2 chapatis",
            90, false, "1 meal", MealCategory.LUNCH, 0xFFA0522D,
            "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=600&auto=format&fit=crop&q=80",
            640, 34, 10, "Chef Special"
        ),
        MenuItem(
            9, "Punjabi Rajma Chawal", "Slow-cooked red kidney beans in thick spiced curry over fragrant basmati rice",
            50, true, "1 bowl", MealCategory.LUNCH, 0xFF8B0000,
            "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
            420, 16, 4, "Campus Favorite"
        ),
        MenuItem(
            10, "Dal Tadka & Steamed Rice", "Yellow lentils tempered with ghee, cumin and garlic served with hot rice",
            40, true, "1 plate", MealCategory.LUNCH, 0xFFFFCC80,
            "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=600&auto=format&fit=crop&q=80",
            360, 12, 3, "Classic"
        ),
        MenuItem(
            11, "Tawa Roti (3 Pieces)", "Freshly roasted whole wheat chapatis brushed with pure butter",
            15, true, "3 pcs", MealCategory.LUNCH, 0xFFDEB887,
            "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop&q=80",
            180, 6, 2, "Add-on"
        )
    )

    val dinnerItems = listOf(
        MenuItem(
            12, "Delhi Chole Bhature", "Spiced chickpea curry paired with two fluffy, ballooned deep-fried bhaturas",
            55, true, "1 plate", MealCategory.DINNER, 0xFFCD853F,
            "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46?w=600&auto=format&fit=crop&q=80",
            520, 15, 6, "Popular"
        ),
        MenuItem(
            13, "Dal Makhani with Jeera Rice", "Black lentils and kidney beans simmered overnight with cream and butter",
            60, true, "1 bowl", MealCategory.DINNER, 0xFF8B4513,
            "https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&auto=format&fit=crop&q=80",
            480, 18, 5, "Chef Special"
        ),
        MenuItem(
            14, "Egg Bhurji Pav Combo", "Spicy scrambled eggs with chopped onions and tomatoes with 2 butter toasted pavs",
            45, false, "1 plate", MealCategory.DINNER, 0xFFFFD700,
            "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=600&auto=format&fit=crop&q=80",
            390, 20, 5, "High Protein"
        ),
        MenuItem(
            15, "Palak Paneer with Rotis", "Smooth spinach gravy with tender cottage cheese cubes and 3 hot rotis",
            65, true, "1 meal", MealCategory.DINNER, 0xFF3CB371,
            "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop&q=80",
            450, 21, 7, "Nutritious"
        ),
        MenuItem(
            16, "Gulab Jamun (Warm)", "Two melt-in-the-mouth soft milk dumplings dipped in cardamom sugar syrup",
            25, true, "2 pcs", MealCategory.DINNER, 0xFF8B4513,
            "https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600&auto=format&fit=crop&q=80",
            260, 4, 1, "Dessert"
        )
    )

    val snackItems = listOf(
        MenuItem(
            17, "Samosa Chaat Platter", "Crushed samosas topped with spicy chole, sweetened yogurt and tangy chutneys",
            25, true, "1 plate", MealCategory.SNACKS, 0xFFD2691E,
            "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop&q=80",
            310, 7, 3, "Evening Hit"
        ),
        MenuItem(
            18, "Mumbai Vada Pav Duo", "Two spicy potato fritters in soft buns laced with garlic chutney and fried chilies",
            25, true, "2 pcs", MealCategory.SNACKS, 0xFFCD853F,
            "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=600&auto=format&fit=crop&q=80",
            340, 8, 3, "Bestseller"
        ),
        MenuItem(
            19, "Iced Cold Coffee with Scoop", "Creamy chilled coffee with rich cocoa dusting and vanilla ice cream topping",
            35, true, "1 glass", MealCategory.SNACKS, 0xFF6F4E37,
            "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=600&auto=format&fit=crop&q=80",
            220, 6, 2, "Refreshing"
        ),
        MenuItem(
            20, "Cheese Masala Maggi", "Classic Maggi noodles cooked with mixed vegetables, secret spices and melted cheese",
            30, true, "1 bowl", MealCategory.SNACKS, 0xFFFFD700,
            "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=600&auto=format&fit=crop&q=80",
            310, 8, 4, "Late Night"
        ),
        MenuItem(
            21, "Fresh Watermelon Juice", "Hydrating pure cold-pressed watermelon juice with mint and black salt",
            25, true, "1 glass", MealCategory.SNACKS, 0xFFFF8C00,
            "https://images.unsplash.com/photo-1589733955941-5eeaf752f6dd?w=600&auto=format&fit=crop&q=80",
            110, 2, 2, "Detox"
        )
    )

    val allMenuItems: List<MenuItem> = breakfastItems + lunchItems + dinnerItems + snackItems

    val messLocations = listOf(
        MessLocation(1, "Central Mess (Block A)", "Block A Ground Floor", CrowdLevel.MODERATE, 134, 200, 8, 12, isMain = true),
        MessLocation(2, "North Wing Cafeteria", "Block D Annex", CrowdLevel.LOW, 42, 180, 2, 5),
        MessLocation(3, "Engineering Block Food Court", "Block E 1st Floor", CrowdLevel.HIGH, 188, 220, 18, 25),
        MessLocation(4, "Girls Hostel Dining Hall", "Block H Dining Complex", CrowdLevel.LOW, 55, 160, 3, 6)
    )

    val timeSlots = listOf(
        TimeSlot(1, "08:00 - 08:30 AM", SlotStatus.AVAILABLE, 24),
        TimeSlot(2, "08:30 - 09:00 AM", SlotStatus.FILLING_FAST, 6),
        TimeSlot(3, "09:00 - 09:30 AM", SlotStatus.FULL, 0),
        TimeSlot(4, "12:30 - 01:00 PM", SlotStatus.AVAILABLE, 30),
        TimeSlot(5, "01:00 - 01:30 PM", SlotStatus.FILLING_FAST, 4),
        TimeSlot(6, "01:30 - 02:00 PM", SlotStatus.AVAILABLE, 18),
        TimeSlot(7, "07:30 - 08:00 PM", SlotStatus.AVAILABLE, 22),
        TimeSlot(8, "08:00 - 08:30 PM", SlotStatus.FILLING_FAST, 5)
    )

    val hourlyCrowd = listOf(
        Pair("8 AM", 0.35f),
        Pair("10 AM", 0.60f),
        Pair("12 PM", 0.92f),
        Pair("2 PM", 0.45f),
        Pair("4 PM", 0.25f),
        Pair("6 PM", 0.50f),
        Pair("8 PM", 0.88f),
        Pair("10 PM", 0.30f)
    )
}
