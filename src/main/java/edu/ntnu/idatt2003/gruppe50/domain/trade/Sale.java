package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a transaction where a player sells shares.
 *
 * <p>A sale transaction adds the sale price to the player's balance
 * and removes the shares from the player's portfolio when committed.
 */
public class Sale extends Transaction {

  /**
   * Creates a new {@code Sale} transaction.
   *
   * @param share   the share being sold
   * @param week    the week the sale takes place
   * @param batchId the id grouping this transaction with others
   *                from the same user action
   * @throws IllegalArgumentException if {@code share} or {@code batchId} is
   *                                  {@code null}, or if {@code week} is not positive
   */
  public Sale(Share share, int week, UUID batchId) {
    super(share, week, new SaleCalculator(share), batchId);
  }

  /**
   * Commits this sale for the given player.
   *
   * <p>The sale adds money, removes the share from the portfolio,
   * and stores the transaction in the player's archive.
   *
   * @param player the player selling the share
   * @throws IllegalArgumentException if {@code player} is null or does not own the share
   */
  @Override
  public void commit(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (isCommitted()) {
      return;
    }
    if (!player.getPortfolio().contains(getShare().getShareId())) {
      throw new IllegalArgumentException("Cannot sell a share you don't own");
    }

    BigDecimal total = getCalculator().calculateTotal();

    player.addMoney(total);
    player.getPortfolio().removeShare(getShare().getShareId());
    player.getTransactionArchive().add(this);
    markCommitted();
  }
}
