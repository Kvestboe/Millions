package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;

import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.ShareDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class StockDetailController {

  private final UUID gameId;
  private final BuyShareUseCase buyShare;
  private final SellShareUseCase sellShare;
  private final PortfolioQueryController portfolioQueryController;
  private final GetPortfolioUseCase getPortfolio;

  public StockDetailController(UUID gameId, BuyShareUseCase buyShare, SellShareUseCase sellShare, PortfolioQueryController portfolioQueryController, GetPortfolioUseCase getPortfolio) {
    this.gameId = gameId;
    this.buyShare = buyShare;
    this.sellShare = sellShare;
    this.portfolioQueryController = portfolioQueryController;
    this.getPortfolio = getPortfolio;
  }

  public void buy(String symbol, BigDecimal quantity) {
    buyShare.execute(new BuyShareUseCase.Request(gameId, symbol, quantity));
  }

  /** Selger ett (det første) lot brukeren eier av gitt symbol. */
  public Optional<UUID> sellOneOf(String symbol) {
    GetPortfolioUseCase.Response res =
        getPortfolio.execute(new GetPortfolioUseCase.Request(gameId));

    Optional<UUID> shareId = res.shares().stream()
        .filter(s -> s.symbol().equalsIgnoreCase(symbol))
        .map(ShareDto::shareId)
        .findFirst();

    shareId.ifPresent(id ->
        sellShare.execute(new SellShareUseCase.Request(gameId, id))
    );
    return shareId;
  }


  public Optional<ShareData> getHolding(String symbol) {
    return portfolioQueryController.getPortfolio().shares().stream()
        .filter(s -> s.symbol().equals(symbol))
        .findFirst();
  }
}
