# ☀️ Weather App

A modern **Android weather application** built entirely with **Jetpack Compose**. It provides current weather information for multiple cities, complete with detailed views and robust features for managing locations and user preferences.

---

## ✨ Features

This application offers a comprehensive set of features for a polished user experience:

* ✅ **Multi-City Display:** View weather for initial hard-coded cities (**San Francisco, New York, Salt Lake City**).
* ✅ **Detailed Weather View:** Get comprehensive data including **temperature, humidity, and precipitation**.
* ✅ **Location Management:**
    * **Add** custom cities to your list.
    * **Remove** cities easily with an intuitive **swipe-to-delete** gesture.
* ✅ **User Preferences:**
    * **Toggle** between **Fahrenheit (°F)** and **Celsius (°C)**.
    * **Persistent** temperature unit preference across sessions.
* ✅ **Real-Time Data:** Fetches up-to-the-minute weather data from the **OpenWeatherMap API**.
* ✅ **Data Refresh:** Use a simple **pull-to-refresh** gesture to update data.
* ✅ **Optimized API Usage:** Implements **caching** to minimize API calls.

---

## 🛠 Tech Stack

The app is built using modern Android best practices and libraries:

### Architecture & Design Patterns
* **MVVM (Model-View-ViewModel)**
* **Repository Pattern**

### UI/UX
* **Jetpack Compose:** For declarative and modern UI development.
* **Material Design 3:** For contemporary theming and components.
* **Coil:** For asynchronous image loading (e.g., weather icons).
* **Navigation Compose:** For managing in-app navigation.

### Networking
* **Retrofit:** Type-safe HTTP client for API interaction.
* **OkHttp:** Efficient HTTP client handling.
* **Gson:** For JSON serialization/deserialization.

### Concurrency
* **Kotlin Coroutines:** For asynchronous and non-blocking operations.
* **StateFlow:** For reactive and observable data streams.

### Data Persistence
* **SharedPreferences:** For storing user preferences (e.g., temperature unit).

### Testing
* **JUnit:** Unit testing framework.
* **Mockito:** Mocking framework for dependency isolation.
* **Coroutines Test:** For testing coroutines-based code.

---

## 🌎 API Integration

This application relies on the **OpenWeatherMap API** for all weather data.

### Endpoints Used:

| Description | Endpoint |
| :--- | :--- |
| **Current Weather Data** | `/data/2.5/weather` |
| **Weather Icons** | `/img/wn/{icon}@2x.png` |

---
