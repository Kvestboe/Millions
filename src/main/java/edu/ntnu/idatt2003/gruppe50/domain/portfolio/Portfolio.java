package edu.ntnu.idatt2003.gruppe50.domain.portfolio;

import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Represents a portfolio containing shares owned by a user.
 * <br>
 * The portfolio maintains a collection of {@link Share} objects.
 */
public class Portfolio {

  private final Map<UUID, Share> shares;

  public static Portfolio rehydrate(Map<UUID, Share> shares) {
    return new Portfolio(shares);
  }

  private Portfolio(Map<UUID, Share> shares) {
    this.shares = new HashMap<>(shares);
  }

  /**
   * Creates a new {@code Portfolio} with an empty list.
   */
  public Portfolio() {
    this.shares = new HashMap<>();
  }

  /**
   * Adds a new share to the portfolio.
   *
   * @param share The new share
   * @return {@code true} if the share was added successfully,
   *     {@code false} if the share could not be added
   */
  public boolean addShare(Share share) {
    Validate.notNull(share, "Share");
    return shares.putIfAbsent(share.getShareId(), share) == null;
  }

  /**
   * Removes a share from the portfolio.
   *
   * @param shareId The share to remove
   * @return {@code true} if the share was removed successfully, {@code false} if the share could
   *     not be removed
   */
  public boolean removeShare(UUID shareId) {
    Validate.notNull(shareId, "Share id");
    return shares.remove(shareId) != null;
  }

  /**
   * Returns all shares in the portfolio.
   *
   * @return an unmodifiable list of the shares in this portfolio
   */
  public List<Share> getShares() {
    return List.copyOf(shares.values());
  }

  /**
   * Returns all shares in the portfolio with the given symbol.
   *
   * @param symbol the share symbol to filter by
   * @return a list of all shares with the given symbol, or an empty list if none are found
   * @throws IllegalArgumentException if symbol is null or blank
   */
  public List<Share> getShares(String symbol) {
    Validate.notBlank(symbol, "Symbol");
    return shares.values().stream()
        .filter(share -> share.getStock().getSymbol().equalsIgnoreCase(symbol))
        .toList();
  }

  public Share getShare(UUID shareId) {
    Validate.notNull(shareId, "Share id");
    Share share = shares.get(shareId);
    if (share == null) {
      throw new NoSuchElementException("No share by that id: " + shareId);
    }
    return share;
  }

  /**
   * Checks whether the portfolio contains at least one
   * share with the same stock as the given share.
   *
   * @param shareToCheck the share whose stock is used for the check
   * @return {@code true} if the portfolio contains at least one share
   *     with the same stock as {@code shareToCheck}, {@code false} otherwise
   */
  public boolean contains(Share shareToCheck) {
    Validate.notNull(shareToCheck, "Share to check");
    return shares.values().stream()
        .anyMatch(share -> share.getShareId().equals(shareToCheck.getShareId()));
  }

  public boolean contains(UUID shareId) {
    Validate.notNull(shareId, "Share id");
    return shares.get(shareId) != null;
  }

  /**
   * Calculates the portfolio net worth.
   *
   * <p>Calculates the amount of money the portfolio is
   * worth if the player were to sell all their stocks.
   *
   * @return the portfolio net worth as {@link BigDecimal}
   */
  public BigDecimal getNetWorth() {
    return shares.values().stream()
        .map(a -> new SaleCalculator(a).calculateTotal())
        // First parameter is start value, second parameter is the accumulative value
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
