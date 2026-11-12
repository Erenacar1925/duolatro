# Server Architecture

## Overview
The server follows a clean MVC (Model-View-Controller) architecture with proper separation of concerns.

## Directory Structure
```
src/main/java/server/
├── Server.java              # Main server class
├── ClientHandler.java       # Handles individual client connections
├── model/                   # Game logic and data models
│   ├── Game.java
│   ├── Player.java
│   ├── Card.java
│   ├── Deck.java
│   ├── Boss.java
│   ├── PokerHand.java
│   └── Message.java
├── controller/              # Business logic controllers
│   ├── LobbyController.java
│   └── GameController.java
└── view/                    # Server-side view/logging
    └── ServerLogger.java
```

## Components

### Server.java
- Main server class that manages client connections
- Maintains a list of connected players
- Provides broadcasting functionality
- Uses proper logging with ServerLogger

### ClientHandler.java
- Handles individual client socket connections
- Parses incoming JSON messages
- Routes messages to appropriate controllers
- Manages player lifecycle (connect/disconnect)

### Controllers

#### LobbyController
Handles lobby-related operations:
- `handlePlayerConnect(String playerName)` - New player joins
- `handlePlayerDisconnect(String playerId)` - Player leaves
- `handleGetPlayerList(List<Player> players)` - Get current players
- `handleStartGameRequest(List<Player> players)` - Start game validation

#### GameController
Handles game-related operations:
- `handleStartGame(List<Player> players)` - Initialize new game
- `handleDealCards()` - Deal initial hands
- `handlePlayHand(String playerId, List<Card> cards)` - Process hand play
- `handleDiscardCard(String playerId, Card card)` - Process card discard
- `handleEndRound()` - End current round

### Message Protocol
All communication uses JSON messages with this structure:
```json
{
  "type": "MESSAGE_TYPE",
  "content": "message_data"
}
```

#### Supported Message Types
- `CONNECT` - Player connects to lobby
- `DISCONNECT` - Player disconnects
- `GET_PLAYER_LIST` - Request current players
- `START_GAME` - Request to start game
- `DEAL_CARDS` - Deal cards to players
- `PLAY_HAND` - Play selected cards
- `DISCARD_CARD` - Discard a card
- `END_ROUND` - End current round

## Usage

### Starting the Server
```java
public static void main(String[] args) {
    new Server().start();
}
```

### Running Tests
```bash
mvn test
```

## Logging
The server uses `ServerLogger` for structured logging:
- `ServerLogger.info(String message)` - Information messages
- `ServerLogger.warning(String message)` - Warning messages  
- `ServerLogger.error(String message)` - Error messages

## Next Steps
1. Implement proper Card parsing in message handlers
2. Add game state persistence
3. Implement boss mechanics
4. Add authentication/authorization
5. Add configuration management
6. Implement proper error handling and recovery 