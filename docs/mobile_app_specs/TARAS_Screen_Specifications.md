# 🏎️ TARAS — Google Stitch Prompts for Every Screen

> **How to use:** Copy each prompt below into Google Stitch (Figma). Each prompt is self-contained with all colors, dimensions, typography, spacing, and content so Stitch can generate the screen without external context. All screens are **Android mobile (412×892dp)**, **dark mode first**, **Material Design 3**.

---

## 📋 Global Frame Settings (apply to ALL screens)

Before generating any screen, set these in Stitch:
- **Frame size:** 412 × 892 (Android phone)
- **Design system:** Material Design 3
- **Theme:** Dark mode
- **Background:** #131313
- **Font families:** Outfit (UI text), Roboto Mono (data/numbers)
- **Corner radius standard:** 8dp (extraSmall), 12dp (small), 16dp (medium), 24dp (large), 32dp (extraLarge)

---

## Screen 1: "The Paddock" — Home Dashboard

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Home Dashboard called "The Paddock".

TOP BAR:
- Material 3 CenterAlignedTopAppBar
- Background: transparent (same as screen background #131313)
- Center: "TARAS" wordmark in Outfit font, 20sp, semibold, color #E5E2E1, letter-spacing 4sp (tracked out)
- No navigation icons on left, a search icon on right in color #C9C5C4
- Height: 64dp

BOTTOM NAVIGATION BAR:
- Material 3 NavigationBar fixed at bottom
- Background: #201F1F (surfaceContainer)
- 4 items equally spaced:
  1. "Paddock" with Home icon — ACTIVE state: icon and label color #FFB4A9, active indicator pill behind icon filled with #474747 (secondaryContainer), pill is 64x32dp with 16dp corner radius
  2. "Grid" with Trophy icon — inactive: icon #C9C5C4, label #C9C5C4
  3. "Calendar" with CalendarMonth icon — inactive: icon #C9C5C4, label #C9C5C4
  4. "Drivers" with Person icon — inactive: icon #C9C5C4, label #C9C5C4
- Label font: Outfit, 12sp, medium weight
- Bar height: 80dp, thin 1px line at top in #494645

MAIN CONTENT (scrollable, 16dp horizontal padding, 12dp gap between items, arranged as a 2-column staggered grid):

Item 1 — "Next Session" Hero Card (spans full width, both columns):
- Height: 200dp, corner radius: 32dp (extraLarge)
- Background: #2B2A29 (surfaceContainerHigh) with a very subtle semi-transparent gradient overlay (dark to transparent, left to right) over a faded flag image (e.g., British flag at 15% opacity)
- Top-left: small pill badge "ROUND 12" in #474747 background, text #E3E2E1, 11sp Outfit medium, 8dp corner radius, 8dp padding horizontal, 4dp vertical
- Center content vertically:
  - "BRITISH GRAND PRIX" in Outfit, 14sp, semibold, #C9C5C4 (onSurfaceVariant), letter-spacing 2sp
  - "FP1 in" in Outfit, 14sp, regular, #938F8E (outline color)
  - "02:14:59" in Outfit, 57sp, extrabold (displayLargeEmphasized), color #E5E2E1
  - Below: "Silverstone Circuit" in Outfit, 12sp, #C9C5C4
- Bottom-right corner: small "LIVE" dot — 8dp red circle #FFB4A9 with a subtle glow/pulse ring around it (1dp ring at 30% opacity)

Item 2 — "Championship Leader" Mini Card (left column, span 1):
- Height: 160dp, corner radius: 24dp (large)
- Background: #201F1F (surfaceContainer)
- Top: label "P1 LEADER" in 11sp Outfit medium, color #C9C5C4, letter-spacing 1.5sp
- Center: Driver name "M. VERSTAPPEN" in 16sp Outfit semibold, #E5E2E1
- Below name: "Red Bull Racing" in 12sp Outfit, #C9C5C4
- Below team: Points "303 PTS" in 20sp Outfit bold, color #FFB4A9 (primary — the one pop of red)
- Bottom: thin progress bar, 4dp height, full width minus 16dp padding each side, track color #494645, fill color #0600EF (Red Bull team color) at 70% width
- No driver image, keep it typographic and minimal

Item 3 — "Live Pit Wall" Shortcut (right column, span 1):
- Height: 120dp, corner radius: 24dp
- Background: #1F4E4D (tertiaryContainer)
- Center: a 48dp icon placeholder (abstract 9-sided cookie/gear shape) in #8BD0CE (tertiary)
- Below icon: "PIT WALL" in 11sp Outfit semibold, #A7ECEA (onTertiaryContainer), letter-spacing 2sp
- Below: "Session offline" in 11sp Outfit regular, #A7ECEA at 60% opacity

Item 4 — "Constructor Battle" Mini Card (right column, below Pit Wall):
- Height: 120dp, corner radius: 24dp
- Background: #201F1F (surfaceContainer)
- "CONSTRUCTORS" label 11sp #C9C5C4
- Two rows showing top 2 constructors:
  Row 1: "1. Red Bull" with a 4dp left border in #0600EF, "580 pts" right-aligned in #E5E2E1
  Row 2: "2. Ferrari" with a 4dp left border in #E10600, "522 pts" right-aligned in #E5E2E1
- Font: 13sp Outfit medium for names, 13sp Roboto Mono for points

Item 5 — "Latest from the Paddock" News Section (spans full width):
- Section header: "LATEST NEWS" in 11sp Outfit semibold, #C9C5C4, letter-spacing 2sp, left-aligned, 8dp bottom margin
- Horizontal scrolling row of 3 News Cards visible (LazyRow):

  Each News Card:
  - Width: 280dp, Height: 200dp, corner radius: 24dp
  - Background: #201F1F (surfaceContainer)
  - Top 60%: placeholder thumbnail area (dark gray gradient placeholder #2B2A29 to #363534, or a racing photo if possible)
  - Bottom 40%: 12dp padding all sides
    - Title: "Hamilton sets record pace in practice" in 16sp Outfit semibold, #E5E2E1, max 2 lines, ellipsis overflow
    - Below title: Row with a 6dp circle dot in #FFB4A9 (primary), then "2h ago" in 11sp Roboto Mono, #C9C5C4
  - Cards have 12dp gap between them
  - Subtle 1dp elevation shadow
```

---

## Screen 2: "The Grid" — Standings (Drivers Tab)

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Standings screen called "The Grid" showing the Drivers championship tab.

TOP BAR:
- Material 3 CenterAlignedTopAppBar
- Background: #131313
- Center: "THE GRID" in Outfit, 20sp, semibold, #E5E2E1, letter-spacing 3sp
- Right: filter icon in #C9C5C4

BOTTOM NAVIGATION BAR:
- Same as Screen 1 but "Grid" tab is ACTIVE (trophy icon and "Grid" label in #FFB4A9 with #474747 indicator pill), all others inactive in #C9C5C4
- Background: #201F1F, height 80dp

TAB ROW (directly below top bar):
- Material 3 PrimaryTabRow, full width
- Two tabs: "DRIVERS" (active) and "CONSTRUCTORS" (inactive)
- Active tab "DRIVERS": text in #FFB4A9 (primary), with a 3dp bottom indicator line in #FFB4A9
- Inactive tab "CONSTRUCTORS": text in #C9C5C4
- Tab background: transparent
- Font: 14sp Outfit semibold, letter-spacing 1.5sp
- Tab row height: 48dp

SEASON SELECTOR (below tabs):
- Small horizontal row: "2024 SEASON" in 12sp Outfit medium, #C9C5C4 left aligned, with a small dropdown chevron icon
- 8dp vertical padding, 16dp horizontal padding

DRIVER STANDINGS LIST (scrollable, below season selector, 16dp horizontal padding):

Each Driver Row (repeat for top 10 drivers, 8dp vertical gap between rows):
- Height: 72dp, full width, corner radius: 16dp (medium)
- Background: #201F1F (surfaceContainer)
- 12dp padding all sides
- Layout: horizontal row

  Left section:
  - Position number "1" in 20sp Roboto Mono bold, color #C9C5C4, fixed width 36dp, center-aligned

  Center-left:
  - Circular driver headshot placeholder: 44dp diameter, filled with team color at 20% opacity, centered circle with initials "MV" in 14sp Outfit bold, #E5E2E1
  - 12dp gap to right of headshot

  Center:
  - Driver name "Max Verstappen" in 16sp Outfit semibold, #E5E2E1
  - Below: "Red Bull Racing" in 12sp Outfit regular, #C9C5C4
  - Below team: Canvas-style horizontal progress bar, height 4dp, corner radius 2dp
    - Track: #494645 (outlineVariant), full width of remaining space
    - Fill: team color (varies per driver — see colors below), width proportional to points

  Right:
  - Points "303" in 18sp Roboto Mono bold, #E5E2E1, right-aligned
  - Below: "PTS" in 10sp Roboto Mono, #C9C5C4

Team colors for the progress bars (use these exact hex values):
- Row 1 (Verstappen): #0600EF (Red Bull blue)
- Row 2 (Norris): #FF8700 (McLaren papaya)
- Row 3 (Leclerc): #E10600 (Ferrari red)
- Row 4 (Sainz): #E10600 (Ferrari red)
- Row 5 (Piastri): #FF8700 (McLaren papaya)
- Row 6 (Hamilton): #00D2BE (Mercedes teal)
- Row 7 (Russell): #00D2BE (Mercedes teal)
- Row 8 (Alonso): #006F62 (Aston Martin green)
- Row 9 (Gasly): #0090FF (Alpine blue)
- Row 10 (Ocon): #0090FF (Alpine blue)

Show at least 6 rows visible on screen, the list scrolls. Each row has a subtle horizontal gradient from left to right: transparent to the team color at 8% opacity — just enough to tint the row edge.
```

---

## Screen 3: "The Grid" — Standings (Constructors Tab)

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Standings screen called "The Grid" showing the Constructors championship tab.

TOP BAR:
- Same as Drivers tab: "THE GRID" center, Outfit 20sp semibold #E5E2E1

BOTTOM NAVIGATION BAR:
- Same as Drivers tab — "Grid" active

TAB ROW:
- Same layout but now "CONSTRUCTORS" is active (text #FFB4A9, 3dp bottom line #FFB4A9)
- "DRIVERS" is inactive (text #C9C5C4)

CONSTRUCTOR STANDINGS LIST (scrollable, 16dp horizontal padding, 12dp gap between items):

Each Constructor Row:
- Height: 88dp, full width, corner radius: 16dp (medium)
- Background: #201F1F (surfaceContainer)
- Left edge: 4dp wide vertical strip (full height of row, rounded on left corners) filled with the team's official color — this is the "racing stripe"
- 16dp padding on all sides (after the stripe)

  Layout:
  - Left: Position "1" in 24sp Roboto Mono bold, #C9C5C4, center-aligned, 40dp width
  - Center:
    - Team name "Red Bull Racing" in 16sp Outfit semibold, #E5E2E1
    - Below: two driver names "Verstappen • Pérez" in 12sp Outfit, #C9C5C4
    - Below: horizontal progress bar, 6dp height, corner radius 3dp, track #494645, fill in team color, width proportional to points vs leader
  - Right:
    - Points "580" in 22sp Roboto Mono bold, #FFB4A9 (primary)
    - Below: "PTS" in 10sp Roboto Mono, #C9C5C4

Show 10 constructor rows with these exact team colors for the racing stripe and progress bar:
1. Red Bull Racing — #0600EF — 580 pts — "Verstappen • Pérez"
2. Ferrari — #E10600 — 522 pts — "Leclerc • Sainz"
3. McLaren — #FF8700 — 468 pts — "Norris • Piastri"
4. Mercedes — #00D2BE — 366 pts — "Hamilton • Russell"
5. Aston Martin — #006F62 — 244 pts — "Alonso • Stroll"
6. Alpine — #0090FF — 118 pts — "Gasly • Ocon"
7. Williams — #005AFF — 86 pts — "Albon • Sargeant"
8. RB — #6692FF — 64 pts — "Ricciardo • Tsunoda"
9. Haas — #FFFFFF (use a 1dp border of #494645 around the stripe since it's white on dark) — 38 pts — "Hülkenberg • Magnussen"
10. Sauber — #52E252 — 12 pts — "Bottas • Zhou"

Each row has a very subtle horizontal gradient from the left stripe color at 10% opacity fading to transparent across the full width of the card.
```

---

## Screen 4: "Circuit Explorer" — Calendar

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Race Calendar screen called "Circuit Explorer".

TOP BAR:
- Material 3 CenterAlignedTopAppBar
- Background: #131313
- Center: "CALENDAR" in Outfit, 20sp, semibold, #E5E2E1, letter-spacing 3sp
- Right: a small year filter chip "2024" in #474747 background, text #E3E2E1, 8dp corner radius

BOTTOM NAVIGATION BAR:
- Same global nav bar. "Calendar" tab is ACTIVE (calendar icon and label in #FFB4A9 with #474747 indicator pill). Others inactive #C9C5C4.
- Background: #201F1F

MAIN CONTENT (scrollable vertical list, 16dp horizontal padding):

LEFT SIDE — Vertical Timeline:
- A continuous vertical line, 2dp wide, color #494645 (outlineVariant), running down the left side at 24dp from the left edge
- At each race entry, a dot on the timeline:
  - Past races: hollow circle, 10dp diameter, 2dp stroke #494645, no fill
  - Current/next race: filled circle, 12dp diameter, filled #FFB4A9 (primary), with a subtle glow ring (2dp ring, #FFB4A9 at 25% opacity)
  - Future races: hollow circle, 10dp, 2dp stroke #494645

RIGHT SIDE — Race Cards (to the right of the timeline, 44dp left margin from screen edge):

Past Race Card (show 2, stacked):
- Height: 64dp, full remaining width, corner radius: 12dp (small)
- Background: #1C1B1B (surfaceContainerLow)
- Opacity: 50% on the entire card (dimmed)
- Layout row:
  - Left: "R10" in 12sp Roboto Mono bold, #C9C5C4
  - Center: "Spanish GP" in 14sp Outfit semibold, #E5E2E1. Below: "Jun 23" in 11sp Outfit, #C9C5C4
  - Right: Winner "VER" with small "P1" badge in 10sp, green checkmark icon or trophy mini icon
- 8dp gap between past race cards

Current/Next Race Card (HIGHLIGHTED — show 1):
- Height: 140dp, full remaining width, corner radius: 16dp (medium)
- Background: #2B2A29 (surfaceContainerHigh)
- Left border accent: 3dp wide strip in #FFB4A9 (primary), full height, rounded left corners
- Top: "R12 • NEXT RACE" pill label in #930100 (primaryContainer) background, text #FFDAD4 (onPrimaryContainer), 8dp corner radius
- Center:
  - "British Grand Prix" in 18sp Outfit bold, #E5E2E1
  - "Silverstone Circuit" in 13sp Outfit, #C9C5C4
  - "Jul 5–7, 2024" in 12sp Roboto Mono, #938F8E
- Bottom: Row of session tags as small chips: "FP1" "FP2" "FP3" "QUAL" "RACE" in 10sp Outfit medium, each chip #474747 background, text #E3E2E1, 6dp corner radius, 6dp horizontal padding. The upcoming session "FP1" chip has #930100 background and #FFDAD4 text (highlighted primary)
- Optional: faint circuit outline illustration as a watermark at 5% opacity in the right side of the card

Future Race Cards (show 3, stacked):
- Height: 72dp, full remaining width, corner radius: 12dp (small)
- Background: #201F1F (surfaceContainer)
- Layout row:
  - Left: "R13" in 12sp Roboto Mono bold, #C9C5C4
  - Center: "Hungarian GP" in 14sp Outfit semibold, #E5E2E1. Below: "Jul 19–21" in 11sp Outfit, #C9C5C4
  - Right: Country flag emoji or small 20x14dp flag placeholder
- 8dp gap between future cards

Future race entries:
- R13: "Hungarian GP" — "Jul 19–21"
- R14: "Belgian GP" — "Jul 26–28"
- R15: "Dutch GP" — "Aug 23–25"
```

---

## Screen 5: "Driver Deep-Dive" — Driver Profile

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Driver Profile screen called "Driver Deep-Dive" for Max Verstappen.

TOP BAR:
- Material 3 TopAppBar (not center-aligned — use standard with back arrow)
- Left: back arrow icon in #E5E2E1
- Title: "DRIVER PROFILE" in 16sp Outfit semibold, #E5E2E1
- Background: transparent, blending into the header below
- No bottom border

HERO HEADER (collapsible parallax area):
- Height: 280dp, full width, no corner radius (edge to edge)
- Background: gradient from #0600EF (Red Bull team color) at 15% opacity on the left, fading to #131313 on the right
- Large watermark: "1" (driver number) in 180sp Outfit extrabold, color #E5E2E1 at 5% opacity, positioned right-aligned, vertically centered, partially clipped by the right edge — a subtle ghost number
- Center-left content (vertically centered, 24dp left padding):
  - "MAX" in 14sp Outfit medium, #C9C5C4, letter-spacing 3sp
  - "VERSTAPPEN" in 28sp Outfit bold (headlineMediumEmphasized), #E5E2E1
  - Below: "Red Bull Racing" in 14sp Outfit regular, #C9C5C4
  - Below: small row — Dutch flag emoji 🇳🇱 + "Netherlands" in 12sp Outfit, #938F8E
  - Below (8dp gap): "P1 — 303 PTS" in 16sp Outfit semibold, #FFB4A9 (primary)
- Right side: driver headshot placeholder — circular, 100dp diameter, border 3dp in #0600EF (team color), filled with #2B2A29. Initials "MV" in 28sp Outfit bold, #E5E2E1 centered inside

DIVIDER:
- 1dp line in #494645, 16dp horizontal margin, 16dp vertical margin

STAT BENTO BOX (2×2 grid, 16dp horizontal padding, 12dp gap between cards):

Each stat card: equal width (half minus gaps), height 100dp, corner radius 16dp (medium)

Card 1 (top-left) — WINS:
- Background: #201F1F (surfaceContainer)
- Icon: trophy icon, 24dp, color #C9C5C4, top-left with 12dp padding
- Value: "60" in 36sp Outfit bold, #E5E2E1, centered
- Label: "WINS" in 11sp Outfit medium, #C9C5C4, letter-spacing 1.5sp, below value

Card 2 (top-right) — PODIUMS:
- Background: #201F1F (surfaceContainer)
- Icon: podium/medal icon, 24dp, #C9C5C4
- Value: "108" in 36sp Outfit bold, #E5E2E1
- Label: "PODIUMS" in 11sp Outfit medium, #C9C5C4

Card 3 (bottom-left) — POLES:
- Background: #201F1F (surfaceContainer)
- Icon: flag icon, 24dp, #C9C5C4
- Value: "39" in 36sp Outfit bold, #E5E2E1
- Label: "POLES" in 11sp Outfit medium, #C9C5C4

Card 4 (bottom-right) — DNFs (uses error semantic colors):
- Background: #93000A (errorContainer)
- Icon: warning/crash icon, 24dp, #FFDAD6 (onErrorContainer)
- Value: "5" in 36sp Outfit bold, #FFDAD6 (onErrorContainer)
- Label: "DNFs" in 11sp Outfit medium, #FFDAD6 (onErrorContainer)

RECENT RESULTS SECTION (below bento box, 16dp top margin):
- Section header: "RECENT RESULTS" in 11sp Outfit semibold, #C9C5C4, letter-spacing 2sp, 16dp horizontal padding
- 8dp below header

Result rows (3 visible, scrollable):
Each row:
- Height: 56dp, full width minus 16dp padding each side, corner radius 12dp
- Background: #201F1F
- Layout row:
  - Left: Race name "British GP" in 14sp Outfit medium, #E5E2E1
  - Center: "Silverstone" in 12sp Outfit, #C9C5C4
  - Right: finishing position in a pill badge:
    - P1: background #0600EF (team color), text "P1" in 12sp Outfit bold, white
    - P2: background #474747, text "P2" in 12sp Outfit bold, #E5E2E1
    - DNF: background #93000A, text "DNF" in 12sp Outfit bold, #FFDAD6
- 6dp gap between rows

Sample data:
- "British GP" — "Silverstone" — P1 (team color pill)
- "Spanish GP" — "Barcelona" — P1 (team color pill)
- "Canadian GP" — "Montréal" — DNF (error pill)

NO bottom navigation bar on this screen — this is a detail/profile screen navigated into from the Drivers list, with a back arrow to return.
```

---

## Screen 6: "Drivers" — Drivers Grid Browser

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Drivers browser screen where users can browse all F1 drivers.

TOP BAR:
- Material 3 CenterAlignedTopAppBar
- Background: #131313
- Center: "DRIVERS" in Outfit, 20sp, semibold, #E5E2E1, letter-spacing 3sp
- Right: search icon in #C9C5C4

BOTTOM NAVIGATION BAR:
- Same global nav bar. "Drivers" tab is ACTIVE (person icon and label in #FFB4A9 with #474747 indicator pill). Others inactive #C9C5C4.
- Background: #201F1F

TEAM FILTER ROW (horizontal scrollable chips, below top bar, 16dp left padding):
- Row of filter chips, 8dp gap between:
  - "All" chip: SELECTED — background #474747 (secondaryContainer), text #E3E2E1, checkmark icon before text, 8dp corner radius, height 32dp
  - "Red Bull" chip: unselected — background transparent, 1dp border #494645, text #C9C5C4. A 10dp circle filled with #0600EF before the text
  - "Ferrari" chip: unselected — 1dp border #494645, text #C9C5C4, 10dp circle #E10600
  - "McLaren" chip: unselected — 1dp border #494645, text #C9C5C4, 10dp circle #FF8700
  - "Mercedes" chip: unselected — 1dp border #494645, text #C9C5C4, 10dp circle #00D2BE
  - (more chips scrollable off-screen)
- Font: 13sp Outfit medium
- Row height: 48dp with 8dp vertical padding

DRIVERS GRID (2-column grid, scrollable, 16dp horizontal padding, 12dp gap):

Each Driver Bio Card:
- Width: half screen minus padding/gap, Height: 180dp, corner radius: 16dp (medium)
- Background: #1C1B1B (surfaceContainerLow)
- Border: 1dp solid #494645 (outlineVariant)
- Subtle horizontal gradient from left to right: transparent → team color at 12% opacity (tints the right edge)

  Card Layout (12dp padding all sides):
  - Top-left: circular headshot placeholder, 52dp diameter, #2B2A29 fill, centered initials in 16sp Outfit bold #E5E2E1
  - Top-right: driver number "1" in 28sp Outfit extrabold, team color (#0600EF for Red Bull), right-aligned
  - Below headshot (8dp gap):
    - Driver name "Max Verstappen" in 14sp Outfit semibold, #E5E2E1
    - Team: "Red Bull Racing" in 11sp Outfit, #C9C5C4
  - Bottom: thin horizontal bar, 3dp height, corner radius 1.5dp, full card width minus padding
    - Track: #494645
    - Fill: team color, width proportional to championship points

Show 6 driver cards visible (3 rows of 2), the grid scrolls:

Row 1:
- Card 1: Max Verstappen — #1 — Red Bull — team color #0600EF — gradient tint #0600EF at 12%
- Card 2: Sergio Pérez — #11 — Red Bull — team color #0600EF

Row 2:
- Card 3: Charles Leclerc — #16 — Ferrari — team color #E10600 — gradient tint #E10600 at 12%
- Card 4: Carlos Sainz — #55 — Ferrari — team color #E10600

Row 3:
- Card 5: Lando Norris — #4 — McLaren — team color #FF8700 — gradient tint #FF8700 at 12%
- Card 6: Oscar Piastri — #81 — McLaren — team color #FF8700
```

---

## Screen 7: "The Paddock" — Live Session Active State

### Prompt to copy:

```
Design a dark mode Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #131313. This is the Home Dashboard "The Paddock" but during a LIVE SESSION — the "Next Session" hero card has transformed into a live state.

TOP BAR:
- Material 3 CenterAlignedTopAppBar
- Background: #131313
- Center: Row — pulsing 8dp circle in #FFB4A9 (primary) + "LIVE — FP1" in Outfit, 16sp, semibold, #FFB4A9
- Right: notification bell icon with a small 8dp red dot indicator

BOTTOM NAVIGATION BAR:
- Same as Screen 1 — "Paddock" active

MAIN CONTENT:

Hero Card — LIVE SESSION (full width, spans both columns):
- Height: 220dp, corner radius: 32dp
- Background: #2B2A29 with a very subtle animated-looking border — 2dp border in #FFB4A9 at 40% opacity (to indicate live state)
- Top-left: "LIVE" pill badge — background #930100 (primaryContainer), text "● LIVE" in 11sp Outfit bold, #FFDAD4, the dot is a solid 6dp circle. Pill has 8dp corner radius
- Top-right: "FP1 — SESSION 1/5" in 11sp Outfit, #C9C5C4
- Center:
  - "BRITISH GRAND PRIX" in 14sp Outfit semibold, #C9C5C4, letter-spacing 2sp
  - "SESSION TIME" in 12sp Outfit, #938F8E
  - "00:42:17" in 57sp Outfit extrabold, #E5E2E1 (counting UP — elapsed session time)
  - Below: "Remaining: 00:47:43" in 14sp Roboto Mono, #C9C5C4
- Bottom row: live timing snippet — 3 drivers with fastest times:
  - "1. VER 1:27.384" in 12sp Roboto Mono, #E5E2E1
  - "2. NOR +0.234" in 12sp Roboto Mono, #C9C5C4
  - "3. LEC +0.567" in 12sp Roboto Mono, #C9C5C4

"Pit Wall" Card (right column) — LIVE STATE:
- Height: 120dp, corner radius: 24dp
- Background: #1F4E4D (tertiaryContainer)
- The decorative shape icon has morphed from a 9-sided cookie shape to a perfect CIRCLE — 48dp, filled #8BD0CE (tertiary)
- Below: "PIT WALL" in 11sp Outfit semibold, #A7ECEA
- Below: "TAP TO VIEW" in 11sp Outfit, #A7ECEA at 80% opacity — now interactive since session is live

Championship Leader card and News section remain the same as Screen 1.
```

---

## Screen 8: Light Mode — "The Paddock" Home

### Prompt to copy:

```
Design a LIGHT MODE Material Design 3 Android mobile screen (412x892) for an F1 racing app called "TARAS". Background color #FBF9F8. This is the Home Dashboard "The Paddock" in light theme.

TOP BAR:
- Background: #FBF9F8 (surface)
- Center: "TARAS" wordmark in Outfit, 20sp, semibold, color #1C1B1B (onSurface)
- Right: search icon in #4D4948 (onSurfaceVariant)

BOTTOM NAVIGATION BAR:
- Background: #F0EDEC (surfaceContainer)
- "Paddock" ACTIVE: icon and label #B82000 (primary), indicator pill #E2E2E1 (secondaryContainer)
- Inactive tabs: icon and label #4D4948 (onSurfaceVariant)
- Thin 1px top border #CEC9C8 (outlineVariant)

MAIN CONTENT:

Hero "Next Session" Card (full width):
- Height: 200dp, corner radius: 32dp
- Background: #EAE7E6 (surfaceContainerHigh)
- "ROUND 12" pill: #E2E2E1 background, text #1B1C1B
- "BRITISH GRAND PRIX" in 14sp Outfit semibold, #4D4948
- "02:14:59" in 57sp Outfit extrabold, #1C1B1B (onSurface)
- "Silverstone Circuit" in 12sp, #4D4948

Championship Leader Card:
- Background: #F0EDEC (surfaceContainer)
- All text in #1C1B1B, secondary text #4D4948
- Points "303 PTS" in #B82000 (primary — light mode version)
- Progress bar fill: #0600EF (Red Bull)

Pit Wall Card:
- Background: #BBEDEB (tertiaryContainer)
- Icon: #3B6564 (tertiary)
- Text: #054B4A (onTertiaryContainer)

News Cards:
- Background: #F0EDEC (surfaceContainer)
- Title: #1C1B1B, timestamp: #4D4948
- Live dot: #B82000 (primary)

This should look clean and bright like Google's own apps in light mode — think Gmail or Google Maps light theme. Very minimal, lots of white space, the colored elements are team colors and the primary red accent only.
```

---

## 🎨 Quick Color Reference Card

Copy this as a reference when generating any screen:

```
=== TARAS M3 Dark Theme Quick Reference ===

Surface family (backgrounds):
  #0E0E0E  surfaceContainerLowest (recessed wells)
  #131313  surface/background (canvas)
  #1C1B1B  surfaceContainerLow
  #201F1F  surfaceContainer (standard cards)
  #2B2A29  surfaceContainerHigh (elevated cards)
  #363534  surfaceContainerHighest (max elevation)

Text:
  #E5E2E1  onSurface (primary text)
  #C9C5C4  onSurfaceVariant (secondary text)
  #938F8E  outline (tertiary/hint text)

Primary (brand red):
  #FFB4A9  primary (icons, indicators, accents)
  #680100  onPrimary
  #930100  primaryContainer
  #FFDAD4  onPrimaryContainer

Secondary (neutral):
  #C7C6C5  secondary
  #474747  secondaryContainer (chips, nav indicator)
  #E3E2E1  onSecondaryContainer

Tertiary (telemetry teal):
  #8BD0CE  tertiary
  #1F4E4D  tertiaryContainer
  #A7ECEA  onTertiaryContainer

Error:
  #FFB4AB  error
  #93000A  errorContainer
  #FFDAD6  onErrorContainer

Dividers:
  #494645  outlineVariant (subtle)
  #938F8E  outline (strong)

Official F1 Team Colors:
  Mercedes     #00D2BE  (onAccent: Black)
  Ferrari      #E10600  (onAccent: White)
  Red Bull     #0600EF  (onAccent: White)
  McLaren      #FF8700  (onAccent: Black)
  Aston Martin #006F62  (onAccent: White)
  Alpine       #0090FF  (onAccent: Black)
  Williams     #005AFF  (onAccent: White)
  Haas         #FFFFFF  (onAccent: Black)
  Sauber       #52E252  (onAccent: Black)
  RB           #6692FF  (onAccent: Black)
  AlphaTauri   #4E7C9B  (onAccent: White)

=== TARAS M3 Light Theme Quick Reference ===

Surface family:
  #FFFFFF  surfaceContainerLowest
  #FBF9F8  surface/background
  #F5F3F2  surfaceContainerLow
  #F0EDEC  surfaceContainer
  #EAE7E6  surfaceContainerHigh
  #E4E2E0  surfaceContainerHighest

Text:
  #1C1B1B  onSurface
  #4D4948  onSurfaceVariant
  #7D7A78  outline

Primary: #B82000 / onPrimary: #FFFFFF
primaryContainer: #FFDAD4 / onPrimaryContainer: #3E0000
```
