# Privacy Policy for Taras (F1 Companion)

**Effective Date:** October 5, 2026  
**Developer:** Yash Ajagiya  
**Source Code:** [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)  
**Hosted Policy:** [https://yashajagiya.github.io/tarasF1Data/privacy-policy.html](https://yashajagiya.github.io/tarasF1Data/privacy-policy.html)

---

## 🔒 Core Privacy Commitment

> **"We collect no personal data. Your name and favorite driver preferences are stored locally on your device via DataStore. No data is sent to any server. News is fetched from public RSS feeds."**

Taras is an open-source, non-commercial Formula 1 companion application built for motorsport fans. We believe in complete data privacy and open transparency.

---

## 1. Information We Do Not Collect

Taras does **not** collect, store, transmit, or share any personal information. Specifically:
- **No Personal Identifiers:** We do not collect names, email addresses, phone numbers, contacts, or account credentials.
- **No Location Tracking:** We do not track coarse or precise GPS/network location.
- **No Device Identifiers:** We do not collect Google Advertising IDs (GAID), Android IDs, IMEI, MAC addresses, or IP addresses.
- **No Commercial SDKs:** We do not bundle analytics trackers (such as Firebase Analytics, Mixpanel, or Facebook SDK) or ad networks (such as Google AdMob).
- **No Analytics / Telemetry:** We do not log your in-app browsing behaviors, search queries, or reading habits.

---

## 2. On-Device Storage (Strictly Local)

To provide personalization (such as greeting messages, widget customization, and theme selection), the app saves a few preferences **exclusively on your physical device** using Jetpack DataStore and Room SQLite database:

| Data Stored | Purpose | Storage Technology | Transmitted to Server? |
| :--- | :--- | :--- | :--- |
| **Nickname / Name** | Greeting on dashboard & home-screen widgets | Android Jetpack DataStore | ❌ **Never** |
| **Favorite Driver & Team** | Highlighting favorite driver stats & widgets | Android Jetpack DataStore | ❌ **Never** |
| **Theme & Language** | UI display (Light / Dark / Selected Language) | Android Jetpack DataStore | ❌ **Never** |
| **Standings & Schedule Cache** | Offline browsing and quick loading | Local SQLite (Room DB) | ❌ **Never** |

> **User Control:** You can instantly and permanently erase all stored data at any time via Android System Settings:  
> `Settings > Apps > Taras > Storage > Clear Data` (or by uninstalling the application).

---

## 3. Network Communications & Public Data

Taras uses the internet exclusively for fetching public sports and news content:
1. **Public Sports Data ([TarasF1Data](https://github.com/yashajagiya/tarasF1Data)):** Driver standings, constructor standings, race calendars, circuit information, and session schedules are downloaded from a public static GitHub repository. No user-specific data is passed in request queries or headers.
2. **Public News Feeds:** News headlines, summaries, and publication timestamps are parsed directly on your device from public RSS feeds published by motorsport journalism outlets (Motorsport.com, Autosport, RaceFans, PlanetF1).
3. **Transport Security:** All network requests are made strictly over encrypted Transport Layer Security (`HTTPS`).

---

## 4. Permissions Requested

- **`POST_NOTIFICATIONS` (Android 13+):**  
  Used solely to deliver local on-device race weekend reminders and session notifications scheduled by Android WorkManager. No remote push tokens, Firebase Cloud Messaging tokens, or device IDs are generated or stored.

---

## 5. Google Play Data Safety Declaration

In compliance with Google Play Developer Policy:
- **Data Collection:** **No data collected.**
- **Data Sharing:** **No data shared with third parties.**
- **Security Practices:** All internet communication uses encrypted protocols (`HTTPS`).
- **Data Deletion:** The app stores all data locally; clearing app data or uninstalling deletes everything.

---

## 6. Children's Privacy

Taras complies with the Children’s Online Privacy Protection Act (COPPA). We do not collect any personal information from children or any other users.

---

## 7. Open Source & Verifiability

Because Taras is open-source, anyone can inspect the source code to verify that no data is collected, tracked, or transmitted:  
👉 [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)

---

## 8. Contact & Inquiries

For questions, feedback, or concerns regarding this policy, please open an issue or reach out directly:
- **Developer:** Yash Ajagiya
- **GitHub:** [https://github.com/yashajagiya](https://github.com/yashajagiya)
- **Repository:** [https://github.com/yashajagiya/Taras](https://github.com/yashajagiya/Taras)
