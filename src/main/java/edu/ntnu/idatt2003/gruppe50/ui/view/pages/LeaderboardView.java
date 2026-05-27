package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.LeaderboardEntry;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Leaderboard page showing the top players for each difficulty.
 *
 * <p>Provides difficulty tabs (Easy/Medium/Hard) and a table ranking
 * winners by score, with medal styling for the top three ranks.
 */
public class LeaderboardView extends StackPane {

  private final Leaderboard leaderboard;
  private final Runnable onBack;
  private final TableView<LeaderboardEntry> table = new TableView<>();
  private Difficulty active = Difficulty.EASY;

  /**
   * Constructs the leaderboard view.
   *
   * @param leaderboard the leaderboard providing top entries per difficulty
   * @param onBack      action triggered when the player clicks "Back"
   */
  public LeaderboardView(Leaderboard leaderboard, Runnable onBack) {
    this.leaderboard = leaderboard;
    this.onBack = onBack;
    build();
  }

  private void build() {
    VBox card = buildCard();
    StackPane.setAlignment(card, Pos.CENTER);
    getChildren().add(card);
  }

  private VBox buildCard() {
    VBox card = new VBox(20);
    card.getStyleClass().addAll("load-game-card", "leaderboard-view");
    card.setPrefWidth(760);
    card.setMaxWidth(760);
    card.setMaxHeight(Double.MAX_VALUE);
    card.setAlignment(Pos.TOP_LEFT);

    buildTable();
    VBox.setVgrow(table, Priority.ALWAYS);
    table.setMaxHeight(Double.MAX_VALUE);

    card.getChildren().addAll(
        buildHeader(),
        buildTabs(),
        table
    );
    return card;
  }

  private StackPane buildHeader() {
    final Button backBtn = ButtonFactory.styled("Back", "system-button", onBack);

    Label title = new Label("🏆 LEADERBOARD");
    title.getStyleClass().add("load-game-title");

    Label subtitle = new Label("TOP PLAYERS PER DIFFICULTY");
    subtitle.getStyleClass().add("load-game-subtitle");

    VBox titleBox = new VBox(2, title, subtitle);
    titleBox.setAlignment(Pos.CENTER);

    StackPane header = new StackPane(titleBox, backBtn);
    StackPane.setAlignment(titleBox, Pos.CENTER);
    StackPane.setAlignment(backBtn, Pos.CENTER_LEFT);
    return header;
  }

  private HBox buildTabs() {
    ToggleGroup group = new ToggleGroup();
    HBox row = new HBox(8);
    row.setAlignment(Pos.CENTER);
    for (Difficulty d : Difficulty.values()) {
      ToggleButton btn = new ToggleButton(d.name());
      btn.setToggleGroup(group);
      btn.getStyleClass().addAll("btn-secondary", "difficulty-tab");
      btn.setUserData(d);
      btn.setSelected(d == active);
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setOnAction(e -> {
        if (!btn.isSelected()) {
          btn.setSelected(true);
          return;
        }
        active = (Difficulty) btn.getUserData();
        refresh();
      });
      HBox.setHgrow(btn, Priority.ALWAYS);
      row.getChildren().add(btn);
    }
    return row;
  }

  private void buildTable() {
    TableColumn<LeaderboardEntry, String> rankCol = new TableColumn<>("#");
    rankCol.setCellValueFactory(c ->
        new SimpleObjectProperty<>("#" + (table.getItems().indexOf(c.getValue()) + 1)));
    rankCol.setCellFactory(col -> new TableCell<>() {
      @Override
      protected void updateItem(String rankText, boolean empty) {
        super.updateItem(rankText, empty);
        getStyleClass().removeAll(
            "leaderboard-rank-first",
            "leaderboard-rank-second",
            "leaderboard-rank-third"
        );
        if (empty || rankText == null) {
          setText(null);
          return;
        }
        int rank = getIndex() + 1;
        setText(rankText);
        setAlignment(Pos.CENTER);
        switch (rank) {
          case 1 -> getStyleClass().add("leaderboard-rank-first");
          case 2 -> getStyleClass().add("leaderboard-rank-second");
          case 3 -> getStyleClass().add("leaderboard-rank-third");
          default -> {
          }
        }
      }
    });

    TableColumn<LeaderboardEntry, String> nameCol = new TableColumn<>("Player");
    nameCol.setCellValueFactory(c ->
        new SimpleObjectProperty<>(c.getValue().playerName()));

    TableColumn<LeaderboardEntry, Number> scoreCol = new TableColumn<>();
    scoreCol.setGraphic(buildScoreHeader());
    scoreCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().score()));
    scoreCol.setCellFactory(col -> new TableCell<>() {
      @Override
      protected void updateItem(Number v, boolean empty) {
        super.updateItem(v, empty);
        setText((empty || v == null) ? null : String.format("%,.1f", v.doubleValue()));
        setAlignment(Pos.CENTER);
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
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    table.setPlaceholder(new Label("No winners on this difficulty yet."));
    refresh();
  }

  private HBox buildScoreHeader() {
    final Label label = new Label("Score");
    Label q = new Label("?");
    q.getStyleClass().add("info-icon");
    Tooltip tooltip = new Tooltip(
        "Score = (1 000 000 / starting capital)^1.5 × (100 / √weeks)\n"
            + "Lower starting capital matters most, while fewer weeks gives a smaller bonus.");
    tooltip.getStyleClass().add("score-tooltip");
    q.setPickOnBounds(true);
    HBox h = new HBox(4, label, q);
    h.setAlignment(Pos.CENTER);
    h.setOnMouseEntered(_ -> showScoreTooltip(h, tooltip));
    h.setOnMouseExited(_ -> tooltip.hide());
    return h;
  }

  private void showScoreTooltip(HBox owner, Tooltip tooltip) {
    var bounds = owner.localToScreen(owner.getBoundsInLocal());
    if (bounds != null && !tooltip.isShowing()) {
      tooltip.show(owner, bounds.getMinX(), bounds.getMaxY() + 8);
    }
  }

  private void refresh() {
    table.setItems(FXCollections.observableArrayList(leaderboard.top(active)));
  }
}
