# Agent Directives & Context

## Critical Rule: Persistent Brain & Context
1. **Always read `brain.md`**: At the start of every session or when handling new tasks, view `brain.md` in the project root to load the user's preferences, project architecture, current roadmap, and technical decisions.
2. **Keep `brain.md` updated continuously**: At the end of every task, discussion, or meaningful exchange, proactively update `brain.md`'s **Session History & Activity Log**, roadmap, and preferences so full context is always preserved across chat restarts.

## Project Conventions (`Taras F1 Static API`)
- **Nature of Project:** 100% Static Formula 1 REST API served directly from GitHub Pages edge CDN (`github.io`).
- **Core Stack:** 100% self-contained `v1/` and `v2/` modules, GitHub Pages portal (`index.html`).
- **Run / Test Commands:**
  - Run V2 Async Anti-Ban Runner: `python run_v2.py` (or `python v2/run_v2.py`)
  - Run V1 Async Anti-Ban Runner: `python run_v1.py` (or `python v1/run_v1.py`)
  - Run Continuous Scraping (No IP Ban): `python run_v2.py --loop --interval 300`
  - Stealth Mode (Maximum Safety): `python run_v2.py --stealth` / `python run_v1.py --stealth`
  - Build V2 API directly: `python v2/build.py` (or `python update_v2.py --skip-scrape`)
  - Scrape specific round: `python run_v2.py --round <round_num>`
  - Run integrity verification: `python v2/test_v2_integrity.py`
  - Extract/sync rounds: `python v2/extract_rounds.py --round <round_num>`
- **Coding & Design Style:** Clean, modular Python scripts, strictly validated JSON schemas, zero data loss, zero application runtime servers.
- **Communication:** Concise, precise, actionable responses with clickable file links.
