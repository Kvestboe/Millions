package edu.ntnu.idatt2003.gruppe50.domain.game;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.notification.Notification;
import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationType;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status;
import edu.ntnu.idatt2003.gruppe50.domain.trade.InsufficientFundsException;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitSellOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.StopLossOrder;
import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root for a single game run.
 *
 * <p>A game session owns the player and exchange state,
 * and enforces lifecycle rules through {@link GameSessionState}.
 */
public final class GameSession {

  private final UUID gameId;
  private final Player player;
  private final Exchange exchange;
  private final LocalDateTime runStartedAt;
  private final Difficulty difficulty;
  private GameSessionState state;
  private LocalDateTime lastPlayed;
  private List<BigDecimal> netWorthHistory;

  public static final BigDecimal WIN_THRESHOLD = new BigDecimal("1000000");

  private GameSession(
      UUID gameId,
      Player player,
      Exchange exchange,
      Difficulty difficulty,
      GameSessionState state,
      LocalDateTime runStartedAt,
      LocalDateTime lastPlayed
  ) {
    this.gameId = gameId;
    this.player = player;
    this.exchange = exchange;
    this.difficulty = difficulty;
    this.state = state;
    this.runStartedAt = runStartedAt;
    this.lastPlayed = lastPlayed;
    this.netWorthHistory = new ArrayList<>(List.of(player.getNetWorth()));
  }

  private GameSession(
      UUID gameId,
      Player player,
      Exchange exchange,
      GameSessionState state,
      LocalDateTime runStartedAt,
      LocalDateTime lastPlayed,
      Difficulty difficulty,
      List<BigDecimal> netWorthHistory
  ) {
    this(gameId, player, exchange, difficulty, state, runStartedAt, lastPlayed);
    this.netWorthHistory = new ArrayList<>(netWorthHistory.size());
    for (BigDecimal value : netWorthHistory) {
      this.netWorthHistory.add(Money.round(value));
    }
  }

  /**
   * Creates a new active game session with generated id and current dates.
   *
   * @param player player participating in the session
   * @param exchange exchange used for trading in the session
   * @param difficulty difficulty used for the session
   * @return newly created active session
   * @throws IllegalArgumentException if {@code player} or {@code exchange} is null
   */
  public static GameSession createNew(Player player, Exchange exchange, Difficulty difficulty) {
    Validate.notNull(player, "Player");
    Validate.notNull(exchange, "Exchange");

    return new GameSession(
        UUID.randomUUID(),
        player,
        exchange,
        difficulty,
        GameSessionState.ACTIVE,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  /**
   * Recreates a game session from already saved data.
   *
   * @param gameId saved session id
   * @param player saved player state
   * @param exchange saved exchange state
   * @param difficulty saved difficulty
   * @param state saved session state
   * @param runStartedAt date the run started
   * @param lastPlayed date the session was last opened
   * @param netWorthHistory saved net worth history
   * @return rehydrated session
   * @throws IllegalArgumentException if any argument is null
   */
  public static GameSession rehydrate(
      UUID gameId,
      Player player,
      Exchange exchange,
      Difficulty difficulty,
      GameSessionState state,
      LocalDateTime runStartedAt,
      LocalDateTime lastPlayed,
      List<BigDecimal> netWorthHistory
  ) {
    Validate.notNull(gameId, "Game id");
    Validate.notNull(player, "Player");
    Validate.notNull(exchange, "Exchange");
    Validate.notNull(state, "Game state");
    Validate.notNull(runStartedAt, "Run started at date");
    Validate.notNull(lastPlayed, "Last played date");
    Validate.notNull(netWorthHistory, "Net worth history");
    return new GameSession(gameId, player, exchange, state, runStartedAt, lastPlayed, difficulty, netWorthHistory);
  }

  /**
   * Marks the session as opened today.
   */
  public void markOpened() {
    lastPlayed = LocalDateTime.now();
  }

  /**
   * Buys shares through the exchange for this session's player.
   *
   * @param symbol stock symbol to buy
   * @param quantity quantity to buy
   * @throws GameSessionFinishedException if the session is finished
   */
  public Transaction buy(String symbol, BigDecimal quantity) {
    ensureActive();
    return exchange.buy(symbol, quantity, player);
  }

  /**
   * Places a buy limit order for this session's player.
   *
   * @param symbol stock symbol to buy
   * @param quantity quantity to buy
   * @param targetPrice highest price the player is willing to pay
   * @param duration number of weeks the order should stay active
   */
  public LimitBuyOrder  placeBuyLimitOrder(
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {
    Validate.notBlank(symbol, "Symbol");
    Validate.positive(quantity, "Quantity");
    Validate.positive(targetPrice, "Target price");
    Validate.positiveInt(duration, "Duration");

    Stock stock = exchange.getStock(symbol);
    int currentWeek = exchange.getWeek();
    int expiryWeek = currentWeek + duration;

    LimitBuyOrder order = new LimitBuyOrder(
        stock,
        player,
        targetPrice,
        quantity,
        currentWeek,
        expiryWeek
    );
    exchange.placeOrder(order);
    return order;
  }

  /**
   * Sells a quantity of a stock through the exchange for this session's player.
   *
   * @param symbol stock symbol to sell
   * @param quantity quantity to sell
   * @throws GameSessionFinishedException if the session is finished
   */
  public List<Transaction> sell(String symbol, BigDecimal quantity) {
    if (state == GameSessionState.FINISHED) {
      throw new GameSessionFinishedException();
    }

    Validate.notBlank(symbol, "Symbol");
    Validate.positive(quantity, "Quantity");

    Stock stock = exchange.getStock(symbol);

    return exchange.sellQuantity(stock, quantity, player);
  }

  /**
   * Places a sell limit order for this session's player.
   *
   * @param symbol stock symbol to sell
   * @param quantity quantity to sell
   * @param targetPrice lowest price the player is willing to sell for
   * @param duration number of weeks the order should stay active
   */
  public LimitSellOrder  placeSellLimitOrder(
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {
    Validate.notBlank(symbol, "Symbol");
    Validate.positive(quantity, "Quantity");
    Validate.positive(targetPrice, "Target price");
    Validate.positiveInt(duration, "Duration");

    Stock stock = exchange.getStock(symbol);
    int currentWeek = exchange.getWeek();
    int expiryWeek = currentWeek + duration;

    LimitSellOrder order = new LimitSellOrder(
        stock,
        player,
        targetPrice,
        quantity,
        currentWeek,
        expiryWeek
    );

    exchange.placeOrder(order);
    return order;
  }

  /**
   * Places a stop loss order for this session's player.
   *
   * @param symbol stock symbol to sell
   * @param quantity quantity to sell
   * @param targetPrice price that triggers the sale
   * @param duration number of weeks the order should stay active
   */
  public StopLossOrder  placeStopLossOrder(
      String symbol,
      BigDecimal quantity,
      BigDecimal targetPrice,
      int duration
  ) {
    Validate.notBlank(symbol, "Symbol");
    Validate.positive(quantity, "Quantity");
    Validate.positive(targetPrice, "Target price");
    Validate.positiveInt(duration, "Duration");

    Stock stock = exchange.getStock(symbol);
    int currentWeek = exchange.getWeek();
    int expiryWeek = currentWeek + duration;

    StopLossOrder order = new StopLossOrder(
        stock,
        player,
        targetPrice,
        quantity,
        currentWeek,
        expiryWeek
    );
    exchange.placeOrder(order);
    return order;
  }

  /**
   * Advances the exchange one week.
   *
   * @throws GameSessionFinishedException if the session is finished
   * @throws InsufficientFundsException if the player cannot afford the hangar cost
   */
  public void advanceWeek() {
    ensureActive();

    BigDecimal hangarCost = getUpcomingHangarCost();
    if (player.getMoney().compareTo(hangarCost) < 0) {
      throw new InsufficientFundsException(
          "Not enough cash to pay hangar rent of " + hangarCost);
    }

    Status statusBefore = player.getStatus();
    player.withdrawMoney(hangarCost);
    exchange.advance();
    Status statusAfter = player.getStatus();

    if (statusAfter.ordinal() > statusBefore.ordinal()) {
      exchange.getNotifications().add(new Notification(
          NotificationType.LEVEL_UP,
          "You reached " + statusAfter.displayName() + " level",
          exchange.getWeek()
      ));
    }

    netWorthHistory.add(player.getNetWorth());
    exchange.notifyObservers();
    if (evaluateOutcome() != GameOutcome.ONGOING) {
      finish();
    }
  }

  /**
   * Marks the session as finished.
   */
  public void finish() {
    state = GameSessionState.FINISHED;
  }

  /**
   * Evaluates whether the game is ongoing, won, or lost.
   *
   * @return the current game outcome
   */
  public GameOutcome evaluateOutcome() {
    BigDecimal netWorth = player.getNetWorth();
    BigDecimal threshold = player.getStartingMoney()
        .multiply(BigDecimal.valueOf(difficulty.getGameOverThreshold()))
        .setScale(2, RoundingMode.HALF_UP);

    if (netWorth.compareTo(WIN_THRESHOLD) >= 0) {
      return GameOutcome.WON;
    }
    if (netWorth.compareTo(threshold) < 0) {
      return GameOutcome.LOST;
    }
    return GameOutcome.ONGOING;
  }

  /**
   * Returns current session state.
   *
   * @return current state
   */
  public GameSessionState getState() {
    return state;
  }

  /**
   * Returns unique session id.
   *
   * @return session id
   */
  public UUID getGameId() {
    return gameId;
  }

  /**
   * Returns player bound to this session.
   *
   * @return session player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns exchange bound to this session.
   *
   * @return session exchange
   */
  public Exchange getExchange() {
    return exchange;
  }

  /**
   * Returns date the session run started.
   *
   * @return run start date
   */
  public LocalDateTime getRunStartedAt() {
    return runStartedAt;
  }

  /**
   * Returns date and time the session was last opened.
   *
   * @return last played date-time
   */
  public LocalDateTime getLastPlayed() {
    return lastPlayed;
  }

  /**
   * Returns the saved net worth history for the session.
   *
   * @return an unmodifiable copy of the net worth history
   */
  public List<BigDecimal> getNetWorthHistory() {
    return List.copyOf(netWorthHistory);
  }

  private void ensureActive() {
    if (state == GameSessionState.FINISHED) {
      throw new GameSessionFinishedException();
    }
  }

  /**
   * Returns the pending orders in the session.
   *
   * @return pending limit orders
   */
  public List<LimitOrder> getPendingOrders() {
    return exchange.getPendingOrders();
  }

  /**
   * Returns the difficulty used in the session.
   *
   * @return session difficulty
   */
  public Difficulty getDifficulty() {
    return difficulty;
  }

  /**
   * Returns the hangar cost the player must pay on the next week advancement.
   *
   * @return the hangar cost for next week
   */
  public BigDecimal getUpcomingHangarCost() {
    return player.getStartingMoney()
        .multiply(BigDecimal.valueOf(difficulty.getHangarCostRate()))
        .setScale(2, RoundingMode.HALF_UP);
  }
}
