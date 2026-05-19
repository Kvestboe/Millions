package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PortfolioDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.UUID;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class PortfolioQueryController implements Observer {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;
  private final SimpleObjectProperty<PortfolioDto> portfolio = new SimpleObjectProperty<>();
  private final ObservableList<ShareDto> shares = FXCollections.observableArrayList();

  public PortfolioQueryController(
      UUID gameId,
      GetPortfolioUseCase getPortfolio,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getPortfolio = getPortfolio;
    exchange.addObserver(this);
    refresh();
  }

  public SimpleObjectProperty<PortfolioDto> getPortfolio() {
    return portfolio;
  }

  public ObservableList<ShareDto> getShares() {
    return shares;
  }

  @Override
  public void update() {
    refresh();
  }

  private void refresh() {
    PortfolioDto portfolioDto = getPortfolio.execute(new Request(gameId)).portfolio();
    portfolio.set(portfolioDto);
    shares.setAll(portfolioDto.shares());
  }
}
