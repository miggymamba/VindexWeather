# Vindex Weather

**Vindex Weather** is an Android application built to demonstrate a production-ready approach to handling unstable network conditions, complex UI states, and background synchronization in a modern mobile environment.

Rather than treating a weather app as a simple API fetcher, this project approaches it as a study in **Offline-First** architecture. It prioritizes data consistency, single source of truth (SSOT) patterns, and seamless recovery from process death or network fragmentation. The codebase prioritizes maintainability, strict separation of concerns, and verifiable correctness through testing.

## Key Features

- **Offline-First Architecture:** The UI consumes data exclusively from the local database (Room). Network calls update the database, observing the "Single Source of Truth" principle.

- **Resilient Synchronization:** Background updates are managed via `WorkManager` with strict constraints (UNMETERED + CHARGING) to respect user resources.

- **Reactive MVI:** UI state is managed via `StateFlow` and immutable data models, ensuring predictable rendering across configuration changes.

- **Adaptive Layouts:** Supports dynamic resizing for Foldables and Tablets using standard `WindowSizeClass` implementation.


## Technical Architecture

Following the [Now in Android](https://github.com/android/nowinandroid) philosophy, this project utilizes **Clean Architecture** with a layered approach to separate concerns and maximize testability.

### Layer Breakdown

- **`:app`**: The entry point, containing `MainActivity` and global Hilt configuration.

- **`:presentation`**: Jetpack Compose UI, ViewModels, and UI state management.

- **`:domain`**: The "Brain." Contains pure Kotlin Business Logic, UseCases, and Repository interfaces. **No Android dependencies.**

- **`:data`**: Implementation of repositories, Retrofit services, and Room persistence. Handles the synchronization logic between Remote and Local data sources.


### Network & Sync Stack

- **Retrofit 3.0 / OkHttp 5:** Handling API communication with custom Interceptors for logging and error mapping.

- **Kotlin Serialization:** Type-safe, reflection-free JSON parsing.

- **WorkManager:** Guaranteed background execution for periodic weather synchronization.

- **Coil:** Coroutine-backed image loading pipeline for weather assets.


## What This Project Demonstrates

This project is intended as a practical demonstration of the following skills and design decisions:

- Implementing an Offline-First repository pattern where the UI never talks to the network directly

- Handling complex asynchronous streams using Kotlin Coroutines and Flow

- Managing large datasets efficiently using **Paging 3** (RemoteMediator pattern)

- Conscious handling of API rate limits using local caching and `debounce` strategies

- Writing unit tests that validate business logic (UseCases) and integration tests for the Repository layer

- Applying "Staff-level" build configuration using Version Catalogs and strict compiler flags


## Development Setup

1. Clone the repository.

2. Ensure you have the latest **Android Studio Ladybug (or later)**.

3. Obtain a free API Key from [OpenWeatherMap](https://openweathermap.org/api).

4. Create a `local.properties` file in the root directory and add:

    ```
    WEATHER_API_KEY=your_actual_key_here
    ```

5. Build the project.


## Testing Strategy

This project emphasizes meaningful unit testing over artificial coverage metrics.

- **Domain Layer:** 100% coverage of UseCases using MockK to verify business rules.

- **Data Layer:** `MockWebServer` is used to simulate network edge cases (404, 500, timeouts) to ensure the app handles failures gracefully without crashing.

- **UI Layer:** "Golden Path" automated tests using `ComposeTestRule` to verify critical user journeys.


## Non-Goals

This project intentionally does not aim to:

- Compete with commercial weather apps in terms of visual polish or animations

- Implement proprietary or paid weather data layers (strictly uses Free Tier endpoints)

- Support legacy Android views (strictly Jetpack Compose)


## Sequence Diagram

````mermaid
sequenceDiagram
    autonumber
    participant UI as Compose UI
    participant VM as ViewModel (MVI)
    participant UC as Use Case (Domain)
    participant REPO as Repository (Data)
    participant DB as Room Database
    participant API as Remote API

    Note over UI, API: [WEATHER REFRESH FLOW]

    UI->>VM: UserIntent.Refresh
    VM->>VM: State = Loading
    VM->>UC: GetWeather(lat, lon)
    
    UC->>REPO: syncWeather()
    activate REPO
    
    REPO->>API: GET /weather
    activate API
    API-->>REPO: 200 OK (JSON)
    deactivate API
    
    REPO->>REPO: Map DTO to Entity
    REPO->>DB: Insert/Update Weather
    activate DB
    DB-->>REPO: Success
    deactivate DB
    
    deactivate REPO

    Note right of REPO: Single Source of Truth updates

    REPO->>UC: Emit New Data (Flow)
    UC->>VM: WeatherData
    VM->>VM: State = Success
    VM-->>UI: Recompose Screen
````

## Screenshots

### Dark Mode

|Screen Name|Images|
|---|---|
|**Dashboard**|[Placeholder]|
|**Search**|[Placeholder]|
|**Forecast**|[Placeholder]|

### Light Mode

|Screen Name|Images|
|---|---|
|**Dashboard**|[Placeholder]|
|**Search**|[Placeholder]|
|**Forecast**|[Placeholder]|

## License

Vindex Weather is compliant with **Apache License 2.0**. See [LICENSE](https://github.com/miggymamba/VindexWeather/blob/main/LICENSE) for more information.