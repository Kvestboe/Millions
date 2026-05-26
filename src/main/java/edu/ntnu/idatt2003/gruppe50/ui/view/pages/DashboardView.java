package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.GoalProgressDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StatusProgressDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.WeeklyMoversDto;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.DashboardQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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
import java.util.function.Consumer;

public class DashboardView extends BorderPane implements Page {

  private final DashboardQueryController controller;
  private final Consumer<StockDto> onMoverSelected;

  public DashboardView(DashboardQueryController controller, Consumer<StockDto> onMoverSelected) {
    this.controller = controller;
    this.onMoverSelected = onMoverSelected;

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

    RowConstraints goalRow = new RowConstraints();
    goalRow.setMinHeight(140);

    RowConstraints moversRow = new RowConstraints();

    RowConstraints notifRow = new RowConstraints();
    notifRow.setMinHeight(160);

    grid.getRowConstraints().addAll(goalRow, moversRow, notifRow);

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
    VBox card = createCard("Watchlist");

    VBox content = new VBox(6);
    // (her vil watchlist-radene plasseres når vi kobler på data senere)

    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scroll.getStyleClass().add("app-scroll");

    // Nøkkelen: ScrollPane skal *ikke* dra opp rad-høyden basert på innhold
    scroll.setPrefViewportHeight(0);
    VBox.setVgrow(scroll, Priority.ALWAYS);

    card.getChildren().add(scroll);
    return card;
  }

  private VBox buildMoversCard() {
    VBox card = createCard("This week's movers");
    card.setSpacing(12);

    VBox gainersSection = new VBox(6);
    Label gainersHeader = new Label("TOP GAINERS");
    gainersHeader.getStyleClass().add("movers-section-header");
    gainersSection.getChildren().add(gainersHeader);

    VBox losersSection = new VBox(6);
    Label losersHeader = new Label("TOP LOSERS");
    losersHeader.getStyleClass().add("movers-section-header");
    losersSection.getChildren().add(losersHeader);

    card.getChildren().addAll(gainersSection, losersSection);

    Runnable apply = () -> {
      WeeklyMoversDto dto = controller.weeklyMoversProperty().get();
      if (dto == null) return;
      rebuildMoverSection(gainersSection, gainersHeader, dto.topGainers(), true);
      rebuildMoverSection(losersSection, losersHeader, dto.topLosers(), false);
    };
    apply.run();
    controller.weeklyMoversProperty().addListener((obs, old, dto) -> apply.run());

    return card;
  }

  private void rebuildMoverSection(VBox section, Label header, List<StockDto> stocks, boolean gainer) {
    section.getChildren().setAll(header);
    for (StockDto stock : stocks) {
      section.getChildren().add(buildMoverRow(stock, gainer));
    }
  }

  private HBox buildMoverRow(StockDto stock, boolean gainer) {
    Label symbol = new Label(stock.symbol());
    symbol.getStyleClass().add("mover-symbol");

    Label name = new Label(stock.company());
    name.getStyleClass().add("mover-name");

    VBox left = new VBox(2, symbol, name);

    double pct = stock.percentChange().doubleValue();
    String arrow = pct >= 0 ? "▲" : "▼";
    Label change = new Label(arrow + " " + String.format("%.1f%%", Math.abs(pct)));
    change.getStyleClass().add(gainer ? "mover-gain" : "mover-loss");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(left, spacer, change);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("mover-row");
    row.setCursor(Cursor.HAND);
    row.setOnMouseClicked(e -> onMoverSelected.accept(stock));

    return row;
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
