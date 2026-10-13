# Implement TARAS Vibrant M3 Expressive UI in Compose

This plan outlines the steps to overhaul the existing `MainActivity` UI and implement the vibrant, colorful Material 3 Expressive designs (Paddock, Grid, Calendar, Drivers) into your current Android project.

## User Review Required

> [!IMPORTANT]
> **Material 3 Expressive API:** The design calls for Material 3 Expressive features (like `MaterialShapes`). These APIs are often experimental or require the very latest Compose Material 3 alpha/beta versions. I plan to bump your Compose Material 3 dependency to the latest version to access these, or closely approximate them with standard M3 shapes if they are unstable. 
> 
> **Fonts:** I will need to download and add the "Outfit" and "Space Mono" (or Roboto Mono) fonts to your `res/font` directory.

## Open Questions

> [!NOTE]
> 1. **Navigation:** Your current `MainActivity` is a single scrollable column. I will introduce `navigation-compose` to handle switching between the 4 main tabs (Paddock, Grid, Calendar, Drivers) and the Driver Profile screen. Are you okay with adding this dependency?
> 2. **Calendar Data:** I see you have `F1ApiDevService.getCurrentCircuits()`. I will create a `CalendarViewModel` to fetch this data for the Calendar screen. Is that correct?

## Proposed Changes

### 1. Theming Foundation (`com.example.taras.ui.theme`)

We will replace the default Purple/Pink theme with the new vibrant light theme.

#### [MODIFY] [Color.kt](file:///C:/Users/yash/AndroidStudioProjects/Taras/app/src/main/java/com/example/taras/ui/theme/Color.kt)
- Define all the new hex codes from the vibrant light theme (Background: `#FFF8F6`, Primary: `#C00100`, etc.).
- Add the specific team accent colors (Red Bull, Ferrari, etc.).

#### [MODIFY] [Theme.kt](file:///C:/Users/yash/AndroidStudioProjects/Taras/app/src/main/java/com/example/taras/ui/theme/Theme.kt)
- Update `LightColorScheme` with the new colors.
- Force the app to use the light theme (or configure it to support dark mode later, but we'll start with the requested vibrant light theme).
- Provide a `CompositionLocal` for Team Colors as requested in your blueprint.

#### [MODIFY] [Type.kt](file:///C:/Users/yash/AndroidStudioProjects/Taras/app/src/main/java/com/example/taras/ui/theme/Type.kt)
- Configure the M3 typography scale to use the new fonts (Outfit & Mono).

#### [NEW] `Shape.kt`
- Define the extra-rounded M3 container shapes (24dp, 28dp, 32dp).

### 2. Dependencies & Resources

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/yash/AndroidStudioProjects/Taras/app/build.gradle.kts)
- Add `androidx.navigation:navigation-compose`.
- Update `androidx.compose.material3` if needed for Expressive features.

#### [NEW] `res/font/`
- Add `outfit.ttf` and `space_mono.ttf` font files.

### 3. UI Layer Architecture (`com.example.taras.ui`)

We will create a structured Compose UI architecture.

#### [NEW] `navigation/NavGraph.kt`
- Setup the NavHost and routes for the 4 bottom bar tabs + Driver Profile.

#### [NEW] `components/`
- `MainScaffold.kt`: The TopAppBar and BottomNavigationBar.
- `TeamColorProvider.kt`: The CompositionLocal setup for team colors.

#### [NEW] `screens/`
- `PaddockScreen.kt`: The home dashboard with the Bento Box grid (Next Session, Leader, News).
- `GridScreen.kt`: The standings list with tabs for Drivers/Constructors and team color progress bars.
- `CalendarScreen.kt`: The timeline view of circuits.
- `DriversRosterScreen.kt`: The grid of all drivers with search and filter chips.
- `DriverProfileScreen.kt`: The deep-dive screen with the parallax header and stat bento box.

### 4. ViewModel Layer (`com.example.taras.viewmodel`)

#### [NEW] `CalendarViewModel.kt`
- Fetch and expose circuit data from `F1ApiDevService`.

### 5. Integration

#### [MODIFY] [MainActivity.kt](file:///C:/Users/yash/AndroidStudioProjects/Taras/app/src/main/java/com/example/taras/view/MainActivity.kt)
- Remove the monolithic `LazyColumn`.
- Set up the ViewModels and pass them into the new `NavGraph` wrapped in `TarasTheme`.

## Verification Plan

### Automated Tests
- N/A for UI overhaul, will rely on compose previews and manual testing.

### Manual Verification
- Build and run the app.
- Verify the bottom navigation works and switches tabs.
- Verify the Paddock screen displays news and mock hero data.
- Verify the Grid screen displays driver standings with correct team color bars.
- Verify the Calendar screen shows the timeline.
- Verify the Drivers screen shows the grid, and tapping a driver navigates to the Driver Profile screen.
