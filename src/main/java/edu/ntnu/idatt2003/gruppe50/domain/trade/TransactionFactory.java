package edu.ntnu.idatt2003.gruppe50.domain.trade;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import java.util.UUID;

/**
 * Factory class for creating financial transactions.
 */
public class TransactionFactory {

  /**
   * Creates a new purchase transaction with a fresh batch id.
   *
   * @param share the share to be purchased
   * @param week  the week the purchase takes place
   * @return a new {@link Transaction} representing the purchase
   * @throws IllegalArgumentException if the input is invalid
   */
  public Transaction createPurchase(Share share, int week) {
    return new Purchase(share, week, UUID.randomUUID());
  }

  /**
   * Creates a new sale transaction with a fresh batch id.
   *
   * @param share the share to be sold
   * @param week  the week the sale takes place
   * @return a new {@link Transaction} representing the sale
   * @throws IllegalArgumentException if the input is invalid
   */
  public Transaction createSale(Share share, int week) {
    return new Sale(share, week, UUID.randomUUID());
  }

  /**
   * Creates a new sale transaction belonging to an existing batch.
   * Used when a single user action results in multiple sale transactions
   * (e.g. a sell crossing multiple FIFO lots).
   *
   * @param share   the share to be sold
   * @param week    the week the sale takes place
   * @param batchId the existing batch id to associate with this sale
   * @return a new {@link Transaction} representing the sale
   * @throws IllegalArgumentException if the input is invalid
   */
  public Transaction createSale(Share share, int week, UUID batchId) {
    return new Sale(share, week, batchId);
  }
}
