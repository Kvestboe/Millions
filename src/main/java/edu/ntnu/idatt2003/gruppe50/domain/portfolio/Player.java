package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.shop.InsufficientCoinsException;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import edu.ntnu.idatt2003.gruppe50.shared.Money;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

  public static Player rehydrate(
      String name,
      BigDecimal startingMoney,
      BigDecimal money,
      int coins,
      String activeTheme,
      List<String> ownedThemes,
      Portfolio portfolio,
      TransactionArchive transactions
  ) {
    return new Player(
        name,
        startingMoney,
        money,
        coins,
        activeTheme,
        ownedThemes,
        portfolio,
        transactions
    );
  }

  private Player(
      String name,
      BigDecimal startingMoney,
      BigDecimal money,
      int coins,
      String activeTheme,
      List<String> ownedThemes,
      Portfolio portfolio,
      TransactionArchive transactions
  ) {
    this.name = name;
    this.startingMoney = Money.round(startingMoney);
    this.money = Money.round(money);
    this.portfolio = portfolio;
    this.transactionArchive = transactions;

    this.coins = Math.max(0, coins);

    this.activeTheme = activeTheme == null || activeTheme.isBlank()
        ? DEFAULT_THEME
        : activeTheme;

    this.ownedThemes.add(DEFAULT_THEME);

    if (ownedThemes != null) {
      this.ownedThemes.addAll(ownedThemes);
    }

    this.ownedThemes.add(this.activeTheme);
  }

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
    money = Money.round(money.add(amount));
  }

  /**
   * Subtracts the given amount from the player's balance.
   *
   * @param amount the amount to subtract
   * @throws IllegalArgumentException if {@code amount} is null or not positive
   */
  public void withdrawMoney(BigDecimal amount) {
    Validate.positive(amount, "Amount");
    money = Money.round(money.subtract(amount));
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
   * @return the players status title as a string
   * @throws IllegalArgumentException if {@code exchange} is null
   */
  public Status getStatus() {
    int weeks = transactionArchive.countDistinctWeeks();
    BigDecimal netWorth = getNetWorth();
    if (meets(Status.SPECULATOR, weeks, netWorth)) return Status.SPECULATOR;
    if (meets(Status.INVESTOR,  weeks, netWorth)) return Status.INVESTOR;
    return Status.NOVICE;
  }

  private boolean meets(Status s, int weeks, BigDecimal netWorth) {
    return weeks >= s.getRequiredWeeks()
        && netWorth.compareTo(targetNetWorth(s)) >= 0;
  }

  /**
   * Progress toward the next status level, in [0.0, 1.0].
   * Returns 1.0 when the player is already at the highest level.
   */
  public double getProgressToNextLevel() {
    Status current = getStatus();
    Status next = current.next();
    if (next == null) {
      return 1.0;                       // SPECULATOR – ingen neste
    }

    int weeks = transactionArchive.countDistinctWeeks();
    double weekProgress = clamp(
        (double) (weeks - current.getRequiredWeeks())
            / (next.getRequiredWeeks() - current.getRequiredWeeks()));

    double gainProgress = gainProgress(current, next);

    return Math.min(weekProgress, gainProgress);
  }

  private double gainProgress(Status current, Status next) {
    BigDecimal currentTarget = targetNetWorth(current);
    BigDecimal nextTarget    = targetNetWorth(next);
    BigDecimal span = nextTarget.subtract(currentTarget);
    if (span.signum() <= 0) {
      return 1.0;                       // vakt mot divisjon på null
    }
    double p = getNetWorth().subtract(currentTarget)
        .divide(span, 6, RoundingMode.HALF_UP)
        .doubleValue();
    return clamp(p);
  }

  /** Net worth required to hold the given status. */
  private BigDecimal targetNetWorth(Status status) {
    return startingMoney.add(startingMoney.multiply(status.getRequiredGain()));
  }

  private static double clamp(double v) {
    return Math.clamp(v, 0.0, 1.0);
  }

  /**
   * Returns the player's starting capital.
   *
   * @return the amount of start capital the player started with
   */
  public BigDecimal getStartingMoney() {
    return startingMoney;
  }

  /**
   * Returns the player's current coin balance.
   *
   * @return the number of coins the player owns
   */
  public int getCoins() {
    return coins;
  }

  /**
   * Adds the given amount of coins to the player's balance.
   *
   * @param amount the number of coins to add
   * @throws IllegalArgumentException if {@code amount} is not positive
   */
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

  /**
   * Returns the id of the currently active visual theme.
   *
   * @return the active theme id
   */
  public String getActiveTheme() {
    return activeTheme;
  }

  /**
   * Sets the currently active visual theme.
   *
   * @param activeTheme the id of the theme to activate
   * @throws IllegalArgumentException if {@code activeTheme} is null or blank
   */
  public void setActiveTheme(String activeTheme) {
    Validate.notBlank(activeTheme, "Active theme");
    this.activeTheme = activeTheme;
  }

  /**
   * Adds the given theme to the player's owned themes.
   *
   * @param themeId the id of the theme to mark as owned
   * @throws IllegalArgumentException if {@code themeId} is null or blank
   */
  public void addOwnedTheme(String themeId) {
    Validate.notBlank(themeId, "Theme id");
    ownedThemes.add(themeId);
  }

  /**
   * Checks whether the player owns the given theme.
   *
   * @param themeId the id of the theme to check
   * @return {@code true} if the player owns the theme, otherwise {@code false}
   * @throws IllegalArgumentException if {@code themeId} is null or blank
   */
  public boolean ownsTheme(String themeId) {
    Validate.notBlank(themeId, "Theme id");
    return ownedThemes.contains(themeId);
  }

  /**
   * Returns the ids of all themes owned by the player.
   *
   * @return an immutable copy of the owned theme ids
   */
  public Set<String> getOwnedThemes() {
    return Set.copyOf(ownedThemes);
  }

  /** What the player still needs for the next level; empty at top status. */
  public Optional<LevelGap> getGapToNextLevel() {
    Status next = getStatus().next();
    if (next == null) {
      return Optional.empty();
    }
    int weeksLeft = Math.max(0,
        next.getRequiredWeeks() - transactionArchive.countDistinctWeeks());
    BigDecimal moneyLeft = targetNetWorth(next)
        .subtract(getNetWorth())
        .max(BigDecimal.ZERO);
    return Optional.of(new LevelGap(weeksLeft, moneyLeft));
  }
}
