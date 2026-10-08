# Privacy Policy for Taras (F1 Companion)

**Effective Date:** October 8, 2026  
**Developer & Data Fiduciary:** Yash Ajagiya  
**Jurisdiction:** India  
**Applicable Legal Framework:** Digital Personal Data Protection Act, 2023 (DPDPA 2023), Information Technology Act, 2000 & IT Rules, 2021  
**Source Code:** [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)  
**Hosted Policy:** [https://yashajagiya.github.io/TarasF1/privacy-policy.html](https://yashajagiya.github.io/TarasF1/privacy-policy.html)  

---

## 🔒 Core Privacy Commitment

> **"We collect no digital personal data on any server. Your nickname, favorite driver preferences, and trading card binder are stored exclusively on your physical device using Android Jetpack DataStore and Room SQLite. No telemetry or advertising trackers exist in the app. Public motorsport news and statistics are fetched securely over HTTPS."**

Taras (तरस्) is an open-source, non-commercial Formula 1 companion application developed by Yash Ajagiya for motorsport enthusiasts. We believe in absolute data privacy, full transparency, and uncompromising adherence to Indian and international data protection standards.

---

## 1. Compliance with the Digital Personal Data Protection Act, 2023 (DPDPA 2023)

In accordance with the **Digital Personal Data Protection Act, 2023** of the Republic of India:

1. **Data Fiduciary:** Yash Ajagiya acts as the Data Fiduciary for Taras.
2. **Grounds of Processing & Purpose Limitation (Section 6):** Any data input into the application (such as your chosen nickname, selected team/driver allegiance, or theme preference) is processed solely for the legitimate purpose of rendering personalized local UI displays and home-screen widgets on your Android device.
3. **Data Minimization:** Taras adheres strictly to the principle of data minimization. The application requires **zero** registration, zero accounts, zero phone numbers, and zero email addresses to operate.
4. **No Server Collection:** No personal identifiers are transmitted, ingested, stored, or processed on external cloud servers, databases, or third-party infrastructure.

---

## 2. Information We Do Not Collect

Taras does **not** collect, store, transmit, or share any personal information. Specifically:
- **No Personal Identifiers:** We do not collect names, email addresses, phone numbers, contacts, or account credentials.
- **No Location Tracking:** We do not track coarse or precise GPS or network-based geographic location.
- **No Device Identifiers:** We do not collect Google Advertising IDs (GAID), Android IDs, IMEI, MAC addresses, or IP logs.
- **No Commercial SDKs:** We do not bundle commercial analytics trackers (such as Firebase Analytics, Mixpanel, Segment) or ad networks (such as Google AdMob).
- **No Telemetry / Behavioral Profiling:** We do not log your in-app browsing habits, search queries, or reading history.

---

## 3. On-Device Storage (Strictly Local Sandbox)

To deliver seamless offline usability, fast loading, and widget personalization, the app stores a few configuration preferences **exclusively on your physical device** within Android's sandboxed storage:

| Data Stored | Purpose | Storage Technology | Transmitted to Server? |
| :--- | :--- | :--- | :--- |
| **Nickname** | Greeting on dashboard & home-screen widgets | Android Jetpack DataStore | ❌ **Never** |
| **Favorite Driver & Team** | Highlighting favorite driver stats & widgets | Android Jetpack DataStore | ❌ **Never** |
| **Theme & Language** | UI display (Light / Dark / Selected Language) | Android Jetpack DataStore | ❌ **Never** |
| **F1 GridTCG Collection** | Unlocked digital cards, pack counters & arena stats | Local SQLite (Room DB) | ❌ **Never** |
| **Standings & Schedule Cache** | Offline browsing and rapid app start | Local SQLite (Room DB) | ❌ **Never** |

---

## 4. Rights of the Data Principal (Sections 11–14, DPDPA 2023)

Under the Digital Personal Data Protection Act, 2023, you enjoy statutory rights as a Data Principal:

1. **Right to Access Information (Section 11):** You have the right to inspect all personal data processed. In Taras, all your data (nickname, preferences, and cards) is visible directly on-device within the app settings, drawer, and binder.
2. **Right to Correction & Updating (Section 12):** You can update, correct, or modify your nickname, theme, and favorite constructor/driver anytime through the Settings Drawer or Onboarding Setup.
3. **Right to Erasure & Data Deletion (Section 12):** Because all data resides strictly on your physical phone, you have autonomous, immediate power to erase all data permanently at any time:
   - Navigate to: `Android Settings > Apps > Taras > Storage > Clear Data`
   - Alternatively, uninstalling the application permanently deletes 100% of local databases, preferences, and cached assets.
4. **Right to Grievance Redressal (Section 13):** You have the right to seek redressal of any grievance regarding personal data directly from our designated Grievance Officer.
5. **Right to Nominate (Section 14):** You maintain the legal right to nominate an individual to exercise your rights in the event of death or incapacity, as provided under Indian law.

---

## 5. Children's Personal Data (Section 9, DPDPA 2023)

In accordance with Section 9 of the DPDPA 2023 and the Children’s Online Privacy Protection Act (COPPA):
- Taras does **not** process identifiable personal data of children.
- Taras does **not** undertake any tracking, behavioral monitoring, or targeted advertising directed at children or minors.
- The application is a family-friendly, educational, and recreational companion suitable for motorsport enthusiasts of all age groups.

---

## 6. Network Communications & Public Data Sources

Taras uses network connectivity exclusively for retrieving public motorsport content over encrypted channels:

1. **Public Sports Data ([TarasF1Data](https://github.com/yashajagiya/tarasF1Data)):** Driver standings, constructor standings, race calendars, circuit information, and session schedules are downloaded from a public static GitHub repository. No user-specific data is passed in request queries or HTTP headers.
2. **Public News Feeds (Intermediary Status):** News headlines, summaries, and publication timestamps are parsed directly on your device from public RSS feeds published by motorsport journalism outlets (Motorsport.com, Autosport, RaceFans, PlanetF1).
3. **Transport Layer Security (TLS/HTTPS):** All network requests are made strictly over encrypted Transport Layer Security (`HTTPS`).

---

## 7. Device Permissions Requested

- **`POST_NOTIFICATIONS` (Android 13+):**  
  Used solely to deliver local on-device race weekend reminders and session countdowns scheduled by Android WorkManager. No remote push tokens, Firebase Cloud Messaging (FCM) tokens, or device IDs are generated or stored.
- **`INTERNET`:**  
  Used solely to fetch public read-only schedule JSON and public RSS news articles over HTTPS.

---

## 8. Reasonable Security Practices (Section 43A, IT Act 2000)

Under Section 43A of the Information Technology Act, 2000 and the Information Technology (Reasonable Security Practices and Procedures and Sensitive Personal Data or Information) Rules, 2011:
- All local data is isolated within Android's protected application sandbox.
- No sensitive personal data or financial information is ever requested, processed, or held.
- All network interactions utilize secure HTTPS with standard encryption protocols.

---

## 9. Google Play Data Safety Declaration

In compliance with Google Play Developer Policy:
- **Data Collection:** **No data collected.**
- **Data Sharing:** **No data shared with third parties.**
- **Security Practices:** Encrypted in transit (`HTTPS`); protected by Android sandbox.
- **Data Deletion:** Full autonomous deletion by user via clearing app data or uninstalling.

---

## 10. Grievance Redressal Mechanism & Grievance Officer

In compliance with Rule 3(2) of the **Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules, 2021** and Section 13 of the **Digital Personal Data Protection Act, 2023**, the details of the designated Grievance Officer are set forth below:

- **Grievance Officer:** Yash Ajagiya
- **Role:** Data Protection & Grievance Redressal Officer
- **Email:** [yashajagiya@gmail.com](mailto:yashajagiya@gmail.com)
- **GitHub Repository:** [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)
- **Jurisdiction & Location:** Gujarat, India

### Statutory Timelines:
- **Acknowledgment:** Any grievance or complaint will be acknowledged within **48 hours** of receipt.
- **Redressal:** Redressal and resolution shall be completed within **15 to 30 days** in accordance with the IT Rules, 2021 and DPDPA, 2023.

---

## 11. Open Source & Verifiability

Because Taras is open-source under the Apache License 2.0, anyone can inspect the source code to independently audit and verify that no data is collected, tracked, or transmitted:  
👉 [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)
