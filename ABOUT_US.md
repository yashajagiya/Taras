# About Taras (तरस्) 🏎️

<p align="center">
  <strong>A high-performance, tracker-free Formula 1 companion for Android</strong><br>
  Built with Jetpack Compose, Material Design 3, and modern reactive Android architecture.
</p>

---

## 🌟 The Philosophy & Meaning of Taras

**"Taras" (तरस्)** in Sanskrit translates to:
> **"Speed," "velocity," "dynamic energy," "impetus,"** or **"strength."**

It perfectly encapsulates the very essence of Formula 1 motorsport—machines and athletes pushed to the cutting edge of physics, where split-second decisions and raw velocity define victory.

---

## 👨‍💻 Developer & Mission

Taras was conceptualized and developed by **Yash Ajagiya**, an independent Android engineer and lifelong Formula 1 enthusiast from India.

### Why Taras Was Created:
Most existing sports applications on the Play Store are weighed down by:
- Intrusive banner ads and unskippable video pop-ups.
- Mandatory account registrations, phone numbers, and cloud tracking.
- Heavy background analytics that drain battery and collect personal browsing habits.
- Cluttered, sluggish user interfaces.

Taras was built to deliver the exact opposite: **a lightning-fast, minimalist, ad-free, 100% private companion app** that respects the user's device and time.

---

## 🏛️ Core Design Pillars

1. 🔒 **Absolute Privacy & Zero Tracking:**
   - No user accounts, logins, or cloud profile databases.
   - Zero commercial ad SDKs, marketing trackers, or telemetry.
   - All preferences and card progress remain 100% on your device (Android Jetpack DataStore and Room SQLite).

2. 🇮🇳 **Indian & Global Legal Adherence:**
   - Full statutory compliance with India's **Digital Personal Data Protection Act, 2023 (DPDPA 2023)**.
   - Adherence to the **Information Technology Act, 2000** and **IT Rules, 2021** (designated Grievance Officer, intermediary RSS rules).
   - Nominative fair use of Formula 1 trademarks under Section 30 of the **Trade Marks Act, 1999**.

3. ⚡ **Engineering Excellence & Open Source:**
   - 100% open-source under the **Apache License 2.0**.
   - Built using state-of-the-art Android practices: Jetpack Compose, Material Design 3 dynamic theming, Navigation 3, Compose Charts, Ksoup, and WorkManager.

---

## 📱 What Taras Offers

- **🗞️ The Paddock:** Real-time news aggregator parsing public motorsport RSS feeds on-device using Ksoup.
- **👥 The Grid:** Live 2026 driver and constructor standings, career milestones, and head-to-head teammate comparison.
- **🗓️ Calendar & Circuits:** Detailed Grand Prix weekend schedules with automated countdowns to FP1, FP2, FP3, Qualifying, Sprint, and Race sessions.
- **🏆 Results Explorer:** Comprehensive race classifications, gaps, lap counts, and instant graphic card sharing.
- **🃏 F1 GridTCG 2026:** An offline digital card collecting mini-game featuring scratch-to-reveal packs, card binders, and head-to-head battle arena. 100% free with zero real-money gaming.
- **📲 Home Screen Glance Widgets:** Quick glance at the upcoming Grand Prix countdown, favorite driver standing, and championship leaders.

---

## 🛠️ Technology Stack

| Component | Framework | Purpose |
| :--- | :--- | :--- |
| **UI System** | Jetpack Compose & Material 3 | Declarative, fluid, dynamic-color UI |
| **Navigation** | Navigation 3 | Modern, type-safe navigation graph |
| **Local Database** | Room (SQLite) | Caching standings, circuits, and TCG binder |
| **Preferences** | Jetpack DataStore | Reactive user preferences (driver, team, theme) |
| **Networking** | Retrofit & Ksoup | Fast JSON fetching and on-device RSS parsing |
| **Background Tasks** | WorkManager | Weekly card pack generation & session notifications |
| **Widgets** | Android Glance | Interactive home-screen widgets |

---

## ⚖️ Legal Disclosures & Grievance Redressal

- **Unofficial Status:** Taras is an independent, non-commercial fan application not affiliated with Formula One Licensing B.V. or FOM.
- **Grievance Officer:** Yash Ajagiya
- **Email:** [yashajagiya@gmail.com](mailto:yashajagiya@gmail.com)
- **Jurisdiction:** Gujarat, India
- **Repository:** [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)
- **Hosted Portal:** [https://yashajagiya.github.io/TarasF1/about.html](https://yashajagiya.github.io/TarasF1/about.html)
