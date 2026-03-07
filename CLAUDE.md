# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Norii is a turn-based strategy game built with [libGDX](https://libgdx.com/) (Java). It supports desktop (LWJGL3), HTML (GWT), and headless backends. The game features single-player vs AI and online multiplayer modes.

## Build & Run Commands

All commands run from `norii/norii/` (the root Gradle project directory).

```bash
# Run the desktop game
./gradlew lwjgl3:run

# Run all tests
./gradlew tests:test

# Run a single test class
./gradlew tests:test --tests "test.ai.TestAiDecisionMaker"

# Build a runnable JAR
./gradlew lwjgl3:jar

# Build HTML/GWT version
./gradlew html:dist

# Build installer packages (Windows: .exe/.msi, Linux: .deb/.rpm)
./gradlew lwjgl3:jpackage
```

## Project Structure

The Gradle multi-project build has these subprojects:

- **`core/`** — All game logic (platform-agnostic). Source at `core/src/main/java/com/jelte/norii/`.
- **`lwjgl3/`** — Desktop launcher (`Lwjgl3Launcher`). Working directory for `run` is `assets/`.
- **`html/`** — GWT/browser launcher.
- **`headless/`** — Headless backend (used by tests).
- **`tests/`** — JUnit 5 tests using the headless GDX backend. Sources are at `tests/src/test/` but declared as `sourceSets.test.java.srcDirs = ["src/main/java"]`.
- **`assets/`** — All game assets (maps, audio, spells JSON, unit stats JSON, skins). Shared by `lwjgl3` and `tests` via `sourceSets.main.resources.srcDirs`.

## Architecture

### Entry Point & Screen Management
`Norii.java` extends `Game` and delegates all screen transitions to the singleton `ScreenManager`. Screens are identified by `ScreenEnum` values (LOGIN, MAIN_MENU, BATTLE, etc.). `ScreenManager.showScreen()` disposes the old screen and sets the new one.

### Battle Flow
`BattleScreen` is the main game screen, which owns:
- **`BattleManager`** — Central controller. Implements a state machine with `BattlePhase` objects: `DeploymentBattlePhase` → `SelectUnitBattlePhase` → `MovementBattlePhase` / `AttackBattlePhase` / `SpellBattlePhase` / `ActionBattlePhase` → `WaitOpponentBattlePhase`. State transitions are triggered via `sendMessageToBattleScreen(MessageToBattleScreen, ...)`.
- **`BattleState`** — A 2D grid (`BattleCell[][]`) representing the current board state, tracking unit positions, HP, and modifiers. Used both for live gameplay and AI lookahead.
- **`BattleStateModifier`** — Applies a `UnitTurn` (sequence of `Move` objects) to a `BattleState` and returns a new state. This is the core simulation engine.

### AI System
`AITeamLeader` implements `UnitOwner` for the AI-controlled team. It calls `AIDecisionMaker.processAi()` incrementally across frames (time-budgeted at 20 seconds max). The decision maker runs a 3-layer minimax-style search:
1. `TargetMoveGenerator` / `AbilityMoveGenerator` generate all possible `UnitTurn` objects for each unit.
2. `BattleStateModifier` simulates each turn to produce candidate `BattleState` instances.
3. Top states are pruned each round (`SAVE_TOP_X_STATES_EVERY_ROUND = 15`), and a random pick from the top 5 results adds variation.

### Entities
`Entity` is the game unit class. It has:
- `EntityData` — stats loaded from `assets/units/unitStats.json` via `EntityFileReader`.
- `EntityVisualComponent` / `FakeEntityVisualComponent` — rendering vs headless stub (used in tests).
- `Ability` — a unit's special spell, loaded from `assets/spells/spells.json` via `SpellFileReader`.

### Multiplayer
`ServerCommunicator` handles WebSocket communication. `OnlineEnemy` implements `UnitOwner` for remote opponents, receiving moves via `MyWebSocketAdapter`.

### King Mechanic
Each team can have a "King" unit (currently only the **Commander**, marked `"isKing": true` in `unitStats.json`). When the King dies, that team immediately loses — regardless of remaining units. If neither team has a King, the old "all units dead" condition applies (used by the Tutorial level). The AI scoring (`BattleState.calculateScore()`) weights king survival at ±200 points so the AI actively prioritizes attacking/protecting kings.

### Key Data Files
- `assets/units/unitStats.json` — Unit stat definitions.
- `assets/spells/spells.json` — Spell/ability definitions.
- `assets/levels/levels.json` — Level configurations.
- `assets/levels/ap.json` — Action point schedules per turn.
- `assets/maps/` — Tiled map `.tmx` files plus precomputed `_paths.txt` / `_lineOfSights.txt`.

### Test Infrastructure
Tests use `GdxTestRunner` (a JUnit 5 extension) to initialize the headless libGDX backend. `FakeEntityVisualComponent` stubs out rendering. Test fixtures live in `assets/tests/`.
