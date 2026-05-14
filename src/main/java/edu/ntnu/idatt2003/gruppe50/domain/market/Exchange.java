package edu.ntnu.idatt2003.gruppe50.domain.market;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents the exchange for buying and selling shares.
 *
 * <p>The exchange updates every week with new prices so the player can buy and sell unique stocks.
 */
public class Exchange extends Observable {

  private final String name;
  private final Map<String, Stock> stockMap;
  private final Random random;
  private final TransactionFactory factory;
  private int week;
  private final List<LimitOrder> pendingOrders = new ArrayList<>();

  /**
   * Creates a new {@code exchange} with a name and stocks represented by symbols.
   *
   * @param name The name of the exchange
   * @param stocks The stocks in the exchange
   * @param factory the transaction factory used
   * @throws IllegalArgumentException if any parameter is null or invalid
   */
  public Exchange(String name, List<Stock> stocks, TransactionFactory factory) {
    Validate.notBlank(name, "Name");
    Validate.notEmpty(stocks, "Stocks");

    Validate.notNull(factory, "Factory");

    this.name = name;
    stockMap = stocks.stream().collect(Collectors.toMap(Stock::getSymbol, v -> v));

    week = 1;
    random = new Random();
    this.factory = factory;
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
   * @throws NoSuchElementException if no stock with the given symbol exists
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
   * Buys a share from the exchange.
   *
   * @param symbol the stock symbol
   * @param quantity the number of stocks
   * @param player the player
   * @return a purchase
   * @throws IllegalArgumentException if {@code symbol} is null or blank, {@code quantity} is null
   *     or negative, or {@code player} is null
   * @throws NoSuchElementException if no stock with the given symbol exists
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
   * @param player the player
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
   * @param stock the stock to sell
   * @param quantity the total number of units to sell
   * @param player the player selling
   * @return the list of sale transactions created, in FIFO order
   * @throws IllegalArgumentException if {@code stock} or {@code player} is null,
   *     or {@code quantity} is not positive
   * @throws IllegalStateException if the player does not own enough shares of
   *     the given stock
   */
  //LAG TESTER
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
        // Sell the entire lot
        Transaction sale = factory.createSale(lot, this.week, batchId);
        sale.commit(player);
        sales.add(sale);
        remaining = remaining.subtract(lotQty);
      } else {
        // Split the lot: sell `remaining`, return (lotQty - remaining) to portfolio
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
      BigDecimal multiplier = BigDecimal.valueOf(Math.exp((random.nextDouble() - 0.5) * 0.4));

      BigDecimal newPrice = stock.getSalesPrice().multiply(multiplier);
      stock.addNewSalesPrice(newPrice);
      return stock;
    });

    processPendingOrders();

    notifyObservers();
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
        // TODO: notify player that order expired
        System.err.println("Order expired for " + order.getPlayer().getName()
            + " on " + order.getStock().getSymbol());
        toRemove.add(order);
        continue;
      }

      BigDecimal currentPrice = order.getStock().getSalesPrice();
      if (order.shouldTrigger(currentPrice)) {
        try {
          order.execute(this);
        } catch (RuntimeException e) {
          // TODO: notify player that order triggered but could not be executed
          System.err.println("Order triggered but failed for "
              + order.getPlayer().getName() + " on " + order.getStock().getSymbol()
              + ": " + e.getMessage());
        }
        toRemove.add(order);
      }
    }

    pendingOrders.removeAll(toRemove);
  }

  /**
   * Returns a list of the stocks with the most profit.
   *
   * @param limit how many stocks do you want in the list
   * @return list of stocks with the most profitable stocks
   * @throws IllegalArgumentException if {@code limit <= 0}
   */
  public List<Stock> getGainers(int limit) {
    Validate.positiveInt(limit, "Limit");
    return stockMap.values().stream()
        .sorted((a, b) -> b.getLatestPriceChange().compareTo(a.getLatestPriceChange()))
        .limit(limit)
        .toList();
  }

  /**
   * Returns a list of the stocks with the biggest loss.
   *
   * @param limit how many stocks do you want in the list
   * @return list of stocks with the least profitable stocks
   * @throws IllegalArgumentException if {@code limit <= 0}
   */
  public List<Stock> getLosers(int limit) {
    Validate.positiveInt(limit, "Limit");
    return stockMap.values().stream()
        .sorted(Comparator.comparing(Stock::getLatestPriceChange))
        .limit(limit).toList();
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

    System.out.println("Pending orders: " + pendingOrders.size());

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
}
