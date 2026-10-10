# SafeTrip: Autonomous AI Agent Highway Route & Safety Planner

[![Kotlin](https://img.shields.io/badge/Kotlin-Native%20%2F%20Android-7F52FF?logo=kotlin&logoColor=white)](#)
[![AI Agents](https://img.shields.io/badge/AI%20Agents-Gemini%20%26%20Claude-4285F4)](#)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](#)

> **SafeTrip (Project-TripAgent)** is an autonomous multi-agent road trip safety engine designed specifically for Indian highways and expressways. It moves beyond standard map routing by deploying autonomous AI agent evaluators that prioritize divided 4/6-lane corridors, verified toll plaza rest pins, secure parking, vehicle-specific SOS triggers, and emergency roadside assistance.

---

## 📱 App Screenshots

| 1. Welcome & Highlights | 2. Home & Last Trip | 3. Safe Route Planner | 4. Saved Trips Sync | 5. Route Details |
| :---: | :---: | :---: | :---: | :---: |
| ![Welcome](./docs/screenshots/welcome.png) | ![Home](./docs/screenshots/home.png) | ![Planner](./docs/screenshots/planner.png) | ![Saved](./docs/screenshots/saved.png) | ![Details](./docs/screenshots/details.png) |

---

## 🏗️ Architecture & Multi-Agent Topologies

SafeTrip employs a multi-agent orchestration architecture:

```mermaid
flowchart TD
    User["User Input: Origin, Destination & Vehicle Specs"] --> Supervisor["Agent Supervisor & Orchestrator"]
    
    subgraph Agents ["Specialized Autonomous Agents"]
        Supervisor --> RouteAgent["Corridor & Routing Agent\n(Evaluates 4/6-lane divided expressways)"]
        Supervisor --> SafetyAgent["Safety & Risk Scoring Agent\n(Night driving hazard & incident analysis)"]
        Supervisor --> TollAgent["Toll Plaza & Rest Pin Verifier\n(Verified NHAI amenities & secure parking)"]
        Supervisor --> SOSAgent["Emergency & Roadside SOS Agent\n(Vehicle-specific breakdown protocols)"]
    end

    RouteAgent --> ToolExec["Tool Calling & Inference Layer\n(Server-side Gemini & Claude APIs)"]
    SafetyAgent --> ToolExec
    TollAgent --> ToolExec
    SOSAgent --> ToolExec

    ToolExec --> Synthesizer["Trip Plan Synthesizer & Schema Validator"]
    Synthesizer --> UI["Native Mobile UI & Interactive Itinerary"]
```

---

## ⚡ Key Capabilities
* **Expressway-First Routing**: Prioritizes access-controlled divided 4/6-lane corridors (e.g., NE-1, NE-4, Yamuna Expressway, Bangalore-Mysore Expressway) over poorly lit arterial state highways.
* **Autonomous Safety & Risk Scoring**: Real-time evaluation of night-driving hazards, isolated stretches, and weather vulnerabilities.
* **Verified Toll Plaza & Rest Pins**: Automatically identifies and bookmarks NHAI verified rest stops, fuel stations, and well-lit parking spots.
* **Vehicle-Specific SOS Routing**: Pre-maps brand service centers and emergency tow services along the active travel corridor.

---

## 🛠️ Tech Stack & Model Execution
* **Language & Architecture**: Kotlin, Android, Clean Architecture, Reactive flows.
* **Agentic Framework**: Server-side Gemini API & Claude API tool-calling pipelines.
* **Data Contracts**: Rigid JSON Schema contracts ensuring deterministic, hallucination-free route metadata.

---

## 👨‍💻 Author
**Karthik M K**  
Lead Software Engineer & Principal Mobile / Agentic Systems Architect  
* [GitHub](https://github.com/karthiikmk) | [LinkedIn](https://linkedin.com/in/karthiikmk) | [Medium](https://medium.com/@karthikmk)
