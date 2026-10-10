# 🛣️ SafeRoutes — Audited Highway Corridors & AI Route Planner

**SafeRoutes** is an Android application designed for safe long-distance driving across Indian highway corridors. It prioritizes road safety, audited highway stopovers, 24x7 emergency SOS protocols, and multi-vehicle garage management over simply finding the shortest route.

---

## 🌟 Key Features

1. **🛣️ Audited Highway Corridors (Home):**
   * Curated safety data for major corridors (e.g., Chennai → Bengaluru NH48, Bengaluru → Hyderabad NH44, Mumbai → Pune Expressway).
   * Weather alerts, road surface quality ratings, recommended break schedules, and toll estimates.

2. **🤖 Multi-Engine AI Route Planner (Plan):**
   * Dynamic routing powered by AI models with central cloud management via Firebase Firestore.
   * Supports **OpenRouter (`openai/gpt-4o`)**, **Tavily**, **Google Gemini**, **Anthropic Claude**, and **SiliconFlow**.
   * Controlled dynamically in Firestore via `/config/routing_control`.

3. **🔖 Saved Routes & Offline Cache:**
   * Clean Room SQLite integration with Cloud Firestore synchronization (`/users/{email}/route_history`).
   * Prevents duplication and provides seamless offline accessibility.

4. **🚨 Emergency Hub & 1033 Helpline (SOS):**
   * Instant 1-tap call triggers for **1033 NHAI Highway Emergency Helpline** and OEM Roadside Assistance (RSA).
   * Live GPS coordinates and nearest police station / hospital locator.

5. **🚗 Multi-Vehicle Garage:**
   * Manage multiple cars (e.g., Tata Nexon, Hyundai i20, Mahindra XUV700).
   * Recommends cold tyre pressure (PSI), FASTag status, connected car status (Bluelink/iRA), and SOS instructions.

---

## 📱 App Screenshots

| 1. Home / Corridors | 2. AI Route Planner | 3. Route Details | 4. Emergency SOS | 5. Vehicle Garage |
| :---: | :---: | :---: | :---: | :---: |
| ![Home](docs/screenshots/home.png) | ![Planner](docs/screenshots/planner.png) | ![Details](docs/screenshots/details.png) | ![SOS](docs/screenshots/sos.png) | ![Garage](docs/screenshots/garage.png) |

---

## 🛠️ Architecture & Tech Stack

* **Language:** 100% Kotlin
* **UI Framework:** Jetpack Compose with Material Design 3
* **Database:** Room SQLite (v3 schema) + Firebase Firestore
* **Authentication:** Google Sign-In via Jetpack Credential Manager & Firebase Auth
* **Networking:** Retrofit2, OkHttp3, and Firebase Firestore SDK
* **Asynchrony:** Kotlin Coroutines & `Flow` (`StateFlow`, `flatMapLatest`)

---

## 🚀 Pushing to GitHub

To push this codebase directly to your GitHub repository:
1. Click the **Export / GitHub** button in the top-right header of Google AI Studio.
2. Select **Push to GitHub** or **Download ZIP**.
3. All code, database schemas, Firestore rules, and README documentation will be committed to your repository.
