package edu.ntnu.idatt2003.gruppe50.domain.market;

import edu.ntnu.idatt2003.gruppe50.domain.notification.Notification;
import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationLog;
import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationType;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Represents the exchange for buying and selling shares.
 *
 * <p>The exchange updates every week with new prices so the player can buy and sell unique stocks.
 */
public class Exchange extends Observable {

  private static final Logger LOG = Logger.getLogger(Exchange.class.getName());

  private final String name;
  private final Map<String, Stock> stockMap;
  private final Random random;
  private final TransactionFactory factory;
  private int week;
  private final List<LimitOrder> pendingOrders;
  private final VolatilityProfile volatility;
  private final NotificationLog notifications;

  /**
   * Recreates an exchange from saved data.
   *
   * @param name          the name of the exchange
   * @param stockMap      the saved stocks, mapped by symbol
   * @param factory       the transaction factory used by the exchange
   * @param week          the saved week number
   * @param pendingOrders the saved pending orders
   * @param volatility    the volatility settings for the exchange
   * @param notifications the notification log for the exchange
   * @return the recreated exchange
   */
  public static Exchange rehydrate(String name, Map<String, Stock> stockMap,
                                   TransactionFactory factory, int week,
                                   List<LimitOrder> pendingOrders, VolatilityProfile volatility,
                                   NotificationLog notifications) {
    return new Exchange(name, stockMap, factory, week, pendingOrders, volatility, notifications);
  }

  private Exchange(String name, Map<String, Stock> stockMap, TransactionFactory factory, int week,
                   List<LimitOrder> pendingOrders, VolatilityProfile volatility,
                   NotificationLog notifications) {
    this.name = name;
    this.stockMap = new HashMap<>(stockMap);
    this.volatility = volatility;
    this.notifications = notifications;
    this.random = new Random();
    this.factory = factory;
    this.week = week;
    this.pendingOrders = new ArrayList<>(pendingOrders);
  }

  /**
   * Creates a new exchange with a name and a list of stocks.
   *
   * @param name       the name of the exchange
   * @param stocks     the stocks in the exchange
   * @param factory    the transaction factory used by the exchange
   * @param volatility the volatility settings for the exchange
   * @throws IllegalArgumentException if any parameter is null or invalid
   */
  public Exchange(String name, List<Stock> stocks, TransactionFactory factory,
                  VolatilityProfile volatility, NotificationLog notifications) {
    Validate.notBlank(name, "Name");
    Validate.notEmpty(stocks, "Stocks");
    Validate.notNull(factory, "Factory");

    this.name = name;
    stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSymbol, v -> v));
    week = 1;
    random = new Random();
    this.factory = factory;
    this.volatility = volatility;
    this.notifications = notifications;
    pendingOrders = new ArrayList<>();
  }

  /**
   * Returns the exchanges name.
   *
   * @return name of the exchange
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current week.
   *
   * @return the week
   */
  public int getWeek() {
    return week;
  }

  /**
   * Checks if the stock is present in the exchange.
   *
   * @param symbol the stock symbol
   * @return {@code true} if the exchange contains a stock
   *     with the given symbol, {@code false} otherwise
   * @throws IllegalArgumentException if {@code symbol} is null or blank
   */
  public boolean hasStock(String symbol) {
    Validate.notBlank(symbol, "Symbol");
    return stockMap.containsKey(symbol);
  }

  /**
   * Returns stock from exchange.
   *
   * @param symbol the stock symbol
   * @return stock from exchange
   * @throws IllegalArgumentException if {@code symbol} is null or blank
   * @throws NoSuchElementException   if no stock with the given symbol exists
   */
  public Stock getStock(String symbol) {
    Validate.notBlank(symbol, "Symbol");
    if (!hasStock(symbol)) {
      throw new NoSuchElementException("No stock by that symbol");
    }
    return stockMap.get(symbol);
  }

  /**
   * Search stocks in the exchange.
   *
   * @param searchTerm name or symbol of stock
   * @return a list of stocks
   * @throws IllegalArgumentException if search term is null or blank
   */
  public List<Stock> findStocks(String searchTerm) {
    Validate.notBlank(searchTerm, "Searchterm");

    return stockMap.values().stream()
        .filter(
            stock -> stock.getSymbol()
                .toLowerCase()
                .contains(searchTerm.toLowerCase())
                || stock.getCompany().toLowerCase()
                .contains(searchTerm.toLowerCase())
        ).toList();
  }

  /**
   * Buys a quantity of a stock for the player.
   *
   * @param symbol   the stock symbol
   * @param quantity the quantity to buy
   * @param player   the player buying the stock
   * @return the purchase transaction
   * @throws IllegalArgumentException if the input is invalid
   * @throws NoSuchElementException   if no stock with the given symbol exists
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    Validate.positive(quantity, "Quantity");
    Validate.notNull(player, "Player");

    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice(), this.week);

    Transaction t = factory.createPurchase(share, this.week);
    t.commit(player);

    notifyObservers();

    return t;
  }

  /**
   * Sells a share the player holds.
   *
   * @param shareId the share to be sold
   * @param player  the player
   * @return a sale
   * @throws IllegalArgumentException if {@code share} or {@code player} is null
   */
  public Transaction sell(UUID shareId, Player player) {
    Validate.notNull(shareId, "Share id");
    Validate.notNull(player, "Player");

    Share share = player.getPortfolio().getShare(shareId);
    Transaction t = factory.createSale(share, this.week);
    t.commit(player);

    notifyObservers();

    return t;
  }

  /**
   * Sells a given quantity of a stock from the player's portfolio using FIFO
   * ordering (oldest shares first). If the requested quantity does not align
   * exactly with the player's existing share lots, the final lot is split:
   * the consumed portion is sold and the remainder is returned to the
   * portfolio as a new share with the same purchase price and week.
   *
   * <p>All resulting sale transactions share the same {@code batchId} so that
   * they can be presented as a single user action in the UI.
   *
   * @param stock    the stock to sell
   * @param quantity the total number of units to sell
   * @param player   the player selling
   * @return the list of sale transactions created, in FIFO order
   * @throws IllegalArgumentException if {@code stock} or {@code player} is null,
   *                                  or {@code quantity} is not positive
   * @throws IllegalStateException    if the player does not own enough shares of
   *                                  the given stock
   */
  public List<Transaction> sellQuantity(Stock stock, BigDecimal quantity, Player player) {
    Validate.notNull(stock, "Stock");
    Validate.positive(quantity, "Quantity");
    Validate.notNull(player, "Player");

    List<Share> lots = player.getPortfolio().getShares(stock.getSymbol()).stream()
        .sorted(Comparator.comparingInt(Share::getPurchaseWeek))
        .toList();

    BigDecimal totalOwned = lots.stream()
        .map(Share::getQuantity)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (totalOwned.compareTo(quantity) < 0) {
      throw new IllegalStateException(
          "Player does not own enough shares of " + stock.getSymbol()
              + " (owned: " + totalOwned + ", requested: " + quantity + ")");
    }

    UUID batchId = UUID.randomUUID();
    List<Transaction> sales = new ArrayList<>();
    BigDecimal remaining = quantity;

    for (Share lot : lots) {
      if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
        break;
      }

      BigDecimal lotQty = lot.getQuantity();

      if (lotQty.compareTo(remaining) <= 0) {
        Transaction sale = factory.createSale(lot, this.week, batchId);
        sale.commit(player);
        sales.add(sale);
        remaining = remaining.subtract(lotQty);
      } else {
        player.getPortfolio().removeShare(lot.getShareId());

        Share consumed = new Share(stock, remaining, lot.getPurchasePrice(), lot.getPurchaseWeek());
        Share leftover = new Share(stock, lotQty.subtract(remaining),
            lot.getPurchasePrice(), lot.getPurchaseWeek());

        player.getPortfolio().addShare(consumed);
        player.getPortfolio().addShare(leftover);

        Transaction sale = factory.createSale(consumed, this.week, batchId);
        sale.commit(player);
        sales.add(sale);
        remaining = BigDecimal.ZERO;
      }
    }

    notifyObservers();
    return sales;
  }

  /**
   * Advances a week and randomizes stock prices. Pending limit orders are
   * checked: expired orders are removed, and orders whose trigger condition
   * is met are executed.
   */
  public void advance() {
    this.week++;
    stockMap.replaceAll((_, stock) -> {
      double multiplier = computeMultiplier(stock);
      stock.addNewSalesPrice(stock.getSalesPrice().multiply(BigDecimal.valueOf(multiplier)));
      return stock;
    });
    processPendingOrders();
  }

  private double computeMultiplier(Stock stock) {
    boolean goesUp = random.nextDouble() < volatility.upChance();
    double maxMove = goesUp ? volatility.maxGain() : volatility.maxLoss();
    double magnitude = Math.pow(random.nextDouble(), 1.7) * maxMove;
    return goesUp ? 1 + magnitude : 1 - magnitude;
  }

  /**
   * Processes all pending limit orders for the current week.
   *
   * <p>For each order:
   * <ul>
   *   <li>If the order has expired, it is removed without execution.</li>
   *   <li>If the order's trigger condition is met at the current price,
   *       it is executed and removed. If execution fails (e.g. insufficient
   *       funds or shares), the failure is logged and the order is removed.</li>
   *   <li>Otherwise, the order remains pending.</li>
   * </ul>
   */
  private void processPendingOrders() {
    List<LimitOrder> toRemove = new ArrayList<>();

    for (LimitOrder order : pendingOrders) {
      if (order.isExpired(this.week)) {
        notifications.add(new Notification(
            NotificationType.ORDER_EXPIRED,
            describeExpiredOrder(order),
            this.week
        ));
        LOG.log(Level.INFO, "Order expired for {0} on {1}",
            new Object[] {order.getPlayer().getName(), order.getStock().getSymbol()});
        toRemove.add(order);
      } else {
        BigDecimal currentPrice = order.getStock().getSalesPrice();
        if (order.shouldTrigger(currentPrice)) {
          try {
            order.execute(this);
            notifications.add(new Notification(
                NotificationType.ORDER_FILLED,
                describeFilledOrder(order),
                this.week
            ));
            toRemove.add(order);
          } catch (RuntimeException e) {
            notifications.add(new Notification(
                NotificationType.ORDER_FAILED,
                describeFailedOrder(order),
                this.week
            ));
            LOG.log(Level.WARNING, "Order triggered but failed for "
                + order.getPlayer().getName() + " on " + order.getStock().getSymbol(), e);
            toRemove.add(order);
          }
        }
      }
    }
    pendingOrders.removeAll(toRemove);
  }

  /**
   * Returns the stocks with the highest percentage price change.
   *
   * @param limit how many stocks do you want in the list
   * @return list of stocks sorted by latest percent change descending
   * @throws IllegalArgumentException if {@code limit <= 0}
   */
  public List<Stock> getGainers(int limit) {
    Validate.positiveInt(limit, "Limit");
    return stockMap.values().stream()
        .sorted(
            (a, b) -> b.getLatestPriceChangePercent().compareTo(a.getLatestPriceChangePercent()))
        .limit(limit)
        .toList();
  }

  /**
   * Returns the stocks with the lowest percentage price change.
   *
   * @param limit how many stocks do you want in the list
   * @return list of stocks sorted by latest percent change ascending
   * @throws IllegalArgumentException if {@code limit <= 0}
   */
  public List<Stock> getLosers(int limit) {
    Validate.positiveInt(limit, "Limit");
    return stockMap.values().stream()
        .sorted(Comparator.comparing(Stock::getLatestPriceChangePercent))
        .limit(limit)
        .toList();
  }

  /**
   * Returns a list of all the stocks in the exchange.
   *
   * @return list with all stocks
   */
  public List<Stock> getStocks() {
    return new ArrayList<>(stockMap.values());
  }

  /**
   * Places a pending limit order on the exchange. The order will be
   * checked against the market price and executed when its trigger
   * condition is met.
   *
   * @param order the order to place
   * @throws IllegalArgumentException if {@code order} is null
   */
  public void placeOrder(LimitOrder order) {
    Validate.notNull(order, "Order");
    pendingOrders.add(order);
    notifyObservers();
  }

  /**
   * Cancels a pending limit order. If the order is not currently
   * pending, this method has no effect.
   *
   * @param order the order to cancel
   * @return true if the order was removed, false otherwise
   */
  public boolean cancelOrder(LimitOrder order) {
    boolean removed = pendingOrders.remove(order);
    if (removed) {
      notifyObservers();
    }
    return removed;
  }

  /**
   * Returns an unmodifiable view of all currently pending limit orders.
   *
   * @return the pending orders
   */
  public List<LimitOrder> getPendingOrders() {
    return Collections.unmodifiableList(pendingOrders);
  }

  /**
   * Returns the notification log for this exchange.
   *
   * @return the notification log
   */
  public NotificationLog getNotifications() {
    return notifications;
  }

  private String describeFilledOrder(LimitOrder order) {
    String verb = isBuy(order) ? "bought" : "sold";
    return order.getQuantity() + "× " + order.getStock().getSymbol()
        + " " + verb + " at " + order.getTargetPrice() + " kr";
  }

  private String describeExpiredOrder(LimitOrder order) {
    String kind = isBuy(order) ? "buy order" : "sell order";
    return order.getQuantity() + "× " + order.getStock().getSymbol()
        + " " + kind + " at " + order.getTargetPrice() + " kr";
  }

  private String describeFailedOrder(LimitOrder order) {
    String kind = isBuy(order) ? "buy" : "sell";
    return order.getQuantity() + "× " + order.getStock().getSymbol()
        + " " + kind + " failed at " + order.getTargetPrice() + " kr";
  }

  private boolean isBuy(LimitOrder order) {
    return order instanceof LimitBuyOrder;
  }
}
