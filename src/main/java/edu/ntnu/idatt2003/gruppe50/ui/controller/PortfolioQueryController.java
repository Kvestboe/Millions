package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observable;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.mapper.ShareDataMapper;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import java.util.List;
import java.util.UUID;

public final class PortfolioQueryController extends Observable implements Observer {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;

  public PortfolioQueryController(
      UUID gameId,
      GetPortfolioUseCase getPortfolio,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
    exchange.addObserver(this);
  }

  public PortfolioData getPortfolio() {
    GetPortfolioUseCase.Response response =
        getPortfolio.execute(new GetPortfolioUseCase.Request(gameId));

    // Although the shares are in the response already, it is more according to clean architecture
    // to make clean new list of shares since it should be independent of application
    List<ShareData> shares = response.shares().stream()
        .map(ShareDataMapper::mapShare).toList();

    return new PortfolioData(
        response.cash(),
        response.portfolioValue(),
        response.netWorth(),
        shares,
        response.netWorthHistory()
    );
  }

  @Override
  public void update() {
    notifyObservers();
  }
}
