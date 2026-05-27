package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase.Response;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Keeps market data for the UI synchronized with the current exchange state.
 */
public final class MarketQueryController implements Observer {

  private final UUID gameId;
  private final GetMarketUseCase getMarket;
  private final ObservableList<StockDto> stocks = FXCollections.observableArrayList();
  private String query = "";
  private Consumer<StockDto> onStockSelected;

  /**
   * Creates a market query controller.
   *
   * @param gameId    id of the game session
   * @param getMarket use case used to retrieve market data
   * @param exchange  observed exchange that triggers market refreshes
   */
  public MarketQueryController(
      UUID gameId,
      GetMarketUseCase getMarket,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getMarket = getMarket;
    exchange.addObserver(this);
    refresh();
  }

  /**
   * Returns the observable stock list shown in the market view.
   *
   * @return observable stock DTO list
   */
  public ObservableList<StockDto> getStocks() {
    return stocks;
  }

  /**
   * Finds a stock in the current market list by symbol.
   *
   * @param symbol stock symbol to search for
   * @return matching stock, or empty if none exists
   */
  public Optional<StockDto> findBySymbol(String symbol) {
    return stocks.stream().filter(s -> s.symbol().equals(symbol)).findFirst();
  }

  /**
   * Registers a callback for stock selection.
   *
   * @param onStockSelected callback receiving the selected stock
   */
  public void setOnStockSelected(Consumer<StockDto> onStockSelected) {
    this.onStockSelected = onStockSelected;
  }

  /**
   * Handles stock selection from the UI.
   *
   * @param stock selected stock
   */
  public void onStockSelected(StockDto stock) {
    if (onStockSelected != null) {
      onStockSelected.accept(stock);
    }
  }

  /**
   * Applies a search query and refreshes the market list.
   *
   * @param query search query
   */
  public void onSearch(String query) {
    this.query = query;
    refresh();
  }

  /**
   * Refreshes market data when the observed exchange changes.
   */
  @Override
  public void update() {
    refresh();
  }

  /**
   * Reloads market data from the application layer.
   */
  public void refresh() {
    Response response =
        getMarket.execute(new Request(gameId, query));

    stocks.setAll(response.stocks());
  }
}
