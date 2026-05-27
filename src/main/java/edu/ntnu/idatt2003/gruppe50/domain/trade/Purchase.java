package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a transaction where a player buys shares.
 *
 * <p>A buy transaction deducts the purchase cost from the player's balance and adds the shares to
 * the player's portfolio when committed.
 */
public class Purchase extends Transaction {

  /**
   * Creates a new {@code Purchase} transaction.
   *
   * @param share   the share being purchased
   * @param week    the week the purchase takes place
   * @param batchId the id grouping this transaction with others
   *                from the same user action
   * @throws IllegalArgumentException if {@code share} or {@code batchId} is
   *                                  {@code null}, or if {@code week} is not positive
   */
  public Purchase(Share share, int week, UUID batchId) {
    super(share, week, new PurchaseCalculator(share), batchId);
  }

  /**
   * Commits this purchase for the given player.
   *
   * <p>The purchase withdraws money, adds the share to the portfolio,
   * and stores the transaction in the player's archive.
   *
   * @param player the player making the purchase
   * @throws IllegalArgumentException if {@code player} is null or has too little money
   */
  @Override
  public void commit(Player player) {
    Validate.notNull(player, "Player");
    if (isCommitted()) {
      return;
    }

    BigDecimal total = getCalculator().calculateTotal();

    if (player.getMoney().compareTo(total) < 0) {
      throw new IllegalArgumentException("Player does not have enough money");
    }

    player.withdrawMoney(total);
    player.getPortfolio().addShare(getShare());
    player.getTransactionArchive().add(this);
    markCommitted();
  }
}
