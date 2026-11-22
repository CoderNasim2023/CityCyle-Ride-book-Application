# CityCycle Ride Booking Application

CityCycle is a native Android application for discovering, browsing, and renting bicycles. It provides a complete local booking flow: users can create an account, sign in, explore available cycles, filter the catalog, select a rental period, and track future, ongoing, and past rentals.

The app uses SQLite for local persistence and a Java-based Android UI with XML layouts and ViewBinding.

## App Overview

CityCycle separates the experience into focused Android activities: authentication and profile screens manage user access, the home and catalog screens help users find cycles, and the rental and history screens handle booking and availability. `DBHelper` acts as the local data layer for SQLite records, seeded cycle information, and rental state updates.

## Features

- **User authentication:** Sign up, sign in, sign out, and persistent session handling with `SharedPreferences`
- **Profile management:** Store personal details and an optional profile photo
- **Cycle catalog:** Browse featured cycles or view the complete cycle list
- **Search and filtering:** Search cycles by name and filter the catalog by branch/location
- **Cycle details:** View cycle specifications, descriptions, pricing, and availability
- **Flexible rentals:** Book a cycle by day or by hour
- **Rental tracking:** Separate lists for future, ongoing, and past rentals
- **Automatic availability:** Restore cycle availability when a rental period expires
- **Cancellation support:** Remove future rental bookings
- **Contact actions:** Open a website, compose SMS, make a call, or share the app
- **Google Maps integration:** Open directions to the configured CityCycle location
- **Animated splash screen:** Lottie animations and app splash resources

## Technology Stack

- **Language:** Java
- **Platform:** Android
- **Minimum SDK:** API 25 (Android 7.1)
- **Target SDK:** API 35
- **UI:** XML layouts, Material Components, ViewBinding
- **Database:** SQLite through `SQLiteOpenHelper`
- **Libraries:**
  - AndroidX AppCompat
  - AndroidX Activity
  - ConstraintLayout
  - Material Components
  - Glide
  - Lottie
  - Chip Navigation Bar
- **Build system:** Gradle with Kotlin DSL

## Project Structure

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/citycycle/
│   │   │   ├── MainActivity.java
│   │   │   ├── SignupActivity.java
│   │   │   ├── SigninActivity.java
│   │   │   ├── AllCycleActivity.java
│   │   │   ├── DetailActivity.java
│   │   │   ├── RentActivity.java
│   │   │   ├── ActivityHistory.java
│   │   │   ├── ProfileActivity.java
│   │   │   ├── EditProfileActivity.java
│   │   │   └── DBHelper.java
│   │   └── res/
│   │       ├── layout/
│   │       ├── drawable/
│   │       ├── mipmap/
│   │       ├── raw/
│   │       └── values/
│   └── test/
└── build.gradle.kts
```

## Data Storage

CityCycle stores application data in a local SQLite database. The database includes tables for:

- Users and profile information
- Cycles, descriptions, branch, price, availability, and images
- Day-based rentals
- Hour-based rentals
- General bookings
- Favorite cycles

The `DBHelper` class manages table creation, seeded cycle data, rental inserts and cancellations, availability updates, and retrieval of current and historical rentals.

> This project is a local/demo application. Passwords are hashed with SHA-256, but production authentication should use server-side account management and secure password hashing such as Argon2 or bcrypt.

## Getting Started

### Requirements

- Android Studio with the Android SDK
- JDK 11 or newer
- An Android device or emulator running API 25 or higher

### Run the App

1. Clone the repository:

   ```bash
   git clone https://github.com/CoderNasim2023/CityCyle-Ride-book-Application.git
   cd CityCyle-Ride-book-Application
   ```

2. Open the project folder in Android Studio.

3. Allow Gradle to sync and download the dependencies.

4. Create or select an Android Virtual Device.

5. Run the `app` configuration.

You can also build it from the terminal:

```bash
./gradlew assembleDebug
```

The debug APK is created at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Basic Usage

1. Launch the app and complete the animated splash screen.
2. Create an account or sign in with an existing account.
3. Browse cycles on the home screen or open the complete catalog.
4. Search by cycle name or choose a branch from the filter.
5. Open a cycle to view details.
6. Choose day or hourly rental, select the start and end period, and confirm the booking.
7. Open **Activity** to view future, ongoing, and past rentals.
8. Open **Profile** to view or edit profile information and sign out.

## Testing

The repository includes example JVM and instrumented test files. Run the available unit tests with:

```bash
./gradlew test
```

Instrumented tests require a connected device or emulator:

```bash
./gradlew connectedAndroidTest
```

## Permissions

The application declares and can request the following permissions:

- **Phone:** Start a phone call from the in-app contact action
- **Internet:** Support external links and web-based Maps actions

## Known Limitations

- Data is stored only on the device and is not synchronized with a backend.
- Login state and user identity are stored locally through `SharedPreferences`.
- The application has demo contact details and location values.
- Cycle availability and rental data depend on the local SQLite database.
- The app does not include payment processing or real-time GPS tracking.

## Contributing

Contributions are welcome. Fork the repository, create a focused feature branch, and submit a pull request.

```bash
git checkout -b feature/your-feature-name
```

Keep changes consistent with the existing Java, XML, and package structure, and test the app on a local emulator or Android device before submitting.

## License

No license file is currently included. Add a license to the repository if you want to define how others may use, modify, or distribute this project.
