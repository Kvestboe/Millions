package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetStockDetailUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StockDetailQueryController {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;
  private final GetStockDetailUseCase getStockDetail;
  private final PreviewOrderUseCase previewOrderUseCase;
  private final Exchange exchange;

  public StockDetailQueryController(
      UUID gameId,
      GetPortfolioUseCase getPortfolio,
      GetStockDetailUseCase getStockDetail,
      PreviewOrderUseCase previewOrderUseCase,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
    this.getStockDetail = getStockDetail;
    this.previewOrderUseCase = previewOrderUseCase;
    this.exchange = exchange;
  }

  public Optional<ShareDto> getHolding(String symbol) {
    List<ShareDto> portfolio = getPortfolio
        .execute(new GetPortfolioUseCase.Request(gameId)).portfolio().shares();
    return portfolio.stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }

  public Optional<StockDto> getStock(String symbol) {
    return getStockDetail.execute(new GetStockDetailUseCase.Request(gameId, symbol));
  }

  public void subscribe(Observer observer) {
    exchange.addObserver(observer);
  }

  public void unsubscribe(Observer observer) {
    exchange.removeObserver(observer);
  }

  public UUID gameId() {
    return gameId;
  }

  public PreviewOrderUseCase previewOrderUseCase() {
    return previewOrderUseCase;
  }
}
