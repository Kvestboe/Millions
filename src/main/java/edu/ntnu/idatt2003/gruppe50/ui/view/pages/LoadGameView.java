package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.LoadGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import java.math.RoundingMode;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoadGameView extends StackPane {

  private final LoadGameController controller;
  private final Runnable onBack;

  public LoadGameView(LoadGameController controller, Runnable onBack) {
    this.controller = controller;
    this.onBack = onBack;
    build();
  }

  private void build() {
    getStylesheets().add(
        getClass().getResource("/css/views/loadGame.css").toExternalForm()
    );

    VBox card = buildCard();
    StackPane.setAlignment(card, Pos.CENTER);
    getChildren().add(card);
  }

  private VBox buildCard() {
    VBox card = new VBox(20);
    card.getStyleClass().add("load-game-card");
    card.setPrefWidth(760);
    card.setMaxWidth(760);
    card.setAlignment(Pos.TOP_LEFT);

    card.getChildren().addAll(
        buildHeader(),
        createSaveTable(),
        createDetailPanel(),
        createButtons()
    );
    return card;
  }

  private StackPane buildHeader() {
    Button backBtn = new Button("Back");
    backBtn.getStyleClass().add("system-button");
    backBtn.setOnAction(_ -> onBack.run());

    Label title = new Label("LOAD GAME");
    title.getStyleClass().add("load-game-title");

    Label subtitle = new Label("SELECT A SAVE FILE");
    subtitle.getStyleClass().add("load-game-subtitle");

    VBox titleBox = new VBox(2, title, subtitle);
    titleBox.setAlignment(Pos.CENTER);

    // StackPane lets the title be truly centred while the back button
    // sits independently on the left edge
    StackPane header = new StackPane(titleBox, backBtn);
    StackPane.setAlignment(titleBox, Pos.CENTER);
    StackPane.setAlignment(backBtn, Pos.CENTER_LEFT);
    return header;
  }

  private TableView<SaveSummaryDto> createSaveTable() {
    TableView<SaveSummaryDto> table = createTable(List.of(
        ColumnPresets.text("Player", SaveSummaryDto::playerName),
        ColumnPresets.text("Status", SaveSummaryDto::status),
        ColumnPresets.text("Week", s -> "Week " + s.week()),
        ColumnPresets.currency("Net worth", SaveSummaryDto::netWorth),
        ColumnPresets.text("Last played", s -> s.lastPlayed().toString())
    ));
    table.setItems(controller.getSaves());
    table.setPrefHeight(220);
    table.setOnMousePressed(_ -> {
      SaveSummaryDto selected = table.getSelectionModel().getSelectedItem();
      if (selected != null) {
        controller.select(selected);
      }
    });
    return table;
  }

  private HBox createDetailPanel() {
    SimpleObjectProperty<SaveSummaryDto> selected = controller.getSelected();

    Label nameVal = new Label("-");
    Label statusVal = new Label("-");
    Label weekVal = new Label("-");
    Label netWorthVal = new Label("-");
    Label lastPlayedVal = new Label("-");

    selected.addListener((_, _, s) -> {
      if (s == null) {
        nameVal.setText("-");
        statusVal.setText("-");
        weekVal.setText("-");
        netWorthVal.setText("-");
        lastPlayedVal.setText("-");
      } else {
        nameVal.setText(s.playerName());
        statusVal.setText(s.status());
        weekVal.setText("Week " + s.week());
        netWorthVal.setText(s.netWorth().setScale(0, RoundingMode.HALF_UP).toString() + " kr");
        lastPlayedVal.setText(s.lastPlayed().toString());
      }
    });

    HBox panel = new HBox(12,
        buildStatCard("PLAYER", nameVal),
        buildStatCard("STATUS", statusVal),
        buildStatCard("WEEK", weekVal),
        buildStatCard("NET WORTH", netWorthVal),
        buildStatCard("LAST PLAYED", lastPlayedVal)
    );
    panel.setAlignment(Pos.CENTER_LEFT);
    return panel;
  }

  private VBox buildStatCard(String label, Label valueLabel) {
    Label overline = new Label(label);
    overline.getStyleClass().add("stat-label");
    valueLabel.getStyleClass().add("stat-value");

    VBox card = new VBox(4, overline, valueLabel);
    card.getStyleClass().add("stat-card");
    HBox.setHgrow(card, Priority.ALWAYS);
    return card;
  }

  private HBox createButtons() {
    Button load = new Button("Load game");
    load.getStyleClass().add("btn-accent");
    load.setMaxWidth(Double.MAX_VALUE);
    load.setOnAction(_ -> controller.load());
    load.disableProperty().bind(controller.getSelected().isNull());

    Button delete = new Button("Delete save");
    delete.getStyleClass().add("system-button-danger");
    delete.setMaxWidth(Double.MAX_VALUE);
    delete.setOnAction(_ -> controller.delete());
    delete.disableProperty().bind(controller.getSelected().isNull());

    HBox.setHgrow(load, Priority.ALWAYS);
    HBox.setHgrow(delete, Priority.ALWAYS);

    HBox buttons = new HBox(12, load, delete);
    buttons.setAlignment(Pos.CENTER_LEFT);
    return buttons;
  }
}
