package edu.ntnu.idatt2003.gruppe50.domain.market;

import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a stock traded on the market.<br>
 * A stock has a symbol, company and a history of sale prices.
 */
public class Stock {

  private static final int PRICE_SCALE = 4;

  private final String symbol;
  private final String company;
  private final List<BigDecimal> prices;

  private Stock(String symbol, String company, List<BigDecimal> prices) {
    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>(prices);
  }

  public static Stock rehydrate(String symbol, String company, List<BigDecimal> prices) {
    return new Stock(symbol, company, prices);
  }

  /**
   * Creates a new {@code Stock} with an initial sales price.
   *
   * @param symbol the stock symbol (e.g. AAPL)
   * @param company the company name (e.g. Apple)
   * @param salesPrice the initial sales price
   * @throws IllegalArgumentException if any argument is invalid
   */
  public Stock(String symbol, String company, BigDecimal salesPrice) {
    Validate.notBlank(symbol, "Symbol");
    Validate.notBlank(company, "Company");
    Validate.positive(salesPrice, "Sales price");

    this.symbol = symbol;
    this.company = company;
    this.prices = new ArrayList<>();
    this.prices.add(salesPrice);
  }

  /**
   * Returns the symbol of the company for the stock.
   *
   * @return symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Returns the company name of the stock.
   *
   * @return company
   */
  public String getCompany() {
    return company;
  }

  /**
   * Returns the latest sales price of the stock.
   *
   * @return the most recent sales price
   */
  public BigDecimal getSalesPrice() {
    return prices.getLast();
  }

  /**
   * Adds a new sales price to the stock.
   *
   * @param salesPrice the new sales price
   * @throws IllegalArgumentException if the price is null, zero or negative
   */
  public void addNewSalesPrice(BigDecimal salesPrice) {
    Validate.positive(salesPrice, "Sales price");
    prices.add(salesPrice.setScale(PRICE_SCALE, RoundingMode.HALF_UP));
  }

  /**
   * Returns the stock price history.
   *
   * @return list of stock prices as BigDecimal
   */
  public List<BigDecimal> getHistoricalPrices() {
    return List.copyOf(prices);
  }

  /**
   * Calculates and returns the stocks highest recorded value.
   *
   * @return the highest value as a {@link BigDecimal}
   */
  public BigDecimal getHighestPrice() {
    return prices.stream().max(BigDecimal::compareTo).orElseThrow();
  }

  /**
   * Calculates and returns the stocks lowest recorded value.
   *
   * @return the lowest value as a {@link BigDecimal}
   */
  public BigDecimal getLowestPrice() {
    return prices.stream()
        .min(BigDecimal::compareTo)
        .orElseThrow();
  }

  /**
   * Calculates the price change.
   *
   * <p>The price change is the difference between the latest price minus the previous one.
   *
   * @return the price change as {@link BigDecimal}
   */
  public BigDecimal getLatestPriceChange() {
    if (prices.size() < 2) {
      return BigDecimal.ZERO;
    }
    int lastIndex = prices.size() - 1;
    return prices.getLast().subtract(prices.get(lastIndex - 1));
  }

  /**
   * Calculates the percentage price change between the two most recent prices.
   * Returns zero if fewer than two prices have been recorded.
   *
   * @return the percentage price change as a {@link BigDecimal} with two decimal places
   */
  public BigDecimal getLatestPriceChangePercent() {
    if (prices.size() < 2) {
      return BigDecimal.ZERO;
    }
    BigDecimal previousPrice = prices.get(prices.size() - 2);
    BigDecimal change = getLatestPriceChange();
    BigDecimal percent = change.divide(previousPrice, 2, RoundingMode.HALF_UP);
    return percent.multiply(new BigDecimal("100"));
  }
}
