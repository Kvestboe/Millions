package edu.ntnu.idatt2003.gruppe50.ui.view.navigation;

public enum PageId {
  DASHBOARD("Dashboard"),
  MARKET("Market"),
  PORTFOLIO("Portfolio"),
  TRANSACTIONS("Transactions"),
  ORDERS("Orders");

  private final String label;

  PageId(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
