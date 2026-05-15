package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StockDetailQueryController {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;
  private final PreviewOrderUseCase previewOrderUseCase;

  public StockDetailQueryController(
      UUID gameId,
      GetPortfolioUseCase getPortfolio,
      PreviewOrderUseCase previewOrderUseCase
  ) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
    this.previewOrderUseCase = previewOrderUseCase;
  }

  public Optional<ShareDto> getHolding(String symbol) {
    List<ShareDto> portfolio = getPortfolio
        .execute(new GetPortfolioUseCase.Request(gameId)).portfolio().shares();
    return portfolio.stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }

  public UUID gameId() {
    return gameId;
  }

  public PreviewOrderUseCase previewOrderUseCase() {
    return previewOrderUseCase;
  }
}
