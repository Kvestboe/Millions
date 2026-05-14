package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetMarketUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.ShareDto;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class MarketQueryController {

  private final UUID gameId;
  private final GetMarketUseCase getMarket;
  private final ObservableList<ShareDto> stocks = FXCollections.observableArrayList();

  public MarketQueryController(UUID gameId, GetMarketUseCase getMarket) {
    this.gameId = gameId;
    this.getMarket = getMarket;
  }




}
