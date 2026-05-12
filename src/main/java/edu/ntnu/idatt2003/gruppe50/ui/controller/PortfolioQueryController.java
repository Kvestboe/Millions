package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;

import java.util.List;
import java.util.UUID;

public final class PortfolioQueryController extends Observable implements Observer {
  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;

  public PortfolioQueryController(UUID gameId, GetPortfolioUseCase getPortfolio) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
  }

  @Override
  public void update() {
    notifyObservers();
  }

  public PortfolioData getPortfolio() {
    GetPortfolioUseCase.Response response = getPortfolio.execute(
        new GetPortfolioUseCase.Request(gameId)
    );

    // Although the shares are in the response already, it is more according to clean architecture
    // to make clean new list of shares since it should be independent of application
    List<ShareData> shares = response.shares().stream()
        .map(s -> new ShareData(
            s.shareId(),
            s.symbol(),
            s.stock(),
            s.quantity(),
            s.purchasePrice(),
            s.currentPrice(),
            s.currentShareValue()
        )).toList();

    return new PortfolioData(response.cash(), response.portfolioValue(), response.netWorth(), shares, List.of());
  }
}
