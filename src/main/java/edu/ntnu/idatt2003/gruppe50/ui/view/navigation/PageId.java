package edu.ntnu.idatt2003.gruppe50.ui.view.navigation;

/**
 * Identifiers for the main pages available in the game navigation.
 */
public enum PageId {
  /**
   * Dashboard page.
   */
  DASHBOARD("Dashboard"),

  /**
   * Market overview page.
   */
  MARKET("Market"),

  /**
   * Portfolio page.
   */
  PORTFOLIO("Portfolio"),

  /**
   * Transaction history page.
   */
  TRANSACTIONS("Transactions"),

  /**
   * Pending orders page.
   */
  ORDERS("Orders"),

  /**
   * Shop page.
   */
  SHOP("Shop");

  private final String label;

  PageId(String label) {
    this.label = label;
  }

  /**
   * Returns the display label for this page id.
   *
   * @return page label
   */
  public String getLabel() {
    return label;
  }
}
