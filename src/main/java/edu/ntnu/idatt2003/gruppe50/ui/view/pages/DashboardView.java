package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.DashboardQueryController;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.CardFactory.createCard;

public class DashboardView extends BorderPane implements Page {

  private final DashboardQueryController controller;

  public DashboardView(DashboardQueryController controller) {
    this.controller = controller;


    GridPane grid = createGrid();

    grid.add(buildMarketEventCard(), 0, 0, 3, 1);
    grid.add(buildMarketEventCard(), 3, 0, 2, 1);
    this.setCenter(grid);
  }

  @Override
  public Parent getView() {
    return this;
  }


  private GridPane createGrid() {
    GridPane grid = new GridPane();

    // Define the different card widths
    for (int i = 0; i < 5; i++) {
      ColumnConstraints col = new ColumnConstraints();
      col.setPercentWidth(20);
      grid.getColumnConstraints().add(col);
    }

    return grid;
  }

  private VBox buildMarketEventCard() {
    return createCard("Market events");
  }

  private VBox buildTradingLogCard() {
    return createCard("Trading log");
  }

  private VBox buildTimelineCard() {
    return createCard("Trading log");
  }

  private VBox buildStatusCard() {
    return createCard("Status");
  }

  private VBox buildWatchlistCard() {
    return createCard("Watchlist");
  }

  private VBox buildMoversCard() {
    return createCard("This week's movers");
  }
}
