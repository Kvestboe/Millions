package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.LoadGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.StatCardFactory;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Page where the player can browse and load existing save files.
 *
 * <p>Shows a table of available saves, a detail panel with stats for the
 * selected save, and load/delete buttons. Finished games can be deleted
 * but cannot be continued.
 */
public class LoadGameView extends StackPane {

  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final LoadGameController controller;
  private final Runnable onBack;

  /**
   * Constructs the load game view.
   *
   * @param controller the controller managing save selection, loading, and deletion
   * @param onBack action triggered when the player clicks "Back"
   */
  public LoadGameView(LoadGameController controller, Runnable onBack) {
    this.controller = controller;
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
    card.getStyleClass().add("load-game-card");
    card.setPrefWidth(760);
    card.setMaxWidth(760);
    card.setMaxHeight(Double.MAX_VALUE);
    card.setAlignment(Pos.TOP_LEFT);

    TableView<SaveSummaryDto> table = createSaveTable();
    VBox.setVgrow(table, Priority.ALWAYS);

    card.getChildren().addAll(
        buildHeader(),
        table,
        createDetailPanel(),
        createButtons()
    );
    return card;
  }

  private StackPane buildHeader() {
    Button backBtn = ButtonFactory.styled("Back", "system-button", onBack);

    Label title = new Label("LOAD GAME");
    title.getStyleClass().add("load-game-title");

    Label subtitle = new Label("SELECT A SAVE FILE");
    subtitle.getStyleClass().add("load-game-subtitle");

    VBox titleBox = new VBox(2, title, subtitle);
    titleBox.setAlignment(Pos.CENTER);

    StackPane header = new StackPane(titleBox, backBtn);
    StackPane.setAlignment(titleBox, Pos.CENTER);
    StackPane.setAlignment(backBtn, Pos.CENTER_LEFT);
    return header;
  }

  private TableView<SaveSummaryDto> createSaveTable() {
    TableView<SaveSummaryDto> table = createTable(List.of(
        ColumnPresets.text("Player", SaveSummaryDto::playerName),
        ColumnPresets.text("Status", SaveSummaryDto::status),
        ColumnPresets.text("State", s -> s.isFinished() ? "Finished" : "Active"),
        ColumnPresets.text("Week", s -> "Week " + s.week()),
        ColumnPresets.currency("Net worth", SaveSummaryDto::netWorth),
        ColumnPresets.text("Last played", s -> s.lastPlayed().format(DATE_FORMAT))
    ));
    table.setItems(controller.getSaves());
    table.setRowFactory(_ -> {
      TableRow<SaveSummaryDto> row = new TableRow<>();
      row.setOnMousePressed(_ -> {
        if (!row.isEmpty()) controller.select(row.getItem());
      });
      return row;
    });
    return table;
  }

  private VBox createDetailPanel() {
    SimpleObjectProperty<SaveSummaryDto> selected = controller.getSelected();

    Label nameVal = new Label("-");
    Label statusVal = new Label("-");
    Label stateVal = new Label("-");
    Label weekVal = new Label("-");
    Label netWorthVal = new Label("-");
    Label lastPlayedVal = new Label("-");
    Label hint = new Label("");
    hint.getStyleClass().add("load-game-hint");
    hint.setMaxWidth(Double.MAX_VALUE);
    hint.setAlignment(Pos.CENTER);

    selected.addListener((_, _, s) -> {
      if (s == null) {
        nameVal.setText("-");
        statusVal.setText("-");
        stateVal.setText("-");
        weekVal.setText("-");
        netWorthVal.setText("-");
        lastPlayedVal.setText("-");
        hint.setText("");
      } else {
        nameVal.setText(s.playerName());
        statusVal.setText(s.status());
        stateVal.setText(s.isFinished() ? "Finished" : "Active");
        weekVal.setText("Week " + s.week());
        netWorthVal.setText(s.netWorth().setScale(0, RoundingMode.HALF_UP).toString() + " kr");
        lastPlayedVal.setText(s.lastPlayed().format(DATE_FORMAT));

        if (s.isFinished()) {
          hint.setText("This game is finished. You can delete it, but it cannot be continued.");
        } else {
          hint.setText("");
        }
      }
    });

    HBox stats = new HBox(12,
        statCard("PLAYER", nameVal),
        statCard("STATUS", statusVal),
        statCard("STATE", stateVal),
        statCard("WEEK", weekVal),
        statCard("NET WORTH", netWorthVal),
        statCard("LAST PLAYED", lastPlayedVal)
    );
    stats.setAlignment(Pos.CENTER_LEFT);

    VBox panel = new VBox(8, stats, hint);
    panel.setAlignment(Pos.CENTER_LEFT);
    return panel;
  }

  private VBox statCard(String label, Label valueLabel) {
    VBox card = StatCardFactory.tile(label, valueLabel);
    HBox.setHgrow(card, Priority.ALWAYS);
    return card;
  }

  private HBox createButtons() {
    Button load = ButtonFactory.styled("Load game", "btn-accent", controller::load);
    load.setMaxWidth(Double.MAX_VALUE);
    load.disableProperty().bind(Bindings.createBooleanBinding(
        () -> controller.getSelected().get() == null
            || controller.getSelected().get().isFinished(),
        controller.getSelected()
    ));

    Button delete = ButtonFactory.styled("Delete save", "system-button-danger", controller::delete);
    delete.setMaxWidth(Double.MAX_VALUE);
    delete.disableProperty().bind(controller.getSelected().isNull());

    HBox.setHgrow(load, Priority.ALWAYS);
    HBox.setHgrow(delete, Priority.ALWAYS);

    HBox buttons = new HBox(12, load, delete);
    buttons.setAlignment(Pos.CENTER_LEFT);
    return buttons;
  }
}
