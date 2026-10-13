# Agent Directives & Context

## 🛡️ Critical Rule: Strict Project Isolation & Boundaries
This workspace is **`TarasAPIstatic`** (`yashajagiya/Taras`). It is strictly and exclusively dedicated to the **100% Static Formula 1 REST API**.
Historical or companion projects (such as the Kotlin/Ktor WebSocket server or the Jetpack Compose mobile app) are **EXTERNAL** and must **NEVER** be mixed into this project's active tasks, commands, or documentation.

### 📌 Project Boundary Rules:
1. **ONLY Update Section 1 of `brain.md`:** When completing tasks, logging sessions, or updating roadmaps, modify **ONLY** Section 1 (`Taras F1 Static API`). **NEVER** add Kotlin, Ktor, Netty, or WebSocket server commands to Section 1.
2. **Section 2 is READ-ONLY / ARCHIVE:** Section 2 of `brain.md` contains historical reference material from companion projects. It must **NEVER** be modified or mixed with active static API work.
3. **No Foreign Stack Code:** Do not write, generate, or execute Kotlin (`kotlin.bat`), Ktor, Gradle, or Amper files in this repository. All code here is Python 3.11+, JSON schemas, and static web portal assets (`index.html`).

---

## 🧠 Persistent Brain Directives (`brain.md`)
1. **Always read `brain.md` at session start:** Review Section 1 of `brain.md` to load user preferences, static API architecture, and recent session logs.
2. **Keep `brain.md` Section 1 updated continuously:** At the conclusion of every task or meaningful exchange, proactively record your work in Section 1's **Session History & Activity Log**.

---

## ⚙️ Active Project Conventions (`Taras F1 Static API`)
- **Repository:** `M:\TarasAPIstatic` (`yashajagiya/Taras`)
- **Nature of Project:** 100% Static Formula 1 REST API hosted on GitHub Pages edge CDN (`github.io`).
- **Architecture:** 100% self-contained `v1/` (legacy) and `v2/` (modern) modules, GitHub Pages portal (`index.html`).
- **Data Stores:** Internal raw data organized under `v2/data/` (`registries/`, `images/`, `standings/`, `sessions/`, `notifications/`).
- **Run & Test Commands:**
  - Run V2 Async Anti-Ban Runner: `python run_v2.py` (or `python v2/run_v2.py`)
  - Run V1 Async Anti-Ban Runner: `python run_v1.py` (or `python v1/run_v1.py`)
  - Continuous Loop Scraping (Anti-Ban Protected): `python run_v2.py --loop --interval 300`
  - Stealth Mode Scraping: `python run_v2.py --stealth`
  - Direct V2 Endpoint Build: `python v2/build.py` (or `python run_v2.py --skip-scrape`)
  - Scrape Specific Round: `python run_v2.py --round <round_num>`
  - Run Data Integrity Verification: `python v2/test_v2_integrity.py`
  - Extract/Sync Specific Round: `python v2/extract_rounds.py --round <round_num>`
- **Coding Style:** Clean, modular Python scripts, strictly validated JSON schemas, zero data loss, zero application runtime servers.
- **Communication:** Concise, precise, actionable responses with clickable file links formatted as `[filename](file:///M:/TarasAPIstatic/path)` with forward slashes.
