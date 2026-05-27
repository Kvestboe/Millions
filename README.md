# Millions – Stock Market Simulator

**STUDENT NAMES:** Marius Stavrum Klepp, Kristian Vestbø
**GROUP:** 50

Millions is a local Java/JavaFX stock market simulator developed as part of **IDATT2003 Programming 2** at NTNU. The player starts with a chosen amount of capital and must reach a net worth of **1 000 000 kr** by trading in a simulated stock market — buying and selling shares, placing conditional orders, and surviving weekly hangar costs across multiple difficulty levels.

---

## Table of Contents

- [About the Game](#about-the-game)
- [Installation](#installation)
- [Build and Run](#build-and-run)
- [Gameplay](#gameplay)
- [Stock Data File Format](#stock-data-file-format)
- [Save Files](#save-files)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [License](#license)

---

## About the Game

Millions simulates a stock exchange where weekly price changes are driven by a per-stock volatility profile. Each turn the player can trade, place conditional orders, and then advance to the next week, where prices update and pending orders are evaluated. The goal is to reach **1 000 000 kr** in net worth before the player's portfolio drops below the difficulty-specific loss threshold.

**Core features**

- Start a new game with a custom name, starting capital, difficulty, and stock data
- Browse the market: search, filter, and view per-stock price history and statistics
- Buy and sell shares with commission and capital gains tax
- Transaction history with full details and filtering
- Weekly progression with top gainers/losers and net worth tracking
- Real-time status progression (Novice -> Investor -> Speculator)

**Extensions beyond the mandatory requirements**

- **Difficulty levels**: (Easy, Medium, Hard) affect market behavior, hangar cost, starting capital cap, and loss threshold
- **Conditional orders**: limit buy, limit sell, and stop-loss orders that trigger automatically during weekly advance
- **Predefined markets**: bundled stock data (S&P 500, Oslo Børs, and a funny exchange) so the player can start without uploading a CSV
- **Save and load**: with support for multiple saves and deletion of saves
- **Shop system**: convert money to coins and buy visual themes
- **Leaderboard**: ranking completed games by result and difficulty
- **Onboarding flow**: introducing core mechanics to new players
- **Visual themes**: unlocked through the shop

---

## Installation

**Prerequisites**

- **JDK 25**
- **Maven 3.9+**

**Clone the repository**

```
git clone git@github.com:mappe-2026/Millions.git
cd Millions
```

**Build the project**

```
mvn clean install
```

---

## Build and Run

| Command | Purpose |
|---|---|
| `mvn clean package` | Build a packaged jar |
| `mvn javafx:run` | Launch the JavaFX application |
| `mvn test` | Run the unit test suite |

---

## Gameplay

### Starting a new game

From the main menu, choose **New Game**. You'll be asked to:

1. Pick a player name
2. Select a difficulty
3. Set a starting capital (subject to the difficulty's cap)
4. Choose a stock market; a predefined one or your own CSV file

### Difficulty levels

| Difficulty | Market bias | Hangar cost | Max starting capital | Loss threshold |
|---|---|---|---|---|
| **Easy** | 54% up-chance, +-12% swings | 0.6% of starting capital | unlimited | 40% of starting capital |
| **Medium** | 50% up-chance, +-14% swings | 1.5% of starting capital | 25 000 kr | 50% of starting capital |
| **Hard** | 46% up-chance, +-16% swings | 2.0% of starting capital | 5 000 kr | 60% of starting capital |

If your net worth drops below the loss threshold, the game ends in a loss. Reaching 1 000 000 kr ends the game in a win.

### The market view

The market screen lists every stock on the exchange. You can:

- Search by ticker symbol or company name
- Open a stock to see price history, highest/lowest price, weekly change, and your current holding
- See the week's top gainers and losers

### Trading

- **Buying:** select a stock, enter quantity Review the preview gross value, commission, and total before confirming.
- **Selling:** open your portfolio (or a stock detail page), pick a share, and confirm. The receipt shows gross value, commission, capital gains tax (on profit only), and net payout.

### Conditional orders

In addition to immediate trades, three conditional order types can be placed:

- **Limit Buy**: buy automatically when the price drops to a target
- **Limit Sell**: sell automatically when the price rises to a target
- **Stop-Loss**: sell automatically when the price falls below a threshold

Orders are evaluated at the start of every weekly advance and either trigger, expire, or remain pending.

### Advancing the week

When you click **Advance to next week**:

1. The hangar cost is withdrawn from your cash balance
2. All stock prices update using the volatility profile
3. Pending conditional orders are evaluated
4. Net worth is recorded and observers (UI) are notified
5. The game checks win/loss conditions

### Player status

| Status | Requirement |
|---|---|
| **Novice** | Starting level |
| **Investor** | Traded >= 10 weeks and net worth has grown by >= 20% |
| **Speculator** | Traded >= 20 weeks and net worth has at least doubled |

### Shop and themes

Cash can be exchanged for coins at a (rising) rate inside the shop. Coins are used to purchase visual themes that change the look of the application.

### Leaderboard

Completed games (won or lost) are saved to a local leaderboard, ranked by a score that combines final result, weeks played, starting capital, and difficulty.

---

## Stock Data File Format

Stock data is loaded from a CSV file with the following format:

```
# Top 500 US Stocks by Market Cap
# Ticker,Name,Price
NVDA,Nvidia,191.27
AAPL,Apple Inc.,276.43
MSFT,Microsoft,404.68
```

- Lines starting with `#` are treated as comments and ignored
- Blank lines are ignored
- Each stock line follows the format: `symbol,name,price`
- Decimal separator is `.` (period)

Bundled sample files are available in `src/main/resources/data/` (`sp500.csv`, `osloBørs.csv`, `fun.csv`, and smaller test files).

---

## Save Files

Game sessions are persisted as JSON files via Jackson under the local `saves/` directory. Saves are automatic after key actions, and multiple parallel saves are supported. Saves can be loaded, continued, or deleted from the main menu.

---

## Testing

The project ships with around **450 unit tests** in JUnit Jupiter 6. `mvn test` runs the full suite.

Coverage includes:

- All 26 use cases (commands and queries)
- Domain entities and value objects: `Player`, `Portfolio`, `Exchange`, `Stock`, `Share`, `Transaction`, `TransactionArchive`, `LimitOrder` hierarchy, `Shop`, `Leaderboard`, `GameSession`
- Calculators: gross, commission, and tax for both `PurchaseCalculator` and `SaleCalculator`
- File and JSON handling via the `Parse` and DTO/mapping layer (kept I/O-free)
- In-memory repository implementations for fast, isolated tests

Tests use `@BeforeEach` for isolated state, `@Nested` to structure larger files, and `assertThrows` for negative paths. `BigDecimal` values are compared with `compareTo()` to avoid scale issues. The GUI layer is verified through manual user testing rather than unit tests.

---

## Project Structure

```
src/
├── main/
│   ├── java/edu/ntnu/idatt2003/gruppe50/
│   │   ├── App.java                  ← JavaFX entry point
│   │   ├── AppModule.java            ← manual dependency injection root
│   │   ├── AppRouter.java            ← screen navigation
│   │   ├── domain/                   ← core business logic, framework-free
│   │   │   ├── game/                 ← GameSession, Difficulty, outcomes
│   │   │   ├── leaderboard/          ← Leaderboard, entries
│   │   │   ├── market/               ← Exchange, Stock, VolatilityProfile
│   │   │   ├── portfolio/            ← Player, Portfolio, Share, Status
│   │   │   ├── repository/           ← ports (e.g. GameSessionRepository)
│   │   │   ├── shop/                 ← Shop, ShopItem, CoinExchange
│   │   │   └── trade/                ← Transaction, Purchase, Sale, orders
│   │   ├── application/              ← use cases (CQRS)
│   │   │   ├── command/              ← BuyShareUseCase, AdvanceWeekUseCase, ...
│   │   │   └── query/                ← GetMarketUseCase, GetPortfolioUseCase, ...
│   │   ├── infrastructure/           ← adapters: file I/O, persistence
│   │   │   ├── persistence/          ← JSON DTOs and GameSaveMapper
│   │   │   └── repository/           ← JsonFileGameSessionRepository, ...
│   │   ├── shared/                   ← cross-cutting utilities
│   │   │   ├── observer/             ← Observable, Observer
│   │   │   ├── Money.java, MoneyFormat.java
│   │   │   ├── Parse.java, Validate.java
│   │   ├── ui/                       ← JavaFX presentation layer
│   │   │   ├── controller/           ← MVC controllers
│   │   │   ├── mapper/               ← domain DTO → UI model mappers
│   │   │   ├── model/                ← UI-only models (TableView rows, etc.)
│   │   │   └── view/                 ← views, pages, components, navigation
│   │   └── module-info.java
│   └── resources/
│       ├── css/                      ← JavaFX stylesheets, per view
│       ├── data/sp500.csv            ← sample stock data
│       └── icons/, sounds/, ...
└── test/java/edu/ntnu/idatt2003/gruppe50/
    └── (mirrors the main package layout)
```
---

## License

This project was developed for educational purposes as part of **IDATT2003** at NTNU.
