# 📋 Multi-Agent Bee Colony Simulation - Project Report

## Course Final Project Documentation

**Project Name:** Melissa - Multi-Agent Bee Hive Simulation  
**Technologies:** JaCaMo (Jason + CArtAgO + Moise), JavaFX, Gemini LLM  
**Report Date:** January 2026

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Project Overview](#2-project-overview)
3. [Technical Architecture](#3-technical-architecture)
4. [Agent System Design](#4-agent-system-design)
5. [Organization Specification (Moise)](#5-organization-specification-moise)
6. [Environment Artifacts (CArtAgO)](#6-environment-artifacts-cartago)
7. [LLM-Powered Wasp Intelligence](#7-llm-powered-wasp-intelligence)
8. [Battle System Mechanics](#8-battle-system-mechanics)
9. [Visualization System](#9-visualization-system)
10. [Running the Project](#10-running-the-project)
11. [Conclusion](#11-conclusion)

---

## 1. Executive Summary

**Melissa** is a sophisticated multi-agent system (MAS) that simulates a bee colony using the JaCaMo platform. The project demonstrates:

- **Autonomous Agent Behavior**: Bees operate independently using BDI (Belief-Desire-Intention) architecture
- **Role-Based Organization**: Agents adopt roles (Queen, Nurse, Sentinel, Explorer) based on age and colony needs
- **Social Coordination**: Agents coordinate through shared artifacts and organizational norms
- **AI vs MAS Battle**: An LLM-powered Wasp predator battles against JaCaMo-based Sentinel bees

The unique feature of this project is the **hybrid AI approach**: traditional multi-agent systems (JaCaMo bees) versus modern Large Language Model intelligence (Gemini-powered Wasp).

---

## 2. Project Overview

### 2.1 What the Project Does

The simulation models a realistic bee hive with the following behaviors:

| Behavior | Description |
|----------|-------------|
| **Honey Production** | Nurses convert collected pollen into honey |
| **Resource Management** | Explorers find pollen fields, collect, and store resources |
| **Temperature Control** | Sentinels heat or cool the hive to maintain optimal temperature |
| **Colony Reproduction** | Queen lays eggs, nurses feed larvae until they evolve into new bees |
| **Hive Defense** | Sentinels defend against an LLM-powered Wasp predator |
| **Agent Lifecycle** | Bees age, change roles, and eventually die |

### 2.2 Project Structure

```
melissa/
├── melissa.jcm                 # JaCaMo project configuration
├── src/
│   ├── agt/                    # Agent definitions (Jason/AgentSpeak)
│   │   ├── queen.asl           # Queen bee behavior
│   │   ├── worker.asl          # Worker bee behaviors (Nurse, Sentinel, Explorer)
│   │   └── wasp.asl            # LLM-powered Wasp agent
│   ├── env/                    # Environment components
│   │   ├── artifact/           # CArtAgO artifacts (Java)
│   │   │   ├── GeminiService.java   # LLM API integration
│   │   │   ├── WaspArtifact.java    # Wasp battle artifact
│   │   │   ├── HiveArtifact.java    # Hive management artifact
│   │   │   ├── MapArtifact.java     # Map and movement artifact
│   │   │   └── Parameters.java      # Simulation parameters
│   │   ├── graphic/            # JavaFX visualization
│   │   └── model/              # Domain models (Bee, Wasp, Hive, etc.)
│   ├── int/                    # Interaction specifications
│   └── org/                    # Organization structure (Moise)
│       └── organisation.xml    # Organizational specification
└── build.gradle                # Build configuration
```

---

## 3. Technical Architecture

### 3.1 JaCaMo Framework Components

JaCaMo integrates three technologies:

| Component | Technology | Purpose |
|-----------|------------|---------|
| **Agents** | Jason (AgentSpeak) | BDI agent programming language |
| **Environment** | CArtAgO | Artifact-based environment |
| **Organization** | Moise | Organizational specification |

### 3.2 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           JaCaMo Platform                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                    AGENTS (Jason/AgentSpeak)                      │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌────────┐  │   │
│  │  │  Queen  │  │  Nurse  │  │Sentinel │  │Explorer │  │  Wasp  │  │   │
│  │  │(monarch)│  │(12 init)│  │(4 init) │  │(20 init)│  │ (LLM)  │  │   │
│  │  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘  └───┬────┘  │   │
│  └───────┼───────────┼───────────┼───────────┼──────────────┼───────┘   │
│          │           │           │           │              │            │
│          ▼           ▼           ▼           ▼              ▼            │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                  ENVIRONMENT (CArtAgO Artifacts)                  │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐  │   │
│  │  │HiveArtifact│  │MapArtifact │  │WaspArtifact│  │SchemeBoard │  │   │
│  │  │- pollen    │  │- day       │  │- health    │  │- goals     │  │   │
│  │  │- honey     │  │- movement  │  │- position  │  │- missions  │  │   │
│  │  │- temp      │  │- pollenFld │  │- targets   │  │- norms     │  │   │
│  │  └────────────┘  └────────────┘  └─────┬──────┘  └────────────┘  │   │
│  └────────────────────────────────────────┼─────────────────────────┘   │
│                                           │                              │
│                                           ▼                              │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                    EXTERNAL SERVICES                              │   │
│  │  ┌────────────────────────────────────────────────────────────┐  │   │
│  │  │                    GeminiService                            │  │   │
│  │  │  - API Integration with Google Gemini LLM                   │  │   │
│  │  │  - Strategic attack decisions for Wasp                      │  │   │
│  │  │  - Fallback strategy when API unavailable                   │  │   │
│  │  └────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                    ORGANIZATION (Moise)                           │   │
│  │  ┌────────────────────────────────────────────────────────────┐  │   │
│  │  │  organisation.xml                                          │  │   │
│  │  │  - Structural Spec: Roles, Groups, Compatibility           │  │   │
│  │  │  - Functional Spec: Goals, Missions, Schemes               │  │   │
│  │  │  - Normative Spec: Obligations, Permissions                │  │   │
│  │  └────────────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Agent System Design

### 4.1 Agent Types and Roles

| Agent | File | Role | Initial Count | Technology |
|-------|------|------|---------------|------------|
| Queen | `queen.asl` | Monarch | 1 | JaCaMo BDI |
| Worker | `worker.asl` | Nurse | 12 | JaCaMo BDI |
| Worker | `worker.asl` | Sentinel | 4 | JaCaMo BDI |
| Worker | `worker.asl` | Explorer | 20 | JaCaMo BDI |
| Wasp | `wasp.asl` | Predator | 1 | **Gemini LLM** |

### 4.2 Agent Lifecycle and Role Transitions

Workers in the simulation follow a lifecycle where they change roles based on age:

```
         Birth (Day 0)
              │
              ▼
        ┌───────────┐
        │   NURSE   │  (Age 0-17)
        │ - feedLarvae
        │ - makeHoney
        │ - feedQueen
        └─────┬─────┘
              │ (Day 18)
              ▼
        ┌───────────┐
        │ SENTINEL  │  (Age 18-21)
        │ - heat/cool
        │ - defense
        └─────┬─────┘
              │ (Day 22)
              ▼
        ┌───────────┐
        │ EXPLORER  │  (Age 22-44)
        │ - searchPollen
        │ - collectPollen
        │ - storePollen
        └─────┬─────┘
              │ (Day 45+, 50% chance)
              ▼
           💀 Death
```

### 4.3 Agent Behaviors (Jason/AgentSpeak)

#### Queen Agent (`queen.asl`)

The Queen is the central agent that:
1. **Initializes the organization** - Creates the SchemeBoard artifact
2. **Creates environment artifacts** - HiveArtifact and MapArtifact
3. **Manages colony reproduction** - Lays eggs periodically
4. **Commits to missions** - mQueen, mFeeding, mRenewal, mTemperature

Key code from `queen.asl`:
```prolog
+!startOrg(Id)
<-  makeArtifact(Id, "ora4mas.nopl.SchemeBoard", ["src/org/organisation.xml", doSimulation], SchArtId);
    commitMission(mQueen)[artifact_id(SchArtId)];
    commitMission(mFeeding)[artifact_id(SchArtId)];
    ...

+!layEggs[scheme(Sch)] : energy(E) & not is_hungry
<-  -+energy(E-5);
    createLarva;
    .wait(7000);
    !!layEggs[scheme(Sch)].
```

#### Worker Agent (`worker.asl`)

Workers are versatile agents that:
1. **Register with organization** - Adopt roles and commit to missions
2. **Execute role-specific behaviors** - Based on current role
3. **Transition between roles** - Based on age
4. **Defend against wasp** - Sentinels flee and counter-attack

Key behaviors by role:

| Role | Behaviors |
|------|-----------|
| **Nurse** | `feedLarvae`, `makeHoney`, `feedQueen`, `feedSelf` |
| **Sentinel** | `heat`, `cool`, `feedSelf`, wasp defense |
| **Explorer** | `searchPollen`, `bringPollen`, `storePollen`, `feedSelf` |

---

## 5. Organization Specification (Moise)

### 5.1 Overview

The organization is defined in `src/org/organisation.xml` and follows the Moise organizational model with three specifications:

### 5.2 Structural Specification

Defines **roles** and **groups**:

```xml
<structural-specification>
    <role-definitions>
        <role id="monarch" />      <!-- Queen bee -->
        <role id="nurse" />        <!-- Care for larvae, make honey -->
        <role id="sentinel" />     <!-- Temperature control, defense -->
        <role id="explorer" />     <!-- Pollen collection -->
    </role-definitions>

    <group-specification id="hiveGroup">
        <roles>
            <role id="monarch" min="1" max="1"/>  <!-- Exactly 1 queen -->
            <role id="nurse" min="0"/>            <!-- Any number -->
            <role id="sentinel" min="0"/>
            <role id="explorer" min="0"/>
        </roles>
        
        <formation-constraints>
           <!-- Roles can transition between each other -->
           <compatibility from="nurse" to="sentinel" bi-dir="true"/>
           <compatibility from="sentinel" to="explorer" bi-dir="true"/>
           <compatibility from="explorer" to="nurse" bi-dir="true"/>
        </formation-constraints>
    </group-specification>
</structural-specification>
```

**Key Points:**
- Only ONE monarch (queen) is allowed
- Workers can change between nurse, sentinel, and explorer roles
- Role compatibility enables the age-based role transition system

### 5.3 Functional Specification

Defines the **goal hierarchy** and **missions**:

```
Goal Tree: survival (Root)
├── start                           → Initialize simulation
├── registerBee                     → Register agent in system
└── beeLife (parallel)
    ├── feeding (parallel)
    │   ├── pollenStockControl (sequence)
    │   │   └── collectPollen (sequence)
    │   │       ├── searchPollen
    │   │       └── bringPollen
    │   │   └── storePollen
    │   ├── makeHoney
    │   ├── feedSelf
    │   └── feedQueen
    ├── swarmRenewal (maintenance)
    │   ├── layEggs
    │   └── feedLarvae
    └── temperatureControl (choice)
        ├── heat
        └── cool
```

**Missions** (bundles of goals assigned to roles):

| Mission | Goals | Description |
|---------|-------|-------------|
| `mQueen` | registerBee, layEggs, start | Queen's responsibilities |
| `mNurse` | registerBee, makeHoney, feedQueen, feedLarvae, feedSelf | Nurse duties |
| `mSentinel` | registerBee, heat, cool, feedSelf | Sentinel duties |
| `mExplorer` | registerBee, searchPollen, bringPollen, storePollen, feedSelf | Explorer duties |
| `mFeeding` | feeding (management) | Oversees feeding goals |
| `mRenewal` | swarmRenewal (management) | Oversees reproduction |
| `mTemperature` | temperatureControl (management) | Oversees temperature |

### 5.4 Normative Specification

Defines **obligations** and **permissions** linking roles to missions:

```xml
<normative-specification>
    <properties>
        <property id="default_management" value="ignore" />
    </properties>

    <!-- Role-Mission Bindings -->
    <norm id="nExplorer" type="obligation" role="explorer" mission="mExplorer"/>
    <norm id="nSentinel" type="obligation" role="sentinel" mission="mSentinel"/>
    <norm id="nNurse" type="obligation" role="nurse" mission="mNurse"/>
    <norm id="nQueen" type="permission" role="monarch" mission="mQueen"/>
    
    <!-- Management missions for monarch -->
    <norm id="nFeeding" type="obligation" role="monarch" mission="mFeeding"/>
    <norm id="nRenewal" type="obligation" role="monarch" mission="mRenewal"/>
    <norm id="nTemperature" type="obligation" role="monarch" mission="mTemperature"/>
</normative-specification>
```

**Norm Types:**
- **Obligation**: Agent MUST commit to the mission
- **Permission**: Agent MAY commit to the mission

### 5.5 Organization Integration

In `melissa.jcm`, agents are assigned to the organization:

```
agent queen : queen.asl {
    instances: 1
    roles: monarch in hive
}

agent worker : worker.asl {
    instances: 12
    roles: nurse in hive
    beliefs: age(0)
}

organisation hiveOrg : organisation.xml {
    group hive : hiveGroup {
    }
}
```

---

## 6. Environment Artifacts (CArtAgO)

### 6.1 HiveArtifact

**Purpose:** Manages hive resources and internal state

**Observable Properties:**
| Property | Type | Description |
|----------|------|-------------|
| `pollen` | int | Current pollen stock |
| `honey` | int | Current honey stock |
| `intTemperature` | int | Internal hive temperature |
| `larvas` | int | Number of larvae |
| `heaters` | int | Number of bees heating |
| `coolers` | int | Number of bees cooling |

**Operations:**
- `hiveStart()` - Initialize hive with starting resources
- `heat()` / `cool()` - Adjust temperature
- `processPollen()` - Convert pollen to honey
- `createLarva()` - Create a new larva
- `eat(amount)` - Consume honey
- `feedLarva()` - Feed a larva

### 6.2 MapArtifact

**Purpose:** Manages the simulation map, bee movements, and pollen fields

**Observable Properties:**
| Property | Type | Description |
|----------|------|-------------|
| `day` | int | Current simulation day |
| `extTemperature` | int | External temperature |
| `hive` | x, y, w, h | Hive location and dimensions |
| `pollenField` | status, x, y, w, h | Pollen field locations |

**Operations:**
- `registerBee(role)` - Register a bee in the environment
- `move(direction)` / `move(x, y)` - Move bee
- `flyTo(x, y)` - Fly to specific coordinates
- `collect()` - Collect pollen from current position
- `changeRole(newRole)` - Change bee's role

### 6.3 WaspArtifact

**Purpose:** Manages the Wasp predator and battle system (see Section 7)

---

## 7. LLM-Powered Wasp Intelligence

### 7.1 Overview

The Wasp is a unique agent in this simulation that uses **Google's Gemini LLM** to make strategic decisions. This creates an exciting battle between:

| Side | Technology | Decision Making |
|------|------------|-----------------|
| 🐝 **Sentinels** | JaCaMo (BDI) | Rule-based, reactive |
| ☠️ **Wasp** | Gemini LLM | AI-powered, strategic |

### 7.2 Implementation Components

#### 7.2.1 Wasp Agent (`wasp.asl`)

The Wasp agent is defined in AgentSpeak but delegates strategic decisions to the LLM:

```prolog
// Agent wasp in project melissa
// LLM-powered predator agent that hunts sentinel bees

/* Initial beliefs */
health(100).
alive(true).
battle_started(false).

/* Initial goals */
!start_hunt.

/* Startup Plan */
+!start_hunt
<-  .print("=== WASP AGENT STARTED ===");
    joinWorkspace("hiveOrg", Workspace);
    makeArtifact("WaspBattle", "artifact.WaspArtifact", [], ArtId);
    focus(ArtId);
    .print("Battle artifact created. Let the hunt begin!").

/* React to battle ending */
+wasp_alive(false)
<-  .print("=== DEFEATED! Sentinels overwhelmed me! ===");
    .print("JaCaMo-based Sentinels are the WINNERS!").

+battle_active(false) : wasp_alive(true)
<-  .print("=== VICTORY! All sentinels eliminated! ===");
    .print("LLM-based Wasp Agent is the WINNER!").
```

#### 7.2.2 WaspArtifact (`WaspArtifact.java`)

The WaspArtifact is the bridge between the Wasp agent and the LLM service:

**Observable Properties:**
| Property | Description |
|----------|-------------|
| `wasp_position` | Current (x, y) position |
| `wasp_health` | Current health points (0-200) |
| `wasp_alive` | Boolean alive status |
| `attack_target` | LLM-decided target (x, y, reasoning) |
| `sentinel_count` | Number of remaining sentinels |
| `battle_active` | Whether battle is ongoing |

**Key Methods:**

1. **`delayedInit()`** - Waits for JavaFX, then starts battle loop
2. **`battleLoop()`** - Main battle loop that:
   - Scans for sentinel positions
   - Queries LLM for attack strategy
   - Moves toward target
   - Executes attacks
   - Handles counter-attacks

```java
@INTERNAL_OPERATION
void battleLoop() {
    while (battleActive && wasp.isAlive()) {
        // Scan for sentinels
        List<Position> sentinels = Environment.getInstance().getSentinelPositions();
        
        // Get LLM strategy
        GeminiService.AttackDecision decision = geminiService.getAttackStrategy(
            sentinels, wasp.getPosition(), mapWidth, mapHeight);
        
        // Move toward target
        while (wasp.distanceTo(target) > 20) {
            wasp.moveToward(targetX, targetY, 3);
            // Check for attacks and counter-attacks
        }
    }
}
```

#### 7.2.3 GeminiService (`GeminiService.java`)

The GeminiService handles all communication with Google's Gemini LLM API:

**Key Features:**
- **Singleton Pattern**: Single instance manages all API calls
- **Rate Limiting**: 5-second cooldown between API calls
- **Fallback Strategy**: Works without API key using algorithmic targeting
- **Structured Prompts**: Provides game context to LLM

**Prompt Engineering:**

The service builds detailed prompts for the LLM:

```java
private String buildPrompt(List<Position> sentinelPositions, Position waspPosition, 
                          int mapWidth, int mapHeight) {
    StringBuilder sb = new StringBuilder();
    sb.append("You are an AI controlling a wasp predator in a beehive simulation game. ");
    sb.append("Your goal is to hunt sentinel bees efficiently.\n\n");
    sb.append("GAME RULES:\n");
    sb.append("- ATTACK: You can kill 1 or 2 sentinels if they are within 50px of you\n");
    sb.append("- DANGER: If 3+ sentinels are within 100px, they will counter-attack\n");
    sb.append("- STRATEGY: Target isolated sentinels (1-2 together), avoid groups of 3+\n");
    sb.append("- Hive location is at bottom-right area - avoid entering!\n\n");
    
    sb.append("CURRENT STATE:\n");
    sb.append("- Your position: (" + waspPosition.getX() + ", " + waspPosition.getY() + ")\n");
    sb.append("- Sentinel positions:\n");
    for (Position p : sentinelPositions) {
        sb.append("  Sentinel: (" + p.getX() + ", " + p.getY() + ")\n");
    }
    
    sb.append("\nRESPOND IN THIS EXACT FORMAT:\n");
    sb.append("TARGET_X: <number>\n");
    sb.append("TARGET_Y: <number>\n");
    sb.append("REASONING: <brief explanation>\n");
    
    return sb.toString();
}
```

**Response Parsing:**

```java
private AttackDecision parseResponse(String jsonResponse, ...) {
    // Parse Gemini API JSON response
    String text = /* extract text from response */;
    
    int targetX = -1, targetY = -1;
    String reasoning = "LLM decision";
    
    for (String line : text.split("\n")) {
        if (line.startsWith("TARGET_X:")) {
            targetX = extractNumber(line);
        } else if (line.startsWith("TARGET_Y:")) {
            targetY = extractNumber(line);
        } else if (line.startsWith("REASONING:")) {
            reasoning = line.substring("REASONING:".length()).trim();
        }
    }
    
    return new AttackDecision(targetX, targetY, reasoning);
}
```

**Fallback Strategy:**

When LLM is unavailable, uses algorithmic targeting:

```java
private AttackDecision getDefaultDecision(List<Position> sentinelPositions, Position waspPosition) {
    // Find clusters of 1-3 sentinels (avoid large groups)
    int bestX = 0, bestY = 0, bestCount = 0;
    
    for (Position p : sentinelPositions) {
        int count = countNearby(p, sentinelPositions, 100);
        // Prefer clusters of 2-3 (avoid 4+ to minimize counter-attack risk)
        if (count >= 1 && count <= 3 && count > bestCount) {
            bestCount = count;
            bestX = p.getX();
            bestY = p.getY();
        }
    }
    
    return new AttackDecision(bestX, bestY, "Fallback: targeting cluster of " + bestCount);
}
```

#### 7.2.4 Wasp Model (`Wasp.java`)

The Wasp entity model:

```java
public class Wasp {
    private int health = 200;        // Max 200 HP
    private int maxHealth = 200;
    private int attackRadius = 50;   // Attack range in pixels
    private int maxKillsPerAttack = 3;
    private double speedMultiplier = 1.0;
    
    // Cannot enter hive area (protected zone)
    public boolean moveToward(int targetX, int targetY, int baseSpeed) {
        if (isInsideHive(newX, newY)) {
            return false;  // Blocked
        }
        // Move toward target
    }
}
```

### 7.3 LLM Integration Flow

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        LLM Integration Flow                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  1. WaspArtifact.battleLoop()                                           │
│         │                                                                │
│         ▼                                                                │
│  2. Get Sentinel Positions from Environment                              │
│         │                                                                │
│         ▼                                                                │
│  3. GeminiService.getAttackStrategy(sentinels, waspPos, mapSize)        │
│         │                                                                │
│         ├──────────────────────────────────────────────────┐            │
│         │                                                   │            │
│         ▼                                                   ▼            │
│  ┌─────────────────┐                              ┌─────────────────┐   │
│  │ API Key Found   │                              │ No API Key      │   │
│  │                 │                              │                 │   │
│  │ buildPrompt()   │                              │ getDefault-     │   │
│  │      │          │                              │ Decision()      │   │
│  │      ▼          │                              │                 │   │
│  │ callGeminiAPI() │                              │ Algorithmic     │   │
│  │      │          │                              │ targeting       │   │
│  │      ▼          │                              │                 │   │
│  │ parseResponse() │                              │                 │   │
│  └────────┬────────┘                              └────────┬────────┘   │
│           │                                                 │            │
│           └──────────────────┬──────────────────────────────┘            │
│                              │                                           │
│                              ▼                                           │
│  4. Return AttackDecision {targetX, targetY, reasoning}                 │
│         │                                                                │
│         ▼                                                                │
│  5. Wasp moves toward target, attacks sentinels                         │
│         │                                                                │
│         ▼                                                                │
│  6. Loop continues until victory or defeat                              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Battle System Mechanics

### 8.1 Combat Rules

| Mechanic | Wasp | Sentinels |
|----------|------|-----------|
| **Attack Range** | 50px (instant kill) | 100px (counter-attack zone) |
| **Damage Output** | Kills 1-2 sentinels | 20 HP per counter-attack (10% of max) |
| **Attack Cooldown** | 500ms | 1000ms |
| **Win Condition** | Eliminate all sentinels | Reduce Wasp HP to 0 |

### 8.2 Battle Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        Battle Flow                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                  INITIALIZATION                          │    │
│  │  1. Wasp spawns at (10, 10) - top-left corner           │    │
│  │  2. Wait for sentinels to register (~17 sentinels)      │    │
│  │  3. Battle begins announcement                           │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                   │
│                              ▼                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   BATTLE LOOP                            │    │
│  │                                                          │    │
│  │   ┌───────────────────────────────────────────────┐     │    │
│  │   │ Wasp scans for sentinels                      │     │    │
│  │   │           │                                    │     │    │
│  │   │           ▼                                    │     │    │
│  │   │ LLM decides optimal target                     │     │    │
│  │   │ (isolated sentinel, avoid groups)              │     │    │
│  │   │           │                                    │     │    │
│  │   │           ▼                                    │     │    │
│  │   │ Wasp moves toward target (3px/50ms)           │     │    │
│  │   │           │                                    │     │    │
│  │   │           ▼                                    │     │    │
│  │   │ ┌─────────────────────────────────────────┐   │     │    │
│  │   │ │ ATTACK CHECK (every 50ms)               │   │     │    │
│  │   │ │                                         │   │     │    │
│  │   │ │ If 1-2 sentinels within 50px:           │   │     │    │
│  │   │ │   → Wasp kills them                     │   │     │    │
│  │   │ │                                         │   │     │    │
│  │   │ │ If 2+ sentinels within 100px:           │   │     │    │
│  │   │ │   → Sentinels counter-attack (20 dmg)   │   │     │    │
│  │   │ └─────────────────────────────────────────┘   │     │    │
│  │   │           │                                    │     │    │
│  │   │           ▼                                    │     │    │
│  │   │ Repeat until target reached or wasp dead      │     │    │
│  │   └───────────────────────────────────────────────┘     │    │
│  │                                                          │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                   │
│                              ▼                                   │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   VICTORY CHECK                          │    │
│  │                                                          │    │
│  │  • Sentinels = 0  →  "LLM-based Wasp Agent WINNER!"     │    │
│  │  • Wasp HP = 0    →  "JaCaMo-based Sentinels WINNER!"   │    │
│  │                                                          │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 8.3 Sentinel Defense Behavior

Sentinels in `worker.asl` have defense plans:

```prolog
// React to wasp detection - flee to hive
+wasp_detected(WX, WY) : role(sentinel) & not fleeing
<-  .print("ALERT! Wasp detected! Fleeing to hive!");
    +fleeing;
    !flee_to_hive.

// Flee to hive plan
+!flee_to_hive : role(sentinel) & fleeing
<-  lookupArtifact("Map", AId);
    focus(AId);
    ?hive(HX, HY, HW, HH)[artifact_id(AId)];
    // Calculate random point inside hive
    TargetX = HX + math.floor(HW * random);
    TargetY = HY + math.floor(HH * random);
    flyTo(TargetX, TargetY);
    .print("Reached hive safety!");
    .wait(3000);
    -fleeing.
```

---

## 9. Visualization System

### 9.1 JavaFX Graphics

The visualization is handled by the `graphic` package:

| Class | Purpose |
|-------|---------|
| `EnvironmentApplication` | Main JavaFX application |
| `Environment` | Manages all visual elements |
| `BeeGraphic` | Renders bee entities |
| `HiveGraphic` | Renders the hive |
| `PollenFieldGraphic` | Renders pollen fields |

### 9.2 Visual Elements

| Element | Color | Size | Description |
|---------|-------|------|-------------|
| Queen | Gold | 6px | Single queen bee |
| Nurse | Blue | 4px | Larva feeders |
| Sentinel | Red | 4px | Defenders |
| Explorer | Yellow | 4px | Pollen gatherers |
| **Wasp** | **Dark Red** | **12px** | LLM predator |
| Hive | Brown | 150x150px | Bottom-right corner |
| Pollen Field | Green | Variable | Resource locations |

---

## 10. Running the Project

### 10.1 Prerequisites

- **Java JDK 11+** with JavaFX support
- **Gradle** (included via wrapper)
- **Gemini API Key** (optional - fallback available)

### 10.2 Setup

```bash
# Clone the repository
git clone https://github.com/hakkikeman/Multi-Agent-Bee-Colony-LLM.git
cd Multi-Agent-Bee-Colony-LLM

# Configure Gemini API (optional)
echo "gemini.api.key=YOUR_API_KEY" > src/env/artifact/gemini-config.properties

# Run with Gradle
./gradlew run
```

### 10.3 Getting a Gemini API Key

1. Go to [Google AI Studio](https://aistudio.google.com/apikey)
2. Sign in with your Google account
3. Click **"Create API Key"**
4. Copy the key to `gemini-config.properties`

---

## 11. Conclusion

### 11.1 Summary

This project successfully demonstrates:

1. **Multi-Agent Systems**: Complex bee colony simulation with autonomous agents
2. **BDI Architecture**: Agents with beliefs, desires, and intentions
3. **Organizational Coordination**: Moise-based role and mission management
4. **Environment Interaction**: CArtAgO artifacts for shared resources
5. **Hybrid AI Integration**: LLM-powered decision making vs rule-based agents

### 11.2 Key Innovations

| Innovation | Description |
|------------|-------------|
| **LLM Integration** | First integration of Gemini LLM into JaCaMo environment |
| **Hybrid Battle** | Unique comparison of MAS vs LLM approaches |
| **Fallback System** | Works with or without API key |
| **Real-time Combat** | Dynamic battle with attack/counter-attack mechanics |

### 11.3 Learning Outcomes

- Understanding of JaCaMo platform components
- Agent programming with Jason/AgentSpeak
- Organizational modeling with Moise
- Environment design with CArtAgO
- LLM API integration and prompt engineering

---

## Appendix A: File Reference

| File | Purpose |
|------|---------|
| `melissa.jcm` | JaCaMo project configuration |
| `src/org/organisation.xml` | Moise organization specification |
| `src/agt/queen.asl` | Queen agent behavior |
| `src/agt/worker.asl` | Worker agent behavior (all roles) |
| `src/agt/wasp.asl` | LLM-powered Wasp agent |
| `src/env/artifact/HiveArtifact.java` | Hive resource management |
| `src/env/artifact/MapArtifact.java` | Map and movement |
| `src/env/artifact/WaspArtifact.java` | Wasp battle system |
| `src/env/artifact/GeminiService.java` | LLM API integration |
| `src/env/model/Wasp.java` | Wasp entity model |
| `src/env/model/Bee.java` | Bee entity model |

---

*Report prepared for Multi-Agent Systems Course Final Project*
