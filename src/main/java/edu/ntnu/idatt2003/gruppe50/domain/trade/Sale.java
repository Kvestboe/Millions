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
   * @param share the share being sold
   * @param week the week the sale takes place
   * @param batchId the id grouping this transaction with others
   *     from the same user action
   * @throws IllegalArgumentException if {@code share} or {@code batchId} is
   *     {@code null}, or if {@code week} is not positive
   */
  public Sale(Share share, int week, UUID batchId) {
    super(share, week, new SaleCalculator(share), batchId);
  }

  /**
   * Commits this sale transaction for the specified player.
   *
   * <p>The player receives money from the sale, the share is removed from the player's portfolio,
   * and the transaction is added to the player's transaction archive.
   *
   * <p>If the transaction is already committed, this method does nothing.
   *
   * @param player the player for whom the transaction is committed
   * @throws IllegalArgumentException if player is null
   * @throws IllegalArgumentException if the player does not own the share
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
