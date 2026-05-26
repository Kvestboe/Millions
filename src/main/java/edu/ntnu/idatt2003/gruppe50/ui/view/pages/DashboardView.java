package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.GoalProgressDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StatusProgressDto;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.DashboardQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class DashboardView extends BorderPane implements Page {

  private final DashboardQueryController controller;

  public DashboardView(DashboardQueryController controller) {
    this.controller = controller;

    GridPane grid = createGrid();

    addToGrid(grid, buildGoalProgressCard(),  0, 0, 2, 1);
    addToGrid(grid, buildWatchlistCard(),     0, 1, 1, 1);
    addToGrid(grid, buildMoversCard(),        1, 1, 1, 1);
    addToGrid(grid, buildNotificationsCard(), 0, 2, 2, 1);

    ScrollPane scrollPane = new ScrollPane(grid);
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.getStyleClass().add("app-scroll");

    this.setCenter(scrollPane);
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
    VBox card = createCard("Goal progress");
    card.setSpacing(10);

    card.getChildren().addAll(
        buildGoalSection(),
        buildDivider(),
        buildStatusSection()
    );

    return card;
  }

  private VBox buildGoalSection() {
    Label percentLabel = new Label();
    percentLabel.getStyleClass().add("goal-progress-percent");
    HBox header = new HBox(percentLabel);
    header.setAlignment(Pos.CENTER_RIGHT);

    ProgressBar bar = new ProgressBar(0);
    bar.getStyleClass().add("goal-progress-bar");

    Label currentLabel = new Label();
    currentLabel.getStyleClass().add("goal-progress-current");

    Label goalLabel = new Label();
    goalLabel.getStyleClass().add("goal-progress-goal");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    HBox footer = new HBox(currentLabel, spacer, goalLabel);

    Runnable apply = () -> {
      GoalProgressDto dto = controller.goalProgressProperty().get();
      if (dto == null) return;
      percentLabel.setText(String.format("%.1f%%", dto.progress() * 100));
      bar.setProgress(dto.progress());
      currentLabel.setText(MoneyFormat.formatCurrency(dto.currentNetWorth()));
      goalLabel.setText("Goal: " + MoneyFormat.formatCurrency(dto.goal()));
    };
    apply.run();
    controller.goalProgressProperty().addListener((obs, old, dto) -> apply.run());

    return new VBox(8, header, bar, footer);
  }

  private Separator buildDivider() {
    Separator sep = new Separator();
    sep.getStyleClass().add("goal-progress-divider");
    return sep;
  }

  private VBox buildStatusSection() {
    Label transitionLabel = new Label();
    transitionLabel.getStyleClass().add("status-progress-transition");

    Label percentLabel = new Label();
    percentLabel.getStyleClass().add("status-progress-percent");

    Region headerSpacer = new Region();
    HBox.setHgrow(headerSpacer, Priority.ALWAYS);
    HBox header = new HBox(transitionLabel, headerSpacer, percentLabel);
    header.setAlignment(Pos.CENTER_LEFT);

    ProgressBar bar = new ProgressBar(0);
    bar.getStyleClass().add("level-progress");

    Label gapLabel = new Label();
    gapLabel.getStyleClass().add("status-progress-gap");

    VBox section = new VBox(8, header, bar, gapLabel);

    Runnable apply = () -> {
      StatusProgressDto dto = controller.statusProgressProperty().get();
      if (dto == null) return;

      if (dto.atMaxLevel()) {
        transitionLabel.setText("✦ Max status reached: " + dto.currentStatus() + " ✦");
        percentLabel.setText("");
        bar.setVisible(false);
        bar.setManaged(false);
        gapLabel.setVisible(false);
        gapLabel.setManaged(false);
      } else {
        transitionLabel.setText(dto.currentStatus() + " → " + dto.nextStatus());
        percentLabel.setText(String.format("%.1f%%", dto.progress() * 100));
        bar.setVisible(true);
        bar.setManaged(true);
        bar.setProgress(dto.progress());

        bar.getStyleClass().removeIf(c ->
            c.equals("level-progress-investor") || c.equals("level-progress-speculator"));
        String variant = "level-progress-" + dto.currentStatus().toLowerCase();
        if (variant.equals("level-progress-investor") || variant.equals("level-progress-speculator")) {
          bar.getStyleClass().add(variant);
        }

        gapLabel.setText(dto.weeksRemaining() + " weeks of trading left  | "
            + MoneyFormat.formatCurrency(dto.moneyRemaining()) + " left");
        gapLabel.setVisible(true);
        gapLabel.setManaged(true);
      }
    };
    apply.run();
    controller.statusProgressProperty().addListener((obs, old, dto) -> apply.run());

    return section;
  }

  private VBox buildWatchlistCard() {
    return createCard("Watchlist");
  }

  private VBox buildMoversCard() {
    VBox card = createCard("This week's movers");
    card.setSpacing(12);

    card.getChildren().addAll(
        buildMoversSection("TOP GAINERS", List.of(
            new MoverRow("GME", "GameStop", 14.2, true),
            new MoverRow("PLTR", "Palantir", 9.7, true),
            new MoverRow("META", "Meta Platforms", 6.3, true)
        )),
        buildMoversSection("TOP LOSERS", List.of(
            new MoverRow("INTC", "Intel", 8.1, false),
            new MoverRow("BA", "Boeing", 6.3, false),
            new MoverRow("F", "Ford Motor", 4.8, false)
        ))
    );

    return card;
  }

  private record MoverRow(String symbol, String name, double pct, boolean gainer) {}

  private VBox buildMoversSection(String title, List<MoverRow> rows) {
    Label header = new Label(title);
    header.getStyleClass().add("movers-section-header");

    VBox section = new VBox(6, header);
    for (MoverRow r : rows) {
      section.getChildren().add(buildMoverRow(r));
    }
    return section;
  }

  private HBox buildMoverRow(MoverRow r) {
    Label symbol = new Label(r.symbol());
    symbol.getStyleClass().add("mover-symbol");

    Label name = new Label(r.name());
    name.getStyleClass().add("mover-name");

    VBox left = new VBox(2, symbol, name);

    Label change = new Label(
        (r.gainer() ? "▲ " : "▼ ") + String.format("%.1f%%", r.pct())
    );
    change.getStyleClass().add(r.gainer() ? "mover-gain" : "mover-loss");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(left, spacer, change);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private VBox buildNotificationsCard() {
    return createCard("Notifications");
  }

  private void addToGrid(GridPane grid, Node card, int col, int row, int colSpan, int rowSpan) {
    GridPane.setHgrow(card, Priority.ALWAYS);
    GridPane.setVgrow(card, Priority.ALWAYS);
    GridPane.setFillWidth(card, true);
    GridPane.setFillHeight(card, true);
    grid.add(card, col, row, colSpan, rowSpan);
  }
}
