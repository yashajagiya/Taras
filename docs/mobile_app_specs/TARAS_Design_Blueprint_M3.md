# 🏎️ TARAS: Official UI/UX Design Blueprint & M3 Architecture (v2)

**Target Audience:** Gen Z, Gen Alpha, Hardcore F1 Fans.
**Design Philosophy:** "High-Speed Bento Box." Elegant, premium minimalism. Data-dense but visually digestible.
**Tech Stack:** Jetpack Compose, `androidx.compose.material3:material3:1.4.0`, Material 3 Expressive (`ExperimentalMaterial3ExpressiveApi`).

> **What changed in this revision:** the v1 palette named color *roles* but hand-picked hex values for only 9 of them. This version generates the full 2025-spec M3 color system — all ~40 roles, including `Fixed` colors and the expanded surface-container scale — from the racing-red brand seed using Google's actual HCT color algorithm, the same math behind `ColorScheme.fromSeed()`. It also fixes a real bug in the original team-color approach (below), adds the M3 Expressive shape/typography/motion systems, and refreshes the team roster to the 11-team 2026 grid.

---

## 1. 🎨 Material 3 Theming & Color Tokens

### 1.1 How this palette was built

Seed color: **`#FF1801` "Racing Red."** Every role below was generated from that seed using the Material Color Utilities algorithm (2025 spec, `Variant.EXPRESSIVE`) — not hand-picked. Two deliberate overrides were layered on top, both using the same override mechanism `ColorScheme.fromSeed(seedColor:, primary:, ...)` exposes natively:

1. **`primary` is pinned to the literal `#FF1801`**, not the algorithm's default dark-theme tone (which lightens a saturated seed to `#FFB0A2` for large-surface safety). TARAS only ever uses `primary` for *small* high-emphasis accents — nav indicator, live dot, buttons — where the full-saturation brand red reads correctly and doesn't visually overwhelm.
2. **The neutral palette's chroma was tightened** (chroma 2–4 instead of the algorithm's default ~8) so backgrounds read as true carbon/graphite rather than inheriting a noticeable red-brown cast from the seed hue. This is still 100% HCT-derived — just a controlled neutral source color, the same lever Material Theme Builder exposes as a separate "Neutral" swatch.

Everything else — containers, `Fixed` roles, secondary, tertiary, error, all eleven surface/outline roles — is the unmodified algorithmic output, which is what actually earns this the "M3" name: internally consistent tonal relationships and contrast guarantees, not just on-brand colors.

> ⚠️ **Accessibility note:** `onPrimary` (white) against pinned `primary` (`#FF1801`) measures **~3.9:1**. That clears the WCAG large-text/UI-component bar (3:1) comfortably — fine for button labels, tab labels, icons — but falls short of the 4.5:1 small-text bar. Never place small body copy directly on a `primary` fill; use `primaryContainer`/`onPrimaryContainer` instead, which is exactly what that role pair exists for.

### 1.2 Core color roles — Dark (default) & Light

Dark mode ships first and remains the default, per the original brief. Light is included because a real M3 app is expected to support both — the algorithm gives you the light scheme for free once the dark one exists.

**Primary (brand)**

| Role | Dark | Light | Usage |
|---|---|---|---|
| `primary` | `#FF1801` | `#FF1801` | Pinned brand red — active nav tab, live dot, primary buttons |
| `onPrimary` | `#FFFFFF` | `#FFFFFF` | Text/icon on `primary` — buttons & labels only (see note above) |
| `primaryContainer` | `#F79F90` | `#FFAC9E` | Larger red-family fills — selected chip, highlighted standings row |
| `onPrimaryContainer` | `#5C2219` | `#6F2116` | Text/icon on `primaryContainer` |
| `primaryFixed` | `#FFAC9E` | `#FFAC9E` | Identical in both themes — persistent elements that must survive a theme switch (e.g. a "LIVE" badge) |
| `onPrimaryFixed` | `#4B160E` | `#520C05` | Text/icon on `primaryFixed` |

**Secondary — "Pitlane" cool accent**

| Role | Dark | Light |
|---|---|---|
| `secondary` | `#B4CAD6` | `#3F6475` |
| `onSecondary` | `#2F434D` | `#F3FAFF` |
| `secondaryContainer` | `#132831` | `#C4EBFF` |
| `onSecondaryContainer` | `#92A7B2` | `#335869` |

Used for filter chips, tab underlines outside the primary nav, and secondary actions — the restrained counterweight to all that red.

**Tertiary — "Live Telemetry" cyan**

| Role | Dark | Light |
|---|---|---|
| `tertiary` | `#8AE0FF` | `#006880` |
| `onTertiary` | `#005063` | `#F1FAFF` |
| `tertiaryContainer` | `#5CD5FB` | `#5CD5FB` |
| `onTertiaryContainer` | `#004658` | `#004658` |

This is a genuine, brand-level M3 tertiary — reserved for session-live indicators and timing deltas. It is **not** where team colors live anymore (see §1.4 for why).

**Error — incidents, DNFs, validation**

| Role | Dark | Light |
|---|---|---|
| `error` | `#FD6F85` | `#AC3149` |
| `onError` | `#490013` | `#FFF7F7` |
| `errorContainer` | `#8A1632` | `#F76A80` |
| `onErrorContainer` | `#FF97A3` | `#68001F` |

Genuinely useful beyond form validation here: red-flag/safety-car banners and DNF markers on the Driver Deep-Dive stat grid are exactly what `errorContainer` is for.

**Surface & neutral**

| Role | Dark | Light | Usage |
|---|---|---|---|
| `background` / `surface` | `#100D0D` | `#FFF8F6` | App canvas |
| `onBackground` / `onSurface` | `#EEE3E1` | `#373130` | Primary readable text |
| `surfaceDim` | `#100D0D` | `#E3D7D5` | |
| `surfaceBright` | `#312B2A` | `#FFF8F6` | |
| `surfaceContainerLowest` | `#000000` | `#FFFFFF` | Recessed wells — search fields, code/telemetry readouts |
| `surfaceContainerLow` | `#161312` | `#FBF2F0` | |
| `surfaceContainer` | `#1D1918` | `#F6ECEA` | Standard Bento Box cards (replaces v1's flat `#16161A`) |
| `surfaceContainerHigh` | `#231E1E` | `#F0E6E4` | Elevated cards, modals, active states |
| `surfaceContainerHighest` | `#2A2524` | `#EBE0DE` | Max elevation |
| `onSurfaceVariant` | `#B3A9A7` | `#655D5C` | Secondary text, timestamps, RSS publish dates |

**Outline & effects**

| Role | Dark | Light | Usage |
|---|---|---|---|
| `outline` | `#7C7472` | `#817977` | Strong borders, input outlines |
| `outlineVariant` | `#4D4645` | `#BAB0AE` | Bento grid hairlines (replaces v1's flat `#2C2C35`) |
| `inverseSurface` / `inverseOnSurface` | `#FFF8F6` / `#595453` | `#100D0D` / `#A29B9A` | Snackbars, tooltips |
| `inversePrimary` | `#914B3F` | `#FF8F7C` | Primary-toned content sitting on an inverse surface |
| `scrim` / `shadow` | `#000000` | `#000000` | |
| `surfaceTint` | `#FFB0A2` | `#9D4335` | Tonal-elevation overlay used by `Surface(tonalElevation = ...)` — left algorithmic on purpose, it's a subtle overlay tint, not a visible brand swatch |

*(The 2025 spec also defines `primaryDim`/`secondaryDim`/`tertiaryDim`/`errorDim` and a `secondaryFixed`/`tertiaryFixed` family, mainly consumed internally by tone-adaptive M3 components rather than referenced directly in app code — generated in the Kotlin snippet below but omitted from these tables to keep them scannable.)*

### 1.3 Kotlin implementation

```kotlin
// Color.kt — values generated from seed #FF1801, spec="2025", variant=EXPRESSIVE
val TarasDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF1801),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF79F90),
    onPrimaryContainer = Color(0xFF5C2219),
    primaryFixed = Color(0xFFFFAC9E),
    onPrimaryFixed = Color(0xFF4B160E),
    secondary = Color(0xFFB4CAD6),
    onSecondary = Color(0xFF2F434D),
    secondaryContainer = Color(0xFF132831),
    onSecondaryContainer = Color(0xFF92A7B2),
    tertiary = Color(0xFF8AE0FF),
    onTertiary = Color(0xFF005063),
    tertiaryContainer = Color(0xFF5CD5FB),
    onTertiaryContainer = Color(0xFF004658),
    error = Color(0xFFFD6F85),
    onError = Color(0xFF490013),
    errorContainer = Color(0xFF8A1632),
    onErrorContainer = Color(0xFFFF97A3),
    background = Color(0xFF100D0D),
    onBackground = Color(0xFFEEE3E1),
    surface = Color(0xFF100D0D),
    onSurface = Color(0xFFEEE3E1),
    surfaceDim = Color(0xFF100D0D),
    surfaceBright = Color(0xFF312B2A),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF161312),
    surfaceContainer = Color(0xFF1D1918),
    surfaceContainerHigh = Color(0xFF231E1E),
    surfaceContainerHighest = Color(0xFF2A2524),
    onSurfaceVariant = Color(0xFFB3A9A7),
    outline = Color(0xFF7C7472),
    outlineVariant = Color(0xFF4D4645),
    inverseSurface = Color(0xFFFFF8F6),
    inverseOnSurface = Color(0xFF595453),
    inversePrimary = Color(0xFF914B3F),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFFB0A2),
)
```

### 1.4 Team Color Tokens — corrected architecture

**The problem with v1:** it said *"tertiary is injected dynamically based on the driver's team."* That doesn't just clash with M3's semantics (`tertiary` is a fixed, app-wide role that other components may read from) — it's a functional bug. The Grid/Standings screen renders **rows for all 11 teams simultaneously**. A single app-wide `tertiary` override cannot represent 11 different colors on screen at once. This isn't a style nitpick, it just doesn't work for the screen it was designed for.

**The fix:** team colors aren't a M3 theme role at all. They're app-level content data — conceptually identical to what M3's own `primaryFixed`/`secondaryFixed` pattern is for (*"stays the same color between light and dark themes"*), just scoped per-team instead of per-theme. Model them as a lookup table plus a `CompositionLocal`, so any composable can read "the current team's color" without touching the global `ColorScheme`:

```kotlin
// TeamColor.kt
data class TeamColorToken(val accent: Color, val onAccent: Color)

val TeamPalette = mapOf(
    "red_bull"     to TeamColorToken(Color(0xFF4781D7), Color.Black),
    "ferrari"      to TeamColorToken(Color(0xFFED1131), Color.Black),
    "mclaren"      to TeamColorToken(Color(0xFFFF8000), Color.Black),
    "mercedes"     to TeamColorToken(Color(0xFF00D7B6), Color.Black),
    "aston_martin" to TeamColorToken(Color(0xFF229971), Color.Black),
    "alpine"       to TeamColorToken(Color(0xFF00A1E8), Color.Black),
    "williams"     to TeamColorToken(Color(0xFF1868DB), Color.White),
    "racing_bulls" to TeamColorToken(Color(0xFF6C98FF), Color.Black),
    "haas"         to TeamColorToken(Color(0xFF9C9FA2), Color.Black),
    "audi"         to TeamColorToken(Color(0xFFACB4C0), Color.Black),
    "cadillac"     to TeamColorToken(Color(0xFFF5F5F5), Color.Black),
)

val LocalTeamColor = compositionLocalOf { TeamPalette.getValue("red_bull") }
```

```kotlin
// Usage — scopes the accent to one card/row instead of the whole theme
CompositionLocalProvider(LocalTeamColor provides TeamPalette.getValue(driver.teamId)) {
    DriverBioCard(driver)
}
```

This also fixes the on-color guesswork: **`onAccent` is precomputed per team, not assumed to be white.** Contrast was checked against both white and black for every 2026-grid team color — most (9 of 11) actually need **black** text/icons for legible contrast; only Williams' navy is dark enough to need white:

| Team | Accent | onAccent | Contrast |
|---|---|---|---|
| Red Bull Racing | `#4781D7` | Black | 5.4 : 1 |
| Ferrari | `#ED1131` | Black | 4.7 : 1 |
| McLaren | `#FF8000` | Black | 8.3 : 1 |
| Mercedes | `#00D7B6` | Black | 11.4 : 1 |
| Aston Martin | `#229971` | Black | 5.9 : 1 |
| Alpine | `#00A1E8` | Black | 7.3 : 1 *(+ BWT pink secondary in some liveries)* |
| Williams | `#1868DB` | White | 5.2 : 1 |
| Racing Bulls | `#6C98FF` | Black | 7.6 : 1 |
| Haas | `#9C9FA2` | Black | 7.9 : 1 |
| Audi | `#ACB4C0`* | Black | 10.0 : 1 |
| Cadillac | `#F5F5F5`** | Black | 19.3 : 1 |

*\* Audi's launch livery is titanium/silver with red accents — no single official brand hex is public, so this is a best-effort estimate. Confirm against team assets before shipping.*
*\*\* Cadillac runs a black/white split livery. Its black side (`~#161616`) is nearly identical to this theme's own `surfaceContainer` — a team-tinted glow using it would be invisible. Use the white/platinum side as the usable in-app accent instead.*

Roster and colors reflect the 2026-season launches (Jan–Feb 2026, the first year of the 11-team grid with Audi replacing Sauber and Cadillac joining as the new 11th team). Liveries and sponsors can shift mid-season — spot-check before a production release.

---

## 2. ✍️ Typography Scale (M3 Expressive)

M3 Expressive doubled the type scale: **15 baseline styles + 15 "Emphasized" counterparts** (same Display/Headline/Title/Body/Label × Large/Medium/Small grid, heavier weight, meant for selection and focal moments). Both Manrope and Outfit ship as variable fonts, so the emphasized weight jump costs nothing extra to bundle.

*   **Font Family:** Manrope or Outfit (variable, geometric sans) for UI text. Roboto Mono for timing/telemetry data.
*   **`displayLargeEmphasized`:** 57sp, heaviest available weight. The "Next Race" countdown — this is the one place a decorative, maximal type moment is earned.
*   **`headlineMediumEmphasized`:** 28sp. Screen titles, but only while that tab is the active selection — falls back to baseline `headlineMedium` otherwise, so the emphasis actually signals something.
*   **`titleMedium`:** 16sp SemiBold. Card titles, driver names — baseline is enough here, this doesn't need to compete for attention.
*   **`labelSmall`:** 11sp Medium, Monospaced (Roboto Mono, not part of the M3 scale swap). Timing gaps, RSS dates, points.

```kotlin
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
Text("FP1 in 02:14:59", style = MaterialTheme.typography.displayLargeEmphasized)
```

---

## 3. 🔷 Shape System (new)

Two separate shape mechanisms, easy to conflate:

1. **The M3 shape scale** — five roundedness steps for standard containers, unchanged in count from base M3 but now used more deliberately:

    ```kotlin
    // Shape.kt
    val TarasShapes = Shapes(
        extraSmall = RoundedCornerShape(8.dp),
        small = RoundedCornerShape(12.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp),      // Bento cards, driver bio cards
        extraLarge = RoundedCornerShape(32.dp), // Hero "Next Session" widget
    )
    ```

2. **`MaterialShapes`** — the Expressive-era library of 35 polygon shapes (cookies, clovers, bursts, pills) plus shape morphing, for decorative/accent elements only — avatars, FABs, loading states, never core layout containers:

    ```kotlin
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    Box(
        Modifier
            .size(56.dp)
            .clip(MaterialShapes.Cookie9Sided.toShape())
            .background(MaterialTheme.colorScheme.tertiary)
    )
    ```

    Best fit in this app: the **"Live Pit Wall" shortcut** — a `Cookie9Sided` or `SoftBurst` shape that morphs into a `Circle` the instant a session goes live, doubling as a state change signal, not just decoration. Also fits the new-expressive `LoadingIndicator`/`ContainedLoadingIndicator` composables (replacing `CircularProgressIndicator` for the RSS feed's loading state).

---

## 4. 📱 Compose Architecture & Navigation Graph

The app utilizes a single-activity architecture wrapped in a Material 3 `Scaffold`.

**Global `Scaffold` Structure:**
*   **`topBar`:** `CenterAlignedTopAppBar` (Transparent background, blurred glassmorphism on scroll). Displays the "TARAS" logo or current active session status.
*   **`bottomBar`:** M3 `NavigationBar`.
    *   Colors: Container is `colorScheme.surface`, Indicator is `colorScheme.primary` at 20% opacity (`primary.copy(alpha = 0.2f)` — compute from the token, don't hardcode `#33FF1801`), Icon is `colorScheme.primary` (active) or `colorScheme.onSurfaceVariant` (inactive).
    *   Items: 🏠 Paddock (Home), 🏆 Grid (Standings), 📅 Calendar, 👤 Drivers.
*   **`content`:** The active `NavHost` destination.

---

## 5. 🖼️ Screen-By-Screen UI Breakdown

### Screen 1: "The Paddock" (Home / Dashboard)
**Compose Blueprint:** `LazyVerticalStaggeredGrid` with 2 columns, background `colorScheme.surface`.
*   **Hero Item (span 2): "Next Session" Widget.** `displayLargeEmphasized` countdown (e.g. "FP1 in 02:14:59"), `extraLarge` shape, gradient overlay on the host country's flag.
*   **Item 2 (span 1): "Championship Leader" Mini-Card.** `surfaceContainerHigh` background, P1 driver cutout overflowing the top edge, points in `titleMedium`.
*   **Item 3 (span 1): "Live Pit Wall" Shortcut.** Glassmorphic button in `tertiaryContainer`; morphing `MaterialShapes` icon per §3.
*   **Item 4+ (span 2): "Latest from the Paddock" (RSS Feed).** `LazyRow` of News RSS Cards; `ContainedLoadingIndicator` while fetching.

### Screen 2: "The Grid" (Standings)
**Compose Blueprint:** `Column` with `PrimaryTabRow` + `HorizontalPager`.
*   **`PrimaryTabRow`:** "Drivers" / "Constructors" tabs, active indicator in `colorScheme.primary`.
*   **Driver List Item:** rank (Monospaced, `onSurfaceVariant`), headshot (`CircleCrop`), name/team (`onSurface`). Beneath the name, a `Canvas` progress bar scoped inside `CompositionLocalProvider(LocalTeamColor provides ...)` per §1.4 — this is the screen the old tertiary-hijack approach couldn't actually serve.

### Screen 3: "Circuit Explorer" (Calendar)
**Compose Blueprint:** `LazyColumn`.
*   **Past Races:** `Modifier.alpha(0.5f)`.
*   **Current/Next Race:** `ElevatedCard` (`surfaceContainerHigh`), `outline`-colored glow border, official circuit map.
*   **Future Races:** Standard `Card` (`surfaceContainer`).
*   **Layout:** Vertical timeline `Canvas` in `outlineVariant`, dots per round.

### Screen 4: "Driver Deep-Dive" (Profiles)
**Compose Blueprint:** `CollapsingToolbarScaffold` / scroll-driven parallax header.
*   **Stat Bento Box:** 2×2 grid — Wins, Podiums, Poles, DNFs. DNF count uses `errorContainer`/`onErrorContainer` — a real semantic fit, not just a nice-to-have.
*   **Typography:** Driver number as a `displayLargeEmphasized` watermark at 5% opacity behind content.

---

## 6. 🧩 Core Component Design Specs

### A. "News RSS Card"
*   `ElevatedCard`, `width(280.dp)` × `height(200.dp)`, `clip(RoundedCornerShape(24.dp))` (→ `TarasShapes.large`).
*   `containerColor = MaterialTheme.colorScheme.surfaceContainer`.
*   Top 60%: thumbnail (`AsyncImage`, `ContentScale.Crop`). Bottom 40%: title (`titleMedium`, 2 lines, ellipsis). Small `primary`-colored live dot next to the timestamp.

### B. "Driver Bio Card" (Drivers Grid)
*   `OutlinedCard`, `fillMaxWidth()`, `clip(RoundedCornerShape(16.dp))` (→ `TarasShapes.medium`).
*   `containerColor = Color.Transparent`, `border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)`.
*   **Team accent, corrected:** reads from `LocalTeamColor.current.accent` (§1.4) rather than the theme's `tertiary`. `Modifier.background(Brush.horizontalGradient(listOf(Color.Transparent, LocalTeamColor.current.accent.copy(alpha = 0.14f))))` — bumped from v1's `0.1f` since several 2026 team colors (Haas, Audi) are lower-chroma and need a touch more alpha to read as a visible tint rather than disappearing into `surfaceContainer`.

---

## 7. ✨ Micro-Interactions & Motion

M3 Expressive replaced duration/easing motion with a **spring-physics token system**, `MaterialTheme.motionScheme`, exposed as six specs — three "effects" (fade/scale/color, non-spatial) and three "spatial" (position/size/layout), each in fast/default/slow:

```kotlin
val motion = MaterialTheme.motionScheme
val pressScale by animateFloatAsState(
    targetValue = if (pressed) 0.98f else 1f,
    animationSpec = motion.fastSpatialSpec(), // replaces v1's hand-tuned animateFloatAsState
)
```

1.  **Shared Element Transitions:** tapping a Driver Card in standings expands the headshot into the Deep-Dive hero image (`SharedTransitionScope`, unchanged from v1 — still the right tool).
2.  **`AnimatedVisibility` on Scroll:** `fadeIn() + slideInVertically(initialOffsetY = { 50 })`, but drive the spec from `motion.defaultSpatialSpec()` instead of a hardcoded duration so it stays consistent with every other spring-based motion in the app.
3.  **Bento Box Press Effect:** `Modifier.clickable(indication = ripple(color = Color.White.copy(alpha = 0.1f)))` + `motion.fastSpatialSpec()`-driven `scale(0.98f)` on press, per the snippet above.
4.  **Shape morph on live state (new):** the Pit Wall shortcut's `MaterialShapes` icon (§3) morphs shape when a session goes live — motion communicating a state change, not just decorating a press.

---

## What's next?

The heavy lifting — real tonal math, a working team-color system, a verified 2026 roster — is done. From here, pick a next step:

1. **Generate the actual `Theme.kt`, `Color.kt`, `Shape.kt` files** — full paste-ready versions of everything in §1.3 and §3, including the light scheme and `TeamPalette`.
2. **Generate the Home Screen Bento Box layout** in Compose, wired to these tokens.
3. **Generate the Standings screen** — the one that actually needed the `LocalTeamColor` fix.
