# Millions – Stock Market Simulator

**STUDENT NAMES:** Marius Klepp , Kristian Vestbø
**GROUP:** 50

This project was developed as part of the course IDATT2003 (Programming 2), which is part of the Bachelor's program in Computer Science at NTNU. Millions is a Java-based stock market simulator where players can buy and sell shares, track their portfolio, and compete to grow their net worth over time.

---

## Table of Contents

- [About the Game](#about-the-game)
- [Installation](#installation)
- [Build and Run](#build-and-run)
- [Usage](#usage)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [License](#license)

---

## About the Game

Millions simulates a stock exchange where the player starts with a chosen amount of capital and a set of stocks loaded from a CSV file. Each week, stock prices change randomly, and the player must make smart decisions about when to buy and sell to maximize their net worth. 

Key features include:

- Start a new game with a custom name, starting capital, and stock data from any CSV file
- Browse the stock market — search, filter, and view price history and statistics
- Buy and sell shares with real commission and tax calculations
- View all completed transactions with full details and receipts
- Advance to the next trading week, which triggers price updates across all stocks
- Track your net worth and player status (Novice, Investor, Speculator) in real time
- Sell your entire portfolio and exit the game at any time

---

## Installation

**Prerequisites:**
- Java 25
- Maven 

**Clone the repository:**

```bash
git clone git@github.com:mappe-2026/Millions.git
cd millions
```

**Build the project:**

```bash
mvn clean install
```

---

## Build and Run

**Build:**

```bash
mvn clean package
```

**Run the application:**

```bash
mvn javafx:run
```

**Run tests only:**

```bash
mvn test
```

---

## Usage

### Starting a New Game

When the application launches, you are taken to the **New Game** screen. Here you must:

1. Enter your player name
2. Enter your starting capital
3. Select a stock data file (CSV format) from your file system

The application will load all stocks from the file and bring you to the main exchange view.

### The Market View

The main screen shows all available stocks on the exchange. You can:

- Search by ticker symbol or company name
- Click a stock to view its price history, highest/lowest price, and weekly change
- See the week's top gainers and losers

### Buying and Selling

To **buy**, select a stock and enter the quantity you wish to purchase. A summary will show you the gross value, commission (0.5%), and total cost before you confirm.

To **sell**, open your portfolio, select a share, and confirm the sale. A receipt showing gross value, commission (1%), capital gains tax (30% on profit), and total payout will be displayed.

### Advancing to the Next Week

Once you are done trading, click **Advance to next week**. All stock prices update randomly. Your portfolio values and net worth update automatically.

### Player Status

Your current status is always visible in the interface:

| Status | Requirement |
|---|---|
| **Novice** | Starting level — no conditions |
| **Investor** | Traded ≥ 10 weeks and net worth increased by ≥ 20% |
| **Speculator** | Traded ≥ 20 weeks and net worth at least doubled |

### Ending the Game

To finish, use the **⋯** menu and select **Sell all & quit**. This sells your entire portfolio and takes you to the end screen, where you can see your final result before closing the application.

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

A sample stock file is included in `src/main/resources/`.

---

## Testing

Run all unit tests with Maven:

```bash
mvn test
```

Unit tests cover the core business logic of the application, including:

- `Stock` and `Share` construction and getters
- `PurchaseCalculator` and `SaleCalculator` (gross, commission, tax, total)
- `Portfolio` — adding, removing, and querying shares
- `TransactionArchive` — storing and retrieving transactions
- `Player` — balance management and net worth calculation
- `Exchange` — buying, selling, stock lookup, and price advancement

Both positive (happy path) and negative (error/edge case) tests are included. Tests follow the **Arrange–Act–Assert** pattern and use JUnit 6.

---

## Project Structure

The codebase follows a layered architecture with one package per concern:

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

## Design Patterns Used

- **MVC (Model–View–Controller):** The GUI is structured strictly according to MVC. Views display data, controllers handle user input, and the model contains all business logic.
- **Observer:** The model is observable. Views register as observers and update automatically when the model changes state (e.g., after a trade or week advance).
- **Factory:** A `TransactionFactory` is used to create `Purchase` and `Sale` instances, decoupling transaction creation from the rest of the code.
- **Strategy / Interface-based calculators:** `TransactionCalculator` is an interface implemented separately by `PurchaseCalculator` and `SaleCalculator`, making it easy to swap or extend calculation logic.

---

## License

This project is developed for educational purposes as part of IDATT2003 at NTNU.
