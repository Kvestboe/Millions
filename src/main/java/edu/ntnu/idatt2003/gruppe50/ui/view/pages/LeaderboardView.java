package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.LeaderboardEntry;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

public class LeaderboardView extends VBox {

  private final Leaderboard leaderboard;
  private final TableView<LeaderboardEntry> table = new TableView<>();
  private Difficulty active = Difficulty.EASY;

  public LeaderboardView(Leaderboard leaderboard, Runnable onBack) {
    this.leaderboard = leaderboard;
    setSpacing(15);
    setAlignment(Pos.TOP_CENTER);
    getStyleClass().add("leaderboard-view");

    Label title = new Label("🏆 Leaderboard");
    title.getStyleClass().add("header-title");

    HBox tabs = buildTabs();
    buildTable();

    Button back = new Button("Back");
    back.setOnAction(e -> onBack.run());

    getChildren().addAll(title, tabs, table, back);
    refresh();
  }

  private HBox buildTabs() {
    ToggleGroup group = new ToggleGroup();
    HBox row = new HBox(8);
    row.setAlignment(Pos.CENTER);
    for (Difficulty d : Difficulty.values()) {
      ToggleButton btn = new ToggleButton(d.name());
      btn.setToggleGroup(group);
      btn.getStyleClass().add("difficulty-tab");
      btn.setUserData(d);
      btn.setSelected(d == active);
      btn.setOnAction(e -> {
        if (!btn.isSelected()) { btn.setSelected(true); return; }
        active = (Difficulty) btn.getUserData();
        refresh();
      });
      row.getChildren().add(btn);
    }
    return row;
  }

  private void buildTable() {
    TableColumn<LeaderboardEntry, String> rankCol = new TableColumn<>("#");
    rankCol.setCellFactory(col -> new TableCell<>() {
      @Override protected void updateItem(String unused, boolean empty) {
        super.updateItem(unused, empty);
        if (empty || getIndex() < 0) { setText(null); return; }
        int rank = getIndex() + 1;
        setText(switch (rank) {
          case 1 -> "🥇";
          case 2 -> "🥈";
          case 3 -> "🥉";
          default -> "#" + rank;
        });
      }
    });

    TableColumn<LeaderboardEntry, String> nameCol = new TableColumn<>("Player");
    nameCol.setCellValueFactory(c ->
        new SimpleObjectProperty<>(c.getValue().playerName()));

    TableColumn<LeaderboardEntry, Number> scoreCol = new TableColumn<>();
    scoreCol.setGraphic(buildScoreHeader());
    scoreCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().score()));
    scoreCol.setCellFactory(col -> new TableCell<>() {
      @Override protected void updateItem(Number v, boolean empty) {
        super.updateItem(v, empty);
        setText((empty || v == null) ? null : String.format("%,.1f", v.doubleValue()));
      }
    });

    TableColumn<LeaderboardEntry, Number> weeksCol = new TableColumn<>("Weeks");
    weeksCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().weeksPlayed()));

    TableColumn<LeaderboardEntry, String> capCol = new TableColumn<>("Start capital");
    capCol.setCellValueFactory(c ->
        new SimpleObjectProperty<>(MoneyFormat.formatCurrency(c.getValue().startingCapital())));

    TableColumn<LeaderboardEntry, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(c ->
        new SimpleObjectProperty<>(c.getValue().completedDate()
            .format(DateTimeFormatter.ISO_LOCAL_DATE)));

    table.getColumns().addAll(rankCol, nameCol, scoreCol, weeksCol, capCol, dateCol);
    table.setPlaceholder(new Label("No winners on this difficulty yet."));
    table.setMaxWidth(720);
  }

  private HBox buildScoreHeader() {
    Label label = new Label("Score");
    Label q = new Label(" ?");
    q.getStyleClass().add("info-icon");
    Tooltip.install(q, new Tooltip(
        "Score = (1 000 000 ÷ starting capital)^1.5 ÷ weeks × 1000\n"
            + "Lower starting capital weighs more than fewer weeks."));
    HBox h = new HBox(2, label, q);
    h.setAlignment(Pos.CENTER_LEFT);
    return h;
  }

  private void refresh() {
    table.setItems(FXCollections.observableArrayList(leaderboard.top(active)));
  }
}