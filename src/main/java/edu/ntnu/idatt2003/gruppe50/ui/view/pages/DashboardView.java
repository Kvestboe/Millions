package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

public class DashboardView extends BorderPane implements Page {

  public DashboardView() {



    GridPane grid = createGrid();

    grid.add(buildTradingLogCard(), 0, 0, 3, 1);
    grid.add(buildMarketEventCard(), 3, 0, 2, 1);
    grid.add(buildTimelineCard(), 0, 1, 2, 1);
    grid.add(buildStatusCard(), 3, 1, 2, 1);
    grid.add(buildWatchlistCard(), 0, 2, 2, 1);
    grid.add(buildMoversCard(), 2, 2, 3, 1);
    grid.add(buildAdvanceButton(), 2, 1, 1, 1);
    this.setCenter(grid);
  }

  @Override
  public Parent getView() {
    return this;
  }


  private GridPane createGrid() {
    GridPane grid = new GridPane();
    grid.getStyleClass().add("grid");

    // Define the different card widths
    for (int i = 0; i < 5; i++) {
      ColumnConstraints col = new ColumnConstraints();
      col.setPercentWidth(20);
      grid.getColumnConstraints().add(col);
    }

    for (int i = 0; i < 3; i++) {
      RowConstraints row = new RowConstraints();
      row.setPercentHeight(i == 1 ? 25 : 37.5);
      grid.getRowConstraints().add(row);
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
    return createCard("Timeline");
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

  private Button buildAdvanceButton() {
    Button btn = new Button("Advance\nWeek");
    btn.getStyleClass().add("advance-button");

    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setMaxHeight(Double.MAX_VALUE);
//    btn.minWidthProperty().bind(btn.heightProperty());
//    btn.maxWidthProperty().bind(btn.heightProperty());
    return btn;
  }
}
