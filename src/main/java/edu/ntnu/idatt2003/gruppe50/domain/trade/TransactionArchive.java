package edu.ntnu.idatt2003.gruppe50.domain.trade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an archive that stores all registered transactions.
 *
 * <p>The archive provides methods for retrieving transactions by week
 * and by transaction type (purchase or sale), as well as utilities
 * for querying the stored transaction history.
 */
public class TransactionArchive {

  private final List<Transaction> transactions;

  /**
   * Creates a new {@code TransactionArchive} with an empty list.
   */
  public TransactionArchive() {
    this.transactions = new ArrayList<>();
  }

  /**
   * Registers a transaction in the archive.
   *
   * <p>A transaction cannot be {@code null} and duplicate transactions are not allowed.
   *
   * @param transaction the transaction to register
   * @return {@code true} if the transaction was added, {@code false} if it was already present
   * @throws IllegalArgumentException if {@code transaction} is {@code null}
   */
  public boolean add(Transaction transaction) {
    if (transaction == null) {
      throw new IllegalArgumentException("Transaction cannot be null");
    }
    if (transactions.contains(transaction)) {
      return false;
    }

    return transactions.add(transaction);
  }

  /**
   * Checks if the archive contains any transactions.
   *
   * @return {@code true} if the archive contains no transactions, {@code false} otherwise
   */
  public boolean isEmpty() {
    return transactions.isEmpty();
  }

  /**
   * Returns all transactions in the archive.
   *
   * @return an unmodifiable list of all transactions
   */
  public List<Transaction> getTransactions() {
    return List.copyOf(transactions);
  }

  /**
   * Returns all transactions that occurred in the specified week.
   *
   * @param week the week number (week 0 and higher are allowed)
   * @return an unmodifiable list of transactions for the given week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Transaction> getTransactions(int week) {
    if (week < 0) {
      throw new IllegalArgumentException("Week cannot be negative");
    }

    return transactions.stream()
        .filter(transaction -> transaction.getWeek() == week)
        .toList();
  }

  /**
   * Returns all purchase transactions that occurred in the specified week.
   *
   * @param week the week number
   * @return a list of {@link Purchase} transactions for the given week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Purchase> getPurchases(int week) {
    List<Purchase> result = new ArrayList<>();

    for (Transaction transaction : getTransactions(week)) {
      if (transaction instanceof Purchase purchase) {
        result.add(purchase);
      }
    }
    return List.copyOf(result);
  }

  /**
   * Returns all sales transactions that occurred in the specified week.
   *
   * @param week the week number
   * @return a list of {@link Sale} transactions for the given week
   * @throws IllegalArgumentException if {@code week} is negative
   */
  public List<Sale> getSales(int week) {
    List<Sale> result = new ArrayList<>();

    for (Transaction transaction : getTransactions(week)) {
      if (transaction instanceof Sale sale) {
        result.add(sale);
      }
    }
    return List.copyOf(result);
  }

  /**
   * Counts the number of distinct weeks in which at
   * least one transaction has been registered.
   *
   * @return the number of unique weeks containing transactions
   */
  public int countDistinctWeeks() {
    return (int) transactions.stream()
        .map(Transaction::getWeek)
        .distinct()
        .count();
  }

  /**
   * Returns the number of transactions in the archive.
   *
   * @return the number of transactions
   */
  public int size() {
    return transactions.size();
  }

  /**
   * Counts all purchase transactions in the archive.
   *
   * @return the number of purchase transactions
   */
  public long countPurchases() {
    return transactions.stream().filter(t -> t instanceof Purchase).count();
  }

  /**
   * Counts all sale transactions in the archive.
   *
   * @return the number of sale transactions
   */
  public long countSales() {
    return transactions.stream().filter(t -> t instanceof Sale).count();
  }

  /**
   * Calculates the total commission from all transactions.
   *
   * @return the total commission
   */
  public BigDecimal totalCommission() {
    return transactions.stream()
        .map(t -> t.getCalculator().calculateCommission())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculates the total tax from all transactions.
   *
   * @return the total tax
   */
  public BigDecimal totalTax() {
    return transactions.stream()
        .map(t -> t.getCalculator().calculateTax())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calculates realized profit or loss across all sale transactions.
   *
   * <p>The result is based on net sale proceeds minus the original purchase cost.
   *
   * @return realized profit or loss
   */
  public BigDecimal realizedProfitLoss() {
    return transactions.stream()
        .filter(t -> t instanceof Sale)
        .map(s -> s.getCalculator().calculateTotal()
            .subtract(s.getShare().getPurchasePrice().multiply(s.getShare().getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Returns the most recent transactions, newest first.
   *
   * @param limit the maximum number of transactions to return
   * @return the most recent transactions
   * @throws IllegalArgumentException if {@code limit} is negative
   */
  public List<Transaction> recent(int limit) {
    if (limit < 0) {
      throw new IllegalArgumentException("Limit cannot be negative");
    }
    int size = transactions.size();
    int start = Math.max(0, size - limit);
    List<Transaction> reversed = new ArrayList<>(transactions.subList(start, size));
    Collections.reverse(reversed);
    return List.copyOf(reversed);
  }
}
