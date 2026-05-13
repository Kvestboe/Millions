package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;

/**
 * Factory class for creating financial transactions.
 */
public class TransactionFactory {

  /**
   * Creates a new purchase transaction.
   *
   * @param share the share to be purchased
   * @param week the week the purchase takes place
   * @return a new {@link Transaction} representing the purchase
   */
  public Transaction createPurchase(Share share, int week) {
    return new Purchase(share, week);
  }

  /**
   * Creates a new sale transaction.
   *
   * @param share the share to be sold
   * @param week the week the sale takes place
   * @return a new {@link Transaction} representing the sale
   */
  public Transaction createSale(Share share, int week) {
    return new Sale(share, week);
  }
}
