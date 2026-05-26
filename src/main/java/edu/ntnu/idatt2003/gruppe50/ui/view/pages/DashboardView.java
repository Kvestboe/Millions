package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class DashboardView extends BorderPane implements Page {

  private final GameController gameController;

  public DashboardView(GameController gameController) {
    this.gameController = gameController;

    GridPane grid = createGrid();

    grid.add(buildGoalProgressCard(),  0, 0, 2, 1);
    grid.add(buildWatchlistCard(),     0, 1, 1, 1);
    grid.add(buildMoversCard(),        1, 1, 1, 1);
    grid.add(buildNotificationsCard(), 0, 2, 2, 1);

    this.setCenter(grid);
  }

  @Override
  public Parent getView() {
    return this;
  }

  private GridPane createGrid() {
    GridPane grid = new GridPane();
    grid.getStyleClass().add("dashboard-grid");

    for (int i = 0; i < 2; i++) {
      ColumnConstraints col = new ColumnConstraints();
      col.setPercentWidth(50);
      grid.getColumnConstraints().add(col);
    }

    double[] rowHeights = {20, 55, 25};
    for (double h : rowHeights) {
      RowConstraints row = new RowConstraints();
      row.setPercentHeight(h);
      grid.getRowConstraints().add(row);
    }

    return grid;
  }

  private VBox buildGoalProgressCard() {
    return createCard("Goal progress");
  }

  private VBox buildWatchlistCard() {
    return createCard("Watchlist");
  }

  private VBox buildMoversCard() {
    return createCard("This week's movers");
  }

  private VBox buildNotificationsCard() {
    return createCard("Notifications");
  }
}
