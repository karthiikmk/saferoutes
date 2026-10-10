# SafeTrip: Autonomous AI Agent Highway Route & Safety Planner

[![Kotlin](https://img.shields.io/badge/Kotlin-Native%20%2F%20Android-7F52FF?logo=kotlin&logoColor=white)](#)
[![AI Agents](https://img.shields.io/badge/AI%20Agents-Gemini%20%26%20Claude-4285F4)](#)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](#)

> **SafeTrip (Project-TripAgent)** is an autonomous multi-agent road trip safety engine designed specifically for Indian highways and expressways. It moves beyond standard map routing by deploying autonomous AI agent evaluators that prioritize divided 4/6-lane corridors, verified toll plaza rest pins, secure parking, vehicle-specific SOS triggers, and emergency roadside assistance.

---

## 💡 Why SafeTrip? (The Origin Story)

> *"Conventional navigation apps like Google Maps are optimized for speed, not safety. But the fastest route isn't always the safest one."*

### The Problem
When driving long-distance across Indian highways—especially solo or at night—blindly following standard GPS algorithms can lead to dangerous situations. Standard navigation tools routinely shave off a few minutes by routing drivers through unlit rural shortcuts, unpaved village lanes, or isolated stretches with zero cell coverage, no fuel stations, and no nearby emergency assistance.

### The Spark Behind the Project
The idea for **SafeTrip** was born out of a frightening personal experience. While driving alone on a long intercity highway journey, I followed standard GPS navigation blindly. To save a few minutes, the app rerouted me off the main multi-lane national highway onto a pitch-black, deserted village track that ended deep inside a rural graveyard. Being stranded in an isolated location with no security or breakdown support made me realize a critical gap: **no existing navigation tool prioritizes driver safety over speed.**

Determined to solve this problem for myself and millions of highway drivers, I created **SafeTrip**—an autonomous AI agent routing system that strictly keeps drivers on well-lit, divided 4/6-lane expressways with audited rest stops, verified parking, and vehicle-specific SOS protocols.

---

https://github.com/user-attachments/assets/ab79bd22-abaa-4bbf-9395-bb1401cd768c

## 📱 App Screenshots

| 1. Welcome & Highlights | 2. Home & Last Trip | 3. Safe Route Planner | 4. Saved Trips Sync | 5. Route Details |
| :---: | :---: | :---: | :---: | :---: |
| <img width="150" height="350" alt="screenshot_1 (3)" src="https://github.com/user-attachments/assets/d5e41911-c9fb-4396-b461-502eb0b569f2" /> | <img width="150" height="350" alt="screenshot_2 (2)" src="https://github.com/user-attachments/assets/1b16933b-8c36-4275-a1e6-c39531e8b000" /> | <img width="150" height="350" alt="screenshot_3 (3)" src="https://github.com/user-attachments/assets/a388b8f9-7dc1-4782-9ee8-75d53dcff09f" /> | <img width="150" height="350" alt="screenshot_4 (1)" src="https://github.com/user-attachments/assets/7b4808de-478b-4da4-b5b4-35b89f4e60c0" /> | <img width="150" height="350" alt="screenshot_5" src="https://github.com/user-attachments/assets/479a3195-7b42-4eca-8996-5bb75ffddd03" /> |

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
