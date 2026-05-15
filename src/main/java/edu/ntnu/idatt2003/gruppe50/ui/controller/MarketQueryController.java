package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase.Response;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class MarketQueryController implements Observer {

  private final UUID gameId;
  private final GetMarketUseCase getMarket;
  private final ObservableList<StockDto> stocks = FXCollections.observableArrayList();
  private String query = "";

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

  public ObservableList<StockDto> getStocks() {
    return stocks;
  }

  public void onSearch(String query) {
    this.query = query;
    refresh();
  }

  @Override
  public void update() {
    refresh();
  }

  public void refresh() {
    Response response =
        getMarket.execute(new Request(gameId, query));

    stocks.setAll(response.stocks());
  }
}
