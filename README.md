
# Finance

Finance is an Android application built with Kotlin for managing personal finances. It allows users to track income and expenses, organize transactions by category, and maintain an overview of their financial health.

## Features

- 📈 Income and expense tracking
- 🗂️ Transaction categorization
- 📊 Financial summaries and balances
- 💡 Simple and intuitive UI/UX

## Technologies Used

- **Kotlin**: Main programming language used for building the app.
- **Android Jetpack Components**:
  - **ViewModel**: Lifecycle-aware data handling.
  - **LiveData**: Observable data holder classes.
  - **Room**: Abstraction layer over SQLite for database management.
- **SQLite / Room**: Local database for storing transactions persistently.
- **RecyclerView**: Efficient and flexible list display for transaction data.
- **ViewBinding**: Type-safe access to views in layout files.
- **Material Design Components**: Modern UI components for consistent and intuitive design.
- **Gradle**: Build system for managing dependencies and project configuration.
- **API Integration**: Used for fetching or sending data to external services to enrich the app's functionality.
- **Web Scraping**: Employed to extract data from websites where APIs are not available or insufficient.

## Getting Started

### Prerequisites

- Android Studio (latest stable version)
- Android Emulator or physical device with Android 5.0 (API level 21) or above
- Git

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/receparslan4247/Finance-Kotlin.git
   ```

2. **Open the project in Android Studio**:
   - Select **File > Open** and locate the cloned folder.

3. **Build the project**:
   - Let Gradle sync all required dependencies.

4. **Run the app**:
   - Choose an emulator or connected device and press the **Run** button.

## Screenshots

| Home Screen                                                                                                                                | Gainer Screen                                                                                                                                | Loser Screen                                                                                                                                | Favourite Screen                                                                                                                                 | Search Screen                                                                                                                                | Detail Screen                                                                                                                                | Detail Screen                                                                                                                                  | Detail Screen                                                                                                                                  |
|--------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Home_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Gainer_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Loser_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Favourites_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Search_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Detail_Screen.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Detail_Screen_2.jpg) | ![image_alt](https://github.com/receparslan4247/Finance-Kotlin/blob/bf033d6a09b8294c5be6a7a586f31c8ce685080a/Screenshoots/Detail_Screen_3.jpg) |

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any feature, bug fix, or improvement.

1. Fork the repo
2. Create a new branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m "Add feature"`
4. Push to your fork: `git push origin feature/your-feature-name`
5. Submit a pull request

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more information.
