# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Created a GUI mockup for the following cases: 
	- start screen
	- game screen
	- game screen during turn
	- game screen during point calculation
	- victory
	- defeat 
	(2025-06-16 to 2025-06-22, by Evelyn Molleker)
	
- Created this CHANGELOG-file (2025-06-22, by Evelyn Molleker)

- Created a Client/Server Sequence Diagram
(2025-06-16 to 2025-06-22, by Mohamed Takieddine Ben Ayed)

- Created File for "Spielkonzept und Umsetzung" (2025-06-22, by Nadine Mueller)

- Created "docs/Kartenlogik Discard, Select.md"
- Spotless created
- Pipeline created 
  (2025-06-22, by Eda Yaramanci)

- Added scoring_rules.md with poker-based point calculation logic.
  (2025-06-22, by Yunxi Zhang)

- Created the Pipeline and added Class-Diagram, initialized the Setup
  (2025-06-22, by Eren Acar)

- Created the class Client (2025-06-25, Mohamed Takieddine Ben Ayed)

- Created the class PokerHand (2025-06-27, by Nadine Müller)

- Created the Server and ClientHandler class. (2025-06-28, Eren Acar)

- Created the class Boss (2025-06-29, Yunxi Zhang)

- Created the class Game (2025-06-29, Yunxi Zhang)

- Added images for the GUI (cards + startscreen background) (2025-06-29, Evelyn Molleker)

- Added fonts (Righteous and Shojumaru) for the GUI (2025-06-29, Evelyn Molleker)

- Added implementation for...
	- Main.java
	- ScreenManager.java
	- StartScreen.java
	- CardView.java
	- GameScreen.java
	(2025-06-29, Evelyn Molleker)

- Added style.css for the GUI (2025-06-29, Evelyn Molleker)

- Created the class Player (2025-06-29, Eda Yaramanci)

- Created the GameState class (2025-07-06, Mohamed Takieddine Ben Ayed)

- Added a GUI for the Lobby (2025-07-02, Evelyn Molleker)

- Added a GUI for the ResultScreen (for winning and losing) (2025-07-02, Evelyn Molleker)

- GameScreen:
	- made the cards clickable
	- added pop-up-box with buttons for 'Play Hand' and 'Discard'
	- added functionality to the buttons 'Play Hand' and 'Discard'
	(2025-07-04, Evelyn Molleker)

- GameScreen:
	- made the cards a little bit bigger
	- added that only the cards of the active player are clickable
	- added that the cards of the inactive player are grayed out 
	- added that the discarded cards are given to the other player the first time they are discarded, the second time they are discarded permanently (not merged with main branch yet)
	(2025-07-06, Evelyn Molleker)

- Game is playable on Console with basic commands (2025-07-04, Eren Acar)

### Changed

- Moved all images and docs to docs/ folder (2025-06-22, by Eda Yaramanci)

- Modified the Json logic in Client class (2025-06-28, Eren Acar)

- Changed player class (2025-06-29, Yunxi Zhang)

- Added JavaFX to build.gradle.kts (2025-06-29, Evelyn Molleker)

- Changed JDK24 to JDK 21 in the CI for Gradle toolchain compatibility (2025-06-29, Evelyn Molleker)

- Modified the Server and ClientHandler classes so
    - They can deserialize the incoming JSON's from the Controller. (2025-07-02, Eren Acar)

- Modified the StartScreenController	
    - Server is notified when both players connect to the game. (2025-07-05, Eren Acar)

### Removed

### Fixed