# 📈 Stocks App

A modern Android application built with Jetpack Compose that provides real-time stock market data, watchlist management, and comprehensive stock analysis features.

## ✨ Features

- **Real-time Stock Data**: Get live stock prices, changes, and trading volumes
- **Top Gainers & Losers**: View the best and worst performing stocks of the day
- **Most Active Stocks**: Track the most actively traded stocks
- **Stock Search**: Search for specific stocks by symbol or company name
- **Company Overview**: Detailed company information and financial metrics
- **Watchlist Management**: Add stocks to your personal watchlist for quick access
- **User Preferences**: Customize app settings and search statistics
- **Modern UI**: Built with Material Design 3 and Jetpack Compose
- **Offline Support**: Room database for local data caching

## 🛠️ Tech Stack

### Architecture
- **MVVM Pattern** with Repository pattern
- **Dependency Injection** with Dagger Hilt
- **Clean Architecture** principles

### UI Framework
- **Jetpack Compose** - Modern declarative UI toolkit
- **Material Design 3** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation
- **Compose BOM** - Bill of materials for Compose versions

### Networking
- **Retrofit** - REST API client
- **OkHttp3** - HTTP client with logging interceptor
- **Gson** - JSON serialization/deserialization
- **Kotlinx Serialization** - Kotlin-native serialization

### Local Storage
- **Room Database** - Local data persistence
- **DataStore Preferences** - Settings and preferences storage

### Other Libraries
- **Material Icons Extended** - Extended icon set
- **Lifecycle ViewModel Compose** - ViewModels with Compose integration

## 🏗️ Project Structure

```
app/
├── src/main/java/com/tushant/stocksapp/
│   ├── MainActivity.kt                 # Main activity with Compose setup
│   ├── StockApplication.kt            # Application class with Hilt
│   ├── data/                          # Data layer
│   │   ├── api/
│   │   │   └── StockApi.kt           # Retrofit API interface
│   │   ├── database/
│   │   │   ├── StockDatabase.kt      # Room database
│   │   │   ├── WatchlistDao.kt       # Database access object
│   │   │   └── entities/             # Room entities
│   │   ├── models/                   # Data models
│   │   │   ├── StockItem.kt          # Stock data model
│   │   │   ├── StockOverview.kt      # Company overview model
│   │   │   ├── SymbolSearch.kt       # Search result model
│   │   │   └── TopGainersLosersResponse.kt
│   │   ├── preferences/              # User preferences
│   │   │   ├── PreferencesManager.kt
│   │   │   ├── PreferencesViewModel.kt
│   │   │   ├── SearchStatistics.kt
│   │   │   └── UserPreferences.kt
│   │   └── repository/
│   │       └── StockRepository.kt    # Data repository
│   ├── di/                           # Dependency injection modules
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   ├── ui/                           # UI layer
│   │   ├── components/               # Reusable UI components
│   │   │   ├── EmptyState.kt
│   │   │   ├── ErrorState.kt
│   │   │   ├── LoadingState.kt
│   │   │   ├── StockCard.kt
│   │   │   └── WatchlistBottomSheet.kt
│   │   ├── navigation/               # Navigation setup
│   │   ├── screens/                  # App screens/features
│   │   └── theme/                    # App theming
│   └── utils/                        # Utility classes
│       ├── Constants.kt
│       ├── Extensions.kt
│       ├── NetworkResult.kt
│       └── NetworkUtils.kt
```

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK with minimum API level 24
- Kotlin 2.0.21 or later
- Java 11

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/tushant-akar/StocksApp.git
   cd stocksapp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder

3. **API Key Configuration**
   - The app uses Alpha Vantage API for stock data
   - Update the API key in `app/src/main/java/com/tushant/stocksapp/utils/Constants.kt`
   ```kotlin
   const val API_KEY = "YOUR_ALPHA_VANTAGE_API_KEY"
   ```
   - Get your free API key from [Alpha Vantage](https://www.alphavantage.co/support/#api-key)

4. **Build and Run**
   - Sync project with Gradle files
   - Build the project (`Build > Make Project`)
   - Run on device or emulator

## 📱 App Features

### Stock Market Data
- Real-time stock prices and market data
- Top gainers, losers, and most active stocks
- Company overviews with financial metrics
- Stock search functionality

### Watchlist Management
- Add/remove stocks from personal watchlist
- Quick access to favorite stocks
- Persistent storage with Room database

### User Experience
- Modern Material Design 3 interface
- Dark/Light theme support
- Smooth animations and transitions
- Error handling and loading states

## 🔧 Configuration

### Minimum Requirements
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Build Configuration
- **Java Version**: 11
- **Kotlin Version**: 2.0.21
- **Gradle Version**: As defined in gradle wrapper
- **AGP Version**: As defined in libs.versions.toml

## 🧪 Testing

The project includes:
- **Unit Tests**: Located in `app/src/test/`
- **Instrumented Tests**: Located in `app/src/androidTest/`

Run tests using:
```bash
./gradlew test                    # Unit tests
./gradlew connectedAndroidTest    # Instrumented tests
```

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

If you have any questions or need help with setup, please open an issue in the GitHub repository.

## 🙏 Acknowledgments

- [Alpha Vantage](https://www.alphavantage.co/) for providing stock market API
- Android Jetpack team for the amazing Compose toolkit

---

**Built with ❤️ using Jetpack Compose and Modern Android Development practices**
