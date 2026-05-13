package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.TransactionCalculator;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;

/**
 * Represents a financial transaction in the game.
 *
 * <p>A transaction is associated with a specific share and week.
 * Subclasses define how the transaction is committed.
 * A transaction can only be committed once.
 */
public abstract class Transaction {

  private final Share share;
  private final int week;
  private final TransactionCalculator calculator;
  private boolean committed;

  /**
   * Creates a new {@code Transaction}.
   *
   * @param share the share involved in the transaction
   * @param week the week number when the transaction occurred
   * @param calculator the calculator used in the transaction
   * @throws IllegalArgumentException if {@code share} or {@code calculator} is
   *     {@code null}, or if {@code week} is not positive
   */
  protected Transaction(Share share, int week, TransactionCalculator calculator) {
    Validate.notNull(share, "Share");
    Validate.positiveInt(week, "Week");
    Validate.notNull(calculator, "Calculator");
    this.share = share;
    this.week = week;
    this.calculator = calculator;
  }

  /**
   * Returns the share associated with this transaction.
   *
   * @return the share involved in the transaction
   */
  public Share getShare() {
    return share;
  }

  /**
   * Returns the week associated with this transaction.
   *
   * @return the week involved in the transaction
   */
  public int getWeek() {
    return week;
  }

  /**
   * Returns the calculator associated with this transaction.
   *
   * @return the calculator involved in the transaction.
   */
  public TransactionCalculator getCalculator() {
    return calculator;
  }

  /**
   * Returns whether this transaction has been committed.
   *
   * @return {@code true} if the transaction has been committed, {@code false} otherwise
   */
  public boolean isCommitted() {
    return committed;
  }

  /**
   * Commits (executes) this transaction for the given player.
   *
   * <p>Subclasses must implement this method and apply the
   * transaction's effects to the given player.
   *
   * @param player the player for whom the transaction is committed
   */
  public abstract void commit(Player player);

  /**
   * Sets committed to true
   */
  protected void markCommitted() {
    this.committed = true;
  }
}
