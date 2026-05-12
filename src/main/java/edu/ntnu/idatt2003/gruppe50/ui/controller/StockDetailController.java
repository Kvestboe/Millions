package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class StockDetailController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final PortfolioQueryController portfolioQueryController;

  public StockDetailController(UUID gameId, BuyShareUseCase buyShare, PortfolioQueryController portfolioQueryController) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.portfolioQueryController = portfolioQueryController;
  }

  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

  public Optional<ShareData> getHolding(String symbol) {
    return portfolioQueryController.getPortfolio().shares().stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }
}
