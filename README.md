# Stack Overflow Android App

A native Android application that allows users to search and browse Stack Overflow questions and answers with a clean, modern interface.

## Features

- **Search Questions**: Search for Stack Overflow questions with real-time debounced search
- **Question Details**: View detailed question information including:
  - Question title, body, and tags
  - Answer count, view count, and score
  - Timestamps with relative time display (e.g., "2 hours ago")
  - Owner information and reputation
- **Answer Viewing**: Browse all answers for a specific question with:
  - Answer body with HTML rendering
  - Vote counts
  - Accepted answer indicators
  - Author information
- **Pagination**: Automatic infinite scroll loading for search results
- **Offline Detection**: No internet dialog with visual feedback
- **Modern UI**: Built with Jetpack Compose and Material3 design

## What the App Does

The Stack Overflow App provides a streamlined mobile experience for browsing Stack Overflow content. Users can search for programming questions, view detailed information about questions and their answers, and navigate through results seamlessly. The app fetches data from the official Stack Overflow API and presents it in an intuitive, mobile-friendly interface.

## Running the App

### Using the Pre-built APK

1. Download `stackOverflow.apk` from the root of this repository
2. Transfer the APK to your Android device
3. Enable "Install from Unknown Sources" in your device settings if prompted
4. Open the APK file and follow installation prompts
5. Launch the app from your app drawer

**Requirements:**
- Android device running Android 8.0 (API 26) or higher
- Internet connection for fetching Stack Overflow data

## Development Setup

### Prerequisites

Before building and running the codebase, ensure you have the following installed:

- **Java Development Kit (JDK)**: JDK 11 or higher
- **Android Studio**: Latest stable version (Koala or newer recommended)
- **Android SDK**: 
  - Compile SDK: API 36
  - Target SDK: API 35
  - Min SDK: API 26
- **Gradle**: 8.11.1 (included via Gradle Wrapper)
- **Git**: For version control

### Key Dependencies

#### Core Android Libraries
- **Kotlin**: 2.0.0
- **Android Gradle Plugin**: 8.9.3
- **AndroidX Core KTX**: 1.17.0
- **Lifecycle Runtime KTX**: 2.10.0

#### UI Framework
- **Jetpack Compose BOM**: 2026.01.01
- **Material3**: Latest from Compose BOM
- **Activity Compose**: 1.12.3
- **Coil (Image Loading)**: 2.7.0
  - Coil Compose
  - Coil GIF support

#### Dependency Injection
- **Dagger Hilt**: 2.57.1
- **Hilt Compose Navigation**: 1.3.0
- **KSP (Kotlin Symbol Processing)**: 2.0.0-1.0.21

#### Networking
- **Retrofit**: 2.9.0
- **Jackson Databind**: 2.17.1
- **Jackson Kotlin Module**: 2.17.1

#### Testing
- **JUnit**: 4.13.2
- **MockK**: 1.13.13
- **Turbine**: 1.2.0 (Flow testing)
- **Kotlinx Coroutines Test**: 1.9.0
- **AndroidX Test**:
  - JUnit Extension: 1.3.0
  - Espresso Core: 3.7.0

## Building from Source

### 1. Clone the Repository

```bash
git clone <repository-url>
cd stackOverflowApp
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository and select it
4. Wait for Gradle sync to complete

### 3. Build the Project

#### Via Android Studio:
- Click "Build" → "Make Project" or press `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

#### Via Command Line:
```bash
# On macOS/Linux
./gradlew build

# On Windows
gradlew.bat build
```

### 4. Run the App

#### Via Android Studio:
1. Connect an Android device or start an emulator
2. Click the "Run" button or press `Shift+F10`
3. Select your target device

#### Via Command Line:
```bash
# Install debug build on connected device
./gradlew installDebug

# Or build and install in one step
./gradlew installDebug
```

### 5. Run Tests

#### Run All Tests:
```bash
./gradlew test
```

#### Run Specific Test Class:
```bash
./gradlew test --tests "SearchViewModelTest"
./gradlew test --tests "QuestionDetailViewModelTest"
```

#### View Test Reports:
After running tests, open the HTML report at:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

## Project Architecture

### Architecture Pattern
- **MVVM (Model-View-ViewModel)**: Clean separation of concerns
- **Repository Pattern**: Abstracts data sources
- **Dependency Injection**: Hilt for compile-time DI

### Project Structure

```
app/src/main/java/com/example/stackoverflow/
├── features/
│   ├── search/
│   │   ├── activities/          # Search screen UI (Compose)
│   │   └── viewmodels/          # Search business logic
│   └── details/
│       ├── activities/          # Question detail UI (Compose)
│       └── viewmodels/          # Detail business logic
├── repository/
│   ├── models/                  # Data models
│   ├── stackoverflowApi/        # API interface definitions
│   └── stackoverflowRepository/ # Repository implementations
├── utils/
│   ├── interfaces/              # Utility interfaces (DateUtils, NetworkChecker)
│   └── implementations/         # Utility implementations
├── injection/                   # Hilt DI modules
└── ui/
    └── theme/                   # Compose theme definitions

app/src/test/java/                # Unit tests
```

### Key Components

#### ViewModels
- **SearchViewModel**: Manages question search, pagination, and network state
- **QuestionDetailViewModel**: Handles answer loading and time formatting

#### Utilities
- **DateUtils**: Abstraction for date/time operations (testable)
- **NetworkConnectivityChecker**: Monitors network availability

#### Repositories
- **StackOverflowRepository**: Fetches data from Stack Overflow API

## Testing

The project includes comprehensive unit tests with **Given-When-Then** structure:

### SearchViewModel Tests (9 tests)
- Date formatting
- Query state management
- Question search with pagination
- Network error handling
- No internet dialog behavior

### QuestionDetailViewModel Tests (15 tests)
- Date formatting with time
- Time-ago formatting (minutes, hours, days, months, years)
- Answer loading
- Error handling
- Loading state management

**Total: 25 passing tests ✅**

All tests use MockK for mocking and Turbine for Flow testing.

## Configuration

### API
The app uses the Stack Overflow API v2.2:
- Base URL: `https://api.stackexchange.com/2.2/`
- No API key required for basic usage
- Rate limits apply (see Stack Overflow API documentation)

### Build Variants
- **Debug**: Development build with debugging enabled
- **Release**: Production build with ProGuard/R8 optimization

## Common Issues

### Gradle Sync Failed
- Ensure you have JDK 11 or higher
- Check internet connection
- Try "File" → "Invalidate Caches / Restart"

### App Crashes on Launch
- Verify device runs Android 8.0 (API 26) or higher
- Check logcat for detailed error messages
- Ensure internet connection is available

### Tests Failing
- Clean the project: `./gradlew clean`
- Rebuild: `./gradlew build`
- Check that all dependencies are downloaded

## Contributing

1. Fork the repository
2. Create a feature branch
3. Write tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

[Add your license information here]

## Contact

[Add contact information here]
