package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a player in the game, holding information about the player's progress.
 *
 * <p>The Player holds a given amount of money and has a
 * portfolio {@link Portfolio} of shares with a record
 * of transactions {@link TransactionArchive} of transactions.
 */
public class Player {

  private final String name;
  private final BigDecimal startingMoney;
  private final Portfolio portfolio;
  private final TransactionArchive transactionArchive;
  private BigDecimal money;
  private int coins = 0;
  private static final String DEFAULT_THEME = "default";
  private String activeTheme = DEFAULT_THEME;
  private final Set<String> ownedThemes = new HashSet<>();

  /**
   * Creates a new {@code Player} with the given name and starting money of given amount.
   *
   * @param name The player's name
   * @param startingMoney The amount of money the player starts with
   * @throws IllegalArgumentException If any argument is null or invalid
   */
  public Player(String name, BigDecimal startingMoney) {
    Validate.notBlank(name, "Name");
    Validate.positive(startingMoney, "Starting money");

    this.name = name;
    this.startingMoney = startingMoney;
    this.money = startingMoney;
    this.portfolio = new Portfolio();
    this.transactionArchive = new TransactionArchive();
    ownedThemes.add(DEFAULT_THEME);
  }

  public static Player rehydrate(
      String name,
      BigDecimal startingMoney,
      BigDecimal money,
      Portfolio portfolio,
      TransactionArchive transactions
  ) {
    return new Player(name, startingMoney, money, portfolio, transactions);
  }

  private Player(
      String name,
      BigDecimal startingMoney,
      BigDecimal money,
      Portfolio portfolio,
      TransactionArchive transactions
  ) {
    this.name = name;
    this.startingMoney = startingMoney;
    this.money = money;
    this.portfolio = portfolio;
    this.transactionArchive = transactions;
    ownedThemes.add(DEFAULT_THEME);
  }

  /**
   * Returns the player's name.
   *
   * @return the player's name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the player's current balance.
   *
   * @return the players amount of money as a {@link BigDecimal}
   */
  public BigDecimal getMoney() {
    return money;
  }

  /**
   * Adds the given amount to the player's balance.
   *
   * @param amount the amount to add
   * @throws IllegalArgumentException if {@code amount} is null or not positive
   */
  public void addMoney(BigDecimal amount) {
    Validate.positive(amount, "Amount");
    money = money.add(amount);
  }

  /**
   * Subtracts the given amount from the player's balance.
   *
   * @param amount the amount to subtract
   * @throws IllegalArgumentException if {@code amount} is null or not positive
   */
  public void withdrawMoney(BigDecimal amount) {
    Validate.positive(amount, "Amount");
    money = money.subtract(amount);
  }

  /**
   * Returns the player's portfolio, containing the owned shares.
   *
   * @return the player's portfolio
   */
  public Portfolio getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the player's transaction archive, containing all previous transactions.
   *
   * @return the player's transaction archive
   */
  public TransactionArchive getTransactionArchive() {
    return transactionArchive;
  }

  /**
   * Calculates the players total amount of money.
   *
   * <p>The total amount of money is the money they have on their account
   * as well as the money would theoretically have if they sold all stocks.
   *
   * @return the players net worth as {@link BigDecimal}
   */
  public BigDecimal getNetWorth() {
    return money.add(portfolio.getNetWorth());
  }

  /**
   * Returns the players status.
   *
   * <p>Calculates the players status based on their net worth
   * and for how many weeks they have played.
   *
   * @param exchange the exchange the user is trading their stocks in
   * @return the players status title as a string
   * @throws IllegalArgumentException if {@code exchange} is null
   */
  public String getStatus(Exchange exchange) {
    Validate.notNull(exchange, "Exchange");
    int week = exchange.getWeek();
    if (money.compareTo(startingMoney.multiply(BigDecimal.TWO)) > 0 && week >= 20) {
      return "Speculator";
    } else if (money.compareTo(startingMoney.multiply(BigDecimal.valueOf(1.2))) > 0 && week >= 10) {
      return "Investor";
    } else {
      return "Novice";
    }
  }

  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  public int getCoins() {
    return coins;
  }

  public void addCoins(int amount) {
    Validate.positiveInt(amount, "Coins");
    coins += amount;
  }

  public void spendCoins(int amount) throws InsufficientCoinsException {
    Validate.positiveInt(amount, "Amount");
    if (coins < amount) {
      throw new InsufficientCoinsException(
          "Not enough coins. Required: " + amount + ", available: " + coins);
    }
    coins -= amount;
  }

  public String getActiveTheme() {
    return activeTheme;
  }

  public void setActiveTheme(String activeTheme) {
    Validate.notBlank(activeTheme, "Active theme");
    this.activeTheme = activeTheme;
  }

  public void addOwnedTheme(String themeId) {
    Validate.notBlank(themeId, "Theme id");
    ownedThemes.add(themeId);
  }

  public boolean ownsTheme(String themeId) {
    Validate.notBlank(themeId, "Theme id");
    return ownedThemes.contains(themeId);
  }

  public Set<String> getOwnedThemes() {
    return Set.copyOf(ownedThemes);
  }
}
