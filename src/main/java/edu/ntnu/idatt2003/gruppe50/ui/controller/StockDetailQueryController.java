package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetStockDetailUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionMarkersUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Provides stock detail, holding and chart marker data to stock detail views. */
public class StockDetailQueryController {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;
  private final GetStockDetailUseCase getStockDetail;
  private final GetTransactionMarkersUseCase getTransactionMarkers;
  private final PreviewOrderUseCase previewOrderUseCase;
  private final Exchange exchange;

  /**
   * Creates a stock detail query controller.
   *
   * @param gameId id of the game session
   * @param getPortfolio use case used to retrieve holdings
   * @param getStockDetail use case used to retrieve stock details
   * @param getTransactionMarkers use case used to retrieve chart markers
   * @param previewOrderUseCase use case used to preview orders
   * @param exchange exchange used for observer subscriptions
   */
  public StockDetailQueryController(
      UUID gameId,
      GetPortfolioUseCase getPortfolio,
      GetStockDetailUseCase getStockDetail,
      GetTransactionMarkersUseCase getTransactionMarkers,
      PreviewOrderUseCase previewOrderUseCase,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
    this.getStockDetail = getStockDetail;
    this.getTransactionMarkers = getTransactionMarkers;
    this.previewOrderUseCase = previewOrderUseCase;
    this.exchange = exchange;
  }

  /**
   * Finds the player's holding for a stock symbol.
   *
   * @param symbol stock symbol to look up
   * @return matching holding, or empty if the player owns none
   */
  public Optional<ShareDto> getHolding(String symbol) {
    List<ShareDto> portfolio = getPortfolio
        .execute(new GetPortfolioUseCase.Request(gameId)).portfolio().shares();
    return portfolio.stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }

  /**
   * Retrieves detailed data for a stock symbol.
   *
   * @param symbol stock symbol to retrieve
   * @return stock details, or empty if the stock does not exist
   */
  public Optional<StockDto> getStock(String symbol) {
    return getStockDetail.execute(new GetStockDetailUseCase.Request(gameId, symbol));
  }

  /**
   * Retrieves buy and sell markers for a stock chart.
   *
   * @param symbol stock symbol to retrieve markers for
   * @return transaction marker response
   */
  public GetTransactionMarkersUseCase.Response getTransactionMarkers(String symbol) {
    return getTransactionMarkers.execute(new GetTransactionMarkersUseCase.Request(gameId, symbol));
  }

  /**
   * Subscribes an observer to exchange updates.
   *
   * @param observer observer to subscribe
   */
  public void subscribe(Observer observer) {
    exchange.addObserver(observer);
  }

  /**
   * Unsubscribes an observer from exchange updates.
   *
   * @param observer observer to unsubscribe
   */
  public void unsubscribe(Observer observer) {
    exchange.removeObserver(observer);
  }

  /**
   * Returns the current game-session id.
   *
   * @return game-session id
   */
  public UUID gameId() {
    return gameId;
  }

  /**
   * Returns the order preview use case for this session.
   *
   * @return order preview use case
   */
  public PreviewOrderUseCase previewOrderUseCase() {
    return previewOrderUseCase;
  }
}
