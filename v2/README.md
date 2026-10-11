# Taras F1 Data API v2

Comprehensive Formula 1 (2026) REST API endpoints served via GitHub Pages Edge CDN.

## Endpoints

### 1. Master & Overview
- `overview.json`: Season summary, current leader, active race weekend, next event.
- `live.json`: Real-time session state, track flags, live leader, WebSocket URLs.

### 2. Standings & Calendar
- `standings.json`: Driver and Constructor championship standings with points and ranks.
- `calendar.json`: Full 2026 race calendar with dates, circuits, formats, and track layout SVGs/PNGs.

### 3. Drivers & Constructors
- `drivers.json`: All 23 drivers with official portrait images, statistics, numbers, and bios.
- `teams.json`: All 11 constructors with official white logo URLs, car cutouts, and chassis specs.

### 4. Race Weekend Results
- `results/latest.json`: The latest or active race weekend sessions.
- `results/round_{N}.json`: Complete weekend results for round `N` (1 to 24) including FP1, FP2, FP3, Qualifying, Sprint Qualifying, Sprint Race, and Grand Prix classifications.

### 5. Media & Fallbacks
- `driverimg.json`: Driver portrait image URLs.
- `teamsimgdata.json`: Team logo and car livery image URLs.
- `racesimg.json`: Circuit layout map URLs.
- `carperrace.json`: Constructor race-by-race point progression.
- `driversperrace.json`: Driver race-by-race point progression.
- `drivers_data.json`: Driver biography and reference encyclopedic data.
