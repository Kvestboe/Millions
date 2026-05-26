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

/** Keeps portfolio data synchronized with the exchange state. */
public final class PortfolioQueryController implements Observer {

  private final UUID gameId;
  private final GetPortfolioUseCase getPortfolio;
  private final SimpleObjectProperty<PortfolioDto> portfolio = new SimpleObjectProperty<>();
  private final ObservableList<ShareDto> shares = FXCollections.observableArrayList();

  /**
   * Creates a portfolio query controller.
   *
   * @param gameId id of the game session
   * @param getPortfolio use case used to retrieve portfolio data
   * @param exchange observed exchange that triggers portfolio refreshes
   */
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

  /**
   * Returns the observable portfolio property.
   *
   * @return portfolio DTO property
   */
  public SimpleObjectProperty<PortfolioDto> getPortfolio() {
    return portfolio;
  }

  /**
   * Returns the observable list of current share holdings.
   *
   * @return observable share DTO list
   */
  public ObservableList<ShareDto> getShares() {
    return shares;
  }

  /** Refreshes portfolio data when the observed exchange changes. */
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
