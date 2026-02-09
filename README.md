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