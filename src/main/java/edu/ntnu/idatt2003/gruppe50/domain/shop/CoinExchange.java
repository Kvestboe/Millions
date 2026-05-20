package edu.ntnu.idatt2003.gruppe50.domain.shop;

import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the exchange rate between in-game money and shop coins.
 *
 * <p>The exchange stores a history of coin prices, where each price represents how much
 * money one coin costs. The price scales with the player's starting capital and changes
 * slightly over time.
 */
public class CoinExchange {

  private static final BigDecimal BASE_CAPITAL = new BigDecimal("10000");
  private static final BigDecimal BASE_PRICE_PER_COIN = new BigDecimal("100");
  private static final BigDecimal MIN_MULTIPLIER = new BigDecimal("0.5");
  private static final BigDecimal MAX_MULTIPLIER = new BigDecimal("2.5");

  private final List<BigDecimal> priceHistory = new ArrayList<>();
  private final Random random = new Random();

  private final BigDecimal minPrice;
  private final BigDecimal maxPrice;

  /**
   * Creates a coin exchange with a starting price based on the player's starting capital.
   *
   * @param startingCapital the player's starting capital
   * @throws IllegalArgumentException if {@code startingCapital} is {@code null} or not positive
   */
  public CoinExchange(BigDecimal startingCapital) {
    Validate.positive(startingCapital, "Starting capital");

    BigDecimal basePrice = Money.round(BASE_PRICE_PER_COIN.multiply(
        startingCapital.divide(BASE_CAPITAL, 4, RoundingMode.HALF_UP)
    ));

    this.minPrice = Money.round(basePrice.multiply(MIN_MULTIPLIER));
    this.maxPrice = Money.round(basePrice.multiply(MAX_MULTIPLIER));

    priceHistory.add(basePrice);
  }

  /**
   * Returns the current price for buying one coin.
   *
   * @return the current price per coin
   */
  public BigDecimal getCurrentPricePerCoin() {
    return priceHistory.getLast();
  }

  /**
   * Returns a copy of the coin price history.
   *
   * @return a copy of all historical coin prices
   */
  public List<BigDecimal> getPriceHistory() {
    return new ArrayList<>(priceHistory);
  }

  /**
   * Calculates how much money it costs to buy the given amount of coins.
   *
   * @param coins the number of coins to buy
   * @return the total cost
   * @throws IllegalArgumentException if {@code coins} is not positive
   */
  public BigDecimal calculateCost(int coins) {
    Validate.positiveInt(coins, "Coins");
    return Money.round(getCurrentPricePerCoin().multiply(BigDecimal.valueOf(coins)));
  }

  /**
   * Advances the coin exchange by one step and adds a new price to the price history.
   *
   * <p>The price changes randomly between -10% and +10%, while staying within the
   * configured minimum and maximum price.
   */
  public void advanceShop() {
    BigDecimal currentPrice = getCurrentPricePerCoin();

    double change = -0.10 + random.nextDouble() * 0.20;
    BigDecimal multiplier = BigDecimal.ONE.add(BigDecimal.valueOf(change));

    BigDecimal newPrice = currentPrice.multiply(multiplier);
    priceHistory.add(Money.round(clamp(newPrice)));
  }

  private BigDecimal clamp(BigDecimal price) {
    if (price.compareTo(minPrice) < 0) {
      return minPrice;
    }

    if (price.compareTo(maxPrice) > 0) {
      return maxPrice;
    }

    return price;
  }
}