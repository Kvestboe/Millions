package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controller for the market screen.
 * Handles retrieval of stocks from the exchange and user interactions
 * such as searching and navigating to stock detail.
 */
public class MarketController extends Observable implements Observer {
  private final Exchange exchange;
  private final Player player;
  private final Consumer<Stock> onStockSelectedCallback;

  /**
   * Creates a new MarketController.
   *
   * @param exchange the exchange to retrieve stocks from
   * @param player the current player
   */
  public MarketController(Exchange exchange, Player player, Consumer<Stock> onStockSelectedCallback) {
    this.exchange = exchange;
    this.player = player;
    this.onStockSelectedCallback = onStockSelectedCallback;
    exchange.addObserver(this);
  }

  /**
   * Searches for stocks matching the given query.
   * Matches on both symbol and company name.
   *
   * @param query the search term
   * @return a list of matching {@link Stock} objects
   */
  public List<Stock> onSearch(String query) {
    return exchange.findStocks(query);
  }

  /**
   * Returns all stocks listed on the exchange.
   *
   * @return a list of all {@link Stock} objects
   */
  public List<Stock> getStocks() {
    return new ArrayList<>(exchange.getStocks());
  }

  /**
   * Called when the user selects a stock in the market table.
   * Navigates to the stock detail screen.
   *
   * @param stock the selected {@link Stock}
   */
  public void onStockSelected(Stock stock) {
    onStockSelectedCallback.accept(stock);
  }

  @Override
  public void update() {
    notifyObservers();
  }
}
