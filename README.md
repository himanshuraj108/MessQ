# MessQ - Smart Campus Mess Management System

A high-scale smart mess management, queue tracking, and meal pre-ordering Android application built with **Jetpack Compose**, **Firebase**, and **Groq AI (LLaMA 3.3)**. Designed to serve 40,000+ students and campus dining halls.

## Features

- **Live Crowd Monitoring & Wait Time Analytics**: Real-time crowd gauge and hourly forecast to minimize queue wait times.
- **Pre-Order Meals & Time Slot Booking**: 3-step ordering flow (Review, Slot Selection, Confirmation) with automated token generation.
- **AI Dining Assistant**: Integrated AI assistant powered by Groq (LLaMA 3.3 70B) for dietary queries, menu recommendations, and mess status.
- **Dynamic Digital Menu**: Categorized menu (Breakfast, Lunch, Dinner, Snacks) with dietary indicators (Veg/Non-Veg) and real-time cart management.
- **Order Management & Digital Receipts**: Live order tracking, detailed digital receipts, and reorder history.
- **Account & Security**: Student and Staff authentication, password recovery via Firebase Auth, and campus wallet balance.

## Screens

| Screen | Description |
|---|---|
| Splash | MQ badge animated launch screen |
| Login / Register | Student and Staff role switcher with secure credentials login |
| Forgot Password | Two-state password recovery via Firebase reset link |
| Home Dashboard | Live crowd arc gauge, quick action grid, and today's specials |
| Queue Status | Real-time occupancy gauge, hourly crowd forecast chart, and multi-mess status |
| Menu | Category filters, quantity selectors, and floating checkout bar |
| Pre-Order | 3-step checkout: order review, date/slot picker, and wallet payment |
| Order Confirmed | Animated success confirmation, generated order ID, and digital receipt |
| Order History | Status-filtered order ledger with reorder capabilities |
| AI Assistant | Conversational interface for menu queries and dining recommendations |

## Tech Stack

- **UI Framework**: Jetpack Compose with Material 3
- **Navigation**: Jetpack Navigation Compose
- **Backend & Database**: Firebase Auth, Cloud Firestore
- **AI Integration**: Groq Cloud API (LLaMA 3.3 70B Versatile) via OkHttp
- **Asynchronous & Reactive**: Kotlin Coroutines, StateFlow, ViewModel
- **Image Loading**: Coil Compose

## Setup & Configuration

1. **Firebase Setup**:
   - Create a Firebase project at console.firebase.google.com.
   - Register an Android app with package name `com.messq.app`.
   - Download `google-services.json` and place it in the `app/` directory.
   - Enable Email/Password authentication and Cloud Firestore.

2. **Groq API Key**:
   - Obtain an API key from console.groq.com.
   - Add your key to `app/src/main/java/com/messq/app/Constants.kt` under `GROQ_API_KEY`.

## Build

```bash
./gradlew assembleDebug
```

Output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

*MessQ - Good Food. Better Days.*
