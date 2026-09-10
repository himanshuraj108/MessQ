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
    val isUser: Boolean
)

object MessQData {

    val breakfastItems = listOf(
        MenuItem(1, "Aloo Paratha", "Stuffed flatbread with spiced potato", 30, true, "2 pcs", MealCategory.BREAKFAST, 0xFFE8D5A3),
        MenuItem(2, "Idli Sambar", "Steamed rice cakes with lentil soup", 25, true, "3 pcs", MealCategory.BREAKFAST, 0xFFF5F0E8),
        MenuItem(3, "Poha", "Flattened rice with vegetables", 20, true, "1 plate", MealCategory.BREAKFAST, 0xFFFFF3CD),
        MenuItem(4, "Bread Butter", "Toasted bread with butter and jam", 15, true, "2 pcs", MealCategory.BREAKFAST, 0xFFFFE4B5),
        MenuItem(5, "Masala Dosa", "Crispy crepe with spiced potato filling", 35, true, "1 pc", MealCategory.BREAKFAST, 0xFFD4A76A),
        MenuItem(6, "Upma", "Semolina porridge with vegetables", 20, true, "1 bowl", MealCategory.BREAKFAST, 0xFFF0E68C),
        MenuItem(7, "Filter Coffee", "South Indian style coffee", 15, true, "1 cup", MealCategory.BREAKFAST, 0xFF8B6914),
        MenuItem(8, "Boiled Eggs", "Protein-rich boiled eggs", 20, false, "2 pcs", MealCategory.BREAKFAST, 0xFFFFF8DC)
    )

    val lunchItems = listOf(
        MenuItem(9, "Dal Tadka", "Yellow lentils tempered with spices", 40, true, "1 bowl", MealCategory.LUNCH, 0xFFFFCC80),
        MenuItem(10, "Paneer Butter Masala", "Cottage cheese in rich tomato gravy", 60, true, "1 bowl", MealCategory.LUNCH, 0xFFFF8A65),
        MenuItem(11, "Chicken Curry", "Spicy chicken in onion-tomato gravy", 80, false, "1 bowl", MealCategory.LUNCH, 0xFFA0522D),
        MenuItem(12, "Steamed Rice", "Plain basmati rice", 20, true, "1 plate", MealCategory.LUNCH, 0xFFF5F5DC),
        MenuItem(13, "Chapati", "Whole wheat flatbread", 10, true, "2 pcs", MealCategory.LUNCH, 0xFFDEB887),
        MenuItem(14, "Rajma Chawal", "Kidney beans curry with rice", 50, true, "1 plate", MealCategory.LUNCH, 0xFF8B0000),
        MenuItem(15, "Mixed Veg", "Seasonal vegetables stir-fried", 35, true, "1 bowl", MealCategory.LUNCH, 0xFF6B8E23),
        MenuItem(16, "Curd", "Fresh yogurt", 15, true, "1 cup", MealCategory.LUNCH, 0xFFFFFAFA)
    )

    val dinnerItems = listOf(
        MenuItem(17, "Roti", "Soft whole wheat flatbread", 8, true, "2 pcs", MealCategory.DINNER, 0xFFDEB887),
        MenuItem(18, "Chole Bhature", "Spiced chickpeas with fried bread", 55, true, "1 plate", MealCategory.DINNER, 0xFFCD853F),
        MenuItem(19, "Egg Bhurji", "Scrambled eggs with spices", 45, false, "1 plate", MealCategory.DINNER, 0xFFFFD700),
        MenuItem(20, "Palak Paneer", "Spinach and cottage cheese curry", 65, true, "1 bowl", MealCategory.DINNER, 0xFF3CB371),
        MenuItem(21, "Dal Makhani", "Slow cooked black lentils", 55, true, "1 bowl", MealCategory.DINNER, 0xFF8B4513),
        MenuItem(22, "Jeera Rice", "Basmati rice with cumin", 30, true, "1 plate", MealCategory.DINNER, 0xFFF5F5DC),
        MenuItem(23, "Khichdi", "Lentil and rice porridge", 35, true, "1 bowl", MealCategory.DINNER, 0xFFDAA520),
        MenuItem(24, "Gulab Jamun", "Milk solid dumplings in sugar syrup", 25, true, "2 pcs", MealCategory.DINNER, 0xFF8B4513)
    )

    val snackItems = listOf(
        MenuItem(25, "Samosa", "Crispy pastry with spiced potato filling", 15, true, "2 pcs", MealCategory.SNACKS, 0xFFD2691E),
        MenuItem(26, "Vada Pav", "Mumbai street burger", 20, true, "1 pc", MealCategory.SNACKS, 0xFFCD853F),
        MenuItem(27, "Masala Chai", "Spiced Indian tea", 10, true, "1 cup", MealCategory.SNACKS, 0xFFB8860B),
        MenuItem(28, "Cold Coffee", "Chilled blended coffee", 30, true, "1 glass", MealCategory.SNACKS, 0xFF6F4E37),
        MenuItem(29, "Maggi Noodles", "Instant noodles", 25, true, "1 plate", MealCategory.SNACKS, 0xFFFFD700),
        MenuItem(30, "Aloo Tikki", "Potato patties with chutneys", 20, true, "2 pcs", MealCategory.SNACKS, 0xFFDEB887),
        MenuItem(31, "Bread Omelette", "Egg omelette sandwiched in bread", 35, false, "1 pc", MealCategory.SNACKS, 0xFFFFCC02),
        MenuItem(32, "Fruit Juice", "Fresh seasonal juice", 25, true, "1 glass", MealCategory.SNACKS, 0xFFFF8C00)
    )

    val messLocations = listOf(
        MessLocation(1, "Main Mess", "Block A", CrowdLevel.MODERATE, 12, 18, isMain = true),
        MessLocation(2, "D-Block Mess", "Block D", CrowdLevel.LOW, 5, 8),
        MessLocation(3, "E-Block Mess", "Block E", CrowdLevel.MODERATE, 15, 20),
        MessLocation(4, "H-Block Mess", "Block H", CrowdLevel.HIGH, 25, 35)
    )

    val timeSlots = listOf(
        TimeSlot(1, "08:00 – 08:30 AM", SlotStatus.AVAILABLE, 8),
        TimeSlot(2, "08:30 – 09:00 AM", SlotStatus.AVAILABLE, 12),
        TimeSlot(3, "09:00 – 09:30 AM", SlotStatus.FILLING_FAST, 3),
        TimeSlot(4, "09:30 – 10:00 AM", SlotStatus.FILLING_FAST, 2),
        TimeSlot(5, "10:00 – 10:30 AM", SlotStatus.AVAILABLE, 15),
        TimeSlot(6, "10:30 – 11:00 AM", SlotStatus.AVAILABLE, 20)
    )

    val hourlyData = listOf(
        Pair("8am", 0.2f),
        Pair("10am", 0.5f),
        Pair("12pm", 0.95f),
        Pair("2pm", 0.6f),
        Pair("4pm", 0.3f),
        Pair("6pm", 0.75f),
        Pair("8pm", 0.4f)
    )

    val pickupDates = listOf(
        Triple("Mon", "16 Jun", true),
        Triple("Tue", "17 Jun", false),
        Triple("Wed", "18 Jun", false),
        Triple("Thu", "19 Jun", false),
        Triple("Fri", "20 Jun", false)
    )

    val assistantReplies = mapOf(
        "breakfast" to "Today's breakfast menu includes Aloo Paratha (Rs.30), Idli Sambar (Rs.25), Poha (Rs.20), Masala Dosa (Rs.35), and Filter Coffee (Rs.15). All items are pure vegetarian.",
        "crowd" to "Current crowd at Main Mess Block A is MODERATE with an estimated wait of 12 to 18 minutes. D-Block Mess has LOW crowd with only 5 to 8 min wait — a better option right now.",
        "slot" to "Available slots today:\n08:00 to 08:30 AM — 8 seats left\n08:30 to 09:00 AM — 12 seats left\n10:00 to 10:30 AM — 15 seats left\nShall I proceed to booking?",
        "veg" to "Vegetarian options today:\nBreakfast: Aloo Paratha, Idli Sambar, Poha, Masala Dosa\nLunch: Dal Tadka, Paneer Butter Masala, Rajma Chawal\nDinner: Chole Bhature, Palak Paneer, Dal Makhani",
        "nutrition" to "Paneer Butter Masala (per serving):\nCalories: 320 kcal\nProtein: 18g\nCarbohydrates: 12g\nFat: 22g\nCalcium content is high — good for bone health.",
        "default" to "Hello! I am MessQ Assistant. I can help you with:\n- Today's menu and prices\n- Live crowd status\n- Booking pickup slots\n- Nutrition information\n- Pre-ordering meals\nWhat would you like to know?"
    )
}
