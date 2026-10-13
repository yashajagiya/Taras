# Agent Directives & Context

## Critical Rule: Persistent Brain & Context
1. **Always read `brain.md`**: At the start of every session or when handling new tasks, view `brain.md` in the project root to load the user's preferences, project architecture, current roadmap, and technical decisions.
2. **Keep `brain.md` updated continuously**: At the end of every task, discussion, or meaningful exchange, proactively update `brain.md`'s **Session History & Activity Log**, roadmap, and preferences so full context is always preserved across chat restarts.

## Project Conventions (`Taras F1 Static API`)
- **Nature of Project:** 100% Static Formula 1 REST API served directly from GitHub Pages edge CDN (`github.io`).
- **Core Stack:** Python 3.x pipeline (`tarasF1DataV2/`), static JSON datasets (`v2/` primary, `v1/` legacy), GitHub Pages portal (`index.html`).
- **Run / Test Commands:**
  - Build V2 API: `python update_v2.py --skip-scrape`
  - Scrape specific round: `python update_v2.py --round <round_num>`
  - Run integrity verification: `python tarasF1DataV2/test_v2_integrity.py`
  - Extract/sync rounds: `python extract_rounds.py --round <round_num>`
- **Coding & Design Style:** Clean, modular Python scripts, strictly validated JSON schemas, zero data loss, zero application runtime servers.
- **Communication:** Concise, precise, actionable responses with clickable file links.
