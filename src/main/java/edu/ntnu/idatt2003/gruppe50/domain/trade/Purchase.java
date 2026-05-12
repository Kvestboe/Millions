package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;

/**
 * Represents a transaction where a player buys shares.
 * <p>
 * A buy transaction deducts the purchase cost from the player's balance
 * and adds the shares to the player's portfolio when committed.
 * </p>
 */
public class Purchase extends Transaction {

  public Purchase(Share share, int week) {
    super(share, week, new PurchaseCalculator(share));
  }

  /**
   * Commits this purchase transaction for the specified player.
   * <p>
   * The player uses money for the purchase, the share is added to the player's portfolio,
   * and the transaction is added to the player's transaction archive.
   * <p>
   * If the transaction is already committed, this method does nothing.
   *
   * @param player the player for whom the transaction is committed
   * @throws IllegalArgumentException if {@code player} is null or does not have enough money
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
