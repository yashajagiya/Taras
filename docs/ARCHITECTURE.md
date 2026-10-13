# 🏗️ Taras F1 API — Architecture & Extension Guide

This document outlines the **Clean Static API Architecture** of the Taras F1 API dataset, explaining how data is ingested, normalized, verified, and edge-served via GitHub Pages.

---

## 🏛️ Core Architectural Principles

1. **Edge-Cached Zero Compute:**
   No runtime application servers, virtual machines, or active databases. All API endpoints exist as pre-rendered, minified JSON files served directly via GitHub's worldwide Fastly/Cloudflare CDN.
2. **Deterministic & Idempotent Builds:**
   Running `python update_v2.py --skip-scrape` builds the entire V2 dataset deterministically from registries and local caches without side-effects or network dependencies.
3. **Non-Destructive Storage:**
   Historical session results (`results/round_{1..18}.json`) are preserved permanently. New rounds are appended without overwriting or destroying past race data.
4. **Decoupled Data Pipeline:**
   The generation pipeline (`tarasF1DataV2/`) is completely decoupled from consumer endpoints (`v2/`). The pipeline produces output in `tarasF1DataV2/output/` and synchronizes to `v2/` only after passing 100% data integrity tests.
5. **Backwards Compatibility:**
   Legacy `v1/` endpoints remain intact for older mobile clients, while new features and unified payloads are developed in `v2/`.

---

## 📁 Repository Directory Structure

```text
TarasAPIstatic/
├── .github/
│   └── workflows/
│       └── update-f1.yml        # Weekly automated GitHub Actions scraper & deployer
├── index.html                   # Interactive Developer Portal & REST API Explorer for GitHub Pages
├── README.md                    # Primary repository overview and developer guide
├── update_v2.py                 # Root CLI entry point for running the master update pipeline
├── extract_rounds.py            # Root CLI entry point for extracting weekend session rounds
├── v1/                          # Legacy v1 endpoints (isolated backward compatibility)
│   ├── README.md
│   ├── *.json                   # Legacy individual standings and image tables
│   └── practice1..race-result/  # Legacy session result directories
├── v2/                          # Modern Unified v2 Static REST API (Primary)
│   ├── README.md
│   ├── overview.json            # Home screen in 1 request
│   ├── drivers.json             # All 23 drivers with portraits & stats
│   ├── teams.json               # All 11 constructors with car cutouts & logos
│   ├── standings.json           # Championship tables & round-by-round points
│   ├── calendar.json            # 24-race schedule & circuit vectors
│   ├── live.json                # Live session status metadata
│   └── results/                 # Session classifications (latest & round_{1..18}.json)
│       ├── latest.json
│       └── round_{N}.json
├── tarasF1DataV2/               # Master Build Engine & Data Pipeline
│   ├── build.py                 # Core aggregator: combines all sources into endpoints
│   ├── update.py                # Pipeline orchestrator (scrape -> build -> verify -> git)
│   ├── test_v2_integrity.py     # 8-suite automated integrity verification
│   ├── extract_rounds.py        # Historical round extraction and normalization
│   ├── collectors/              # External data adapters (ESPN, Encyclopedia, Sessions)
│   ├── scrapers/                # Official Formula 1 session HTML parsers
│   ├── data/                    # Authoritative registries (teams, drivers, calendar, assets)
│   └── output/                  # Staging directory for newly generated v2 endpoints
└── docs/                        # Complete technical documentation
    ├── API_REFERENCE.md         # Full REST API endpoint reference & schemas
    ├── ARCHITECTURE.md          # Architecture & extension guide (this document)
    ├── F1_DATA_SOURCES.md       # External data source catalog
    ├── PROJECT.md               # Product background & Taras ecosystem role
    └── mobile_app_specs/        # Specifications for the TARAS Jetpack Compose mobile app
```

---

## 🔄 Data Pipeline Flow

```mermaid
graph TD
    A[Official F1 Web / ESPN / Ergast] -->|Scrapers| B[tarasF1DataV2/scrapers/]
    B -->|Normalized Sessions| C[tarasF1DataV2/data/sessions/]
    D[Authoritative Registries<br/>data/teams_registry.json<br/>data/calendar_master.json] --> E[tarasF1DataV2/build.py]
    C --> E
    E -->|Generates JSON| F[tarasF1DataV2/output/]
    F -->|Validation| G[test_v2_integrity.py]
    G -->|All 8 Tests Pass| H[Synchronize to public v2/]
    H -->|Git Push| I[GitHub Pages Edge CDN<br/>yashajagiya.github.io/Taras/v2/]
```

---

## 🛠️ How to Extend & Maintain

### 1. Adding a New Driver or Mid-Season Driver Swap
1. Open `tarasF1DataV2/data/teams_registry.json`.
2. Locate the team (e.g. `racing-bulls`).
3. Add or update the driver entry with `id`, `name`, `code`, `number`, and `portrait_url`.
4. Run `python update_v2.py --skip-scrape`.
5. The build engine automatically updates `v2/drivers.json`, `v2/teams.json`, and `v2/overview.json`.

### 2. Updating Constructor Renders or Colors
1. Open `tarasF1DataV2/build.py`.
2. Update the `OFFICIAL_TEAM_ASSETS` dictionary with new high-resolution URLs or ARGB hex color codes.
3. Run `python update_v2.py --skip-scrape`.

### 3. Scraping & Archiving a New Race Weekend
```bash
# Scrape specific round (e.g. Round 18 United States GP):
python update_v2.py --round 18

# Or extract all past rounds:
python tarasF1DataV2/extract_rounds.py --round 18 --build
```
This automatically scrapes FP1, FP2, FP3, Qualifying, Sprint, and Race classifications, writes `v2/results/round_18.json`, updates `v2/results/latest.json`, updates championship points in `v2/standings.json`, and runs the integrity verification suite.

### 4. Running Data Integrity Tests
```bash
python tarasF1DataV2/test_v2_integrity.py
```
Tests verified:
- [x] `overview.json` schema and leadership presence
- [x] All 23 drivers present with 16 season + 8 career metrics
- [x] All 11 constructors present with car renders and specs
- [x] Driver and Constructor standings with round points
- [x] Calendar coverage with valid dates and track maps
- [x] `results/latest.json` full weekend classification
- [x] Historical rounds 1 to 16 data retention
- [x] Specific Sprint weekend formatting (e.g. Round 17)

---

## 🌐 Deploying to GitHub Pages (`github.io`)

1. Commit and push the repository to GitHub:
   ```bash
   git add .
   git commit -m "Update F1 Static API dataset"
   git push origin main
   ```
2. In GitHub Repository Settings -> **Pages**:
   - **Source:** Deploy from a branch
   - **Branch:** `main` / `/ (root)`
3. Within ~60 seconds, all endpoints are live at `https://<username>.github.io/<repo>/v2/<endpoint>.json`.
