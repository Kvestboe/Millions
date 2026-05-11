package edu.ntnu.idatt2003.gruppe50.domain.market;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents the exchange for buying and selling shares.
 * <p>
 * The exchange updates every week with new
 * prices so the player can buy and sell unique stocks.
 * </p>
 */
public class Exchange extends Observable {
  private final String name;
  private int week;
  private final Map<String, Stock> stockMap;
  private final Random random;
  private final TransactionFactory factory;

  /**
   * Creates a new {@code exchange} with a name and stocks represented by symbols.
   *
   * @param name    The name of the exchange
   * @param stocks  The stocks in the exchange
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
   * @return {@code true} if the exchange contains a stock with the given symbol,
   *{@code false} otherwise
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
   * Buys a share from the exchange.
   *
   * @param symbol   the stock symbol
   * @param quantity the number of stocks
   * @param player   the player
   * @return a purchase
   * @throws IllegalArgumentException if {@code symbol} is null or blank, {@code quantity} is null
   *                                  or negative, or {@code player} is null
   * @throws NoSuchElementException   if no stock with the given symbol exists
   */
  public Transaction buy(String symbol, BigDecimal quantity, Player player) {
    Validate.positive(quantity, "Quantity");
    Validate.notNull(player, "Player");

    Stock stock = getStock(symbol);
    Share share = new Share(stock, quantity, stock.getSalesPrice());

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
   * Advances a week and randomizes stock prices.
   */
  public void advance() {
    this.week++;

    stockMap.replaceAll((_, stock) -> {
      double change = -0.2 + random.nextDouble() * 0.4;
      BigDecimal multiplier = BigDecimal.valueOf(1 + change);

      BigDecimal newPrice = stock.getSalesPrice().multiply(multiplier);
      stock.addNewSalesPrice(newPrice);
      return stock;
    });
    notifyObservers();
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
        .sorted(
            (a, b) -> b.getLatestPriceChange()
                .compareTo(a.getLatestPriceChange())
        ).limit(limit).toList();
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

}