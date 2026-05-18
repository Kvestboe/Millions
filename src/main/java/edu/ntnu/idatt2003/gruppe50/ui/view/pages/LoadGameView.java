package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import edu.ntnu.idatt2003.gruppe50.ui.controller.LoadGameController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoadGameView extends VBox {

  private final LoadGameController controller;
  private final Runnable onBack;

  public LoadGameView(LoadGameController controller, Runnable onBack) {
    this.controller = controller;
    this.onBack = onBack;
    build();
  }

  private void build() {
    Button backBtn = new Button("Back");
    backBtn.setOnAction(_ -> onBack.run());

    Label title = new Label("Load game");
    title.getStyleClass().add("page-title");

    getChildren().addAll(backBtn, title, createSaveTable(), createDetailPanel(), createButtons());
  }

  private TableView<SaveSummaryDto> createSaveTable() {
    TableView<SaveSummaryDto> saveTable = createTable(List.of(
        ColumnPresets.text("Name", SaveSummaryDto::playerName),
        ColumnPresets.text("Date", s -> s.lastPlayed().toString()),
        ColumnPresets.currency("Net worth", SaveSummaryDto::netWorth)
    ));
    saveTable.setItems(controller.getSaves());
    saveTable.setOnMousePressed(_ -> {
      SaveSummaryDto selected = saveTable.getSelectionModel().getSelectedItem();
      if (selected != null) {
        controller.select(selected);
      }
    });
    return saveTable;
  }
  // TODO: need to add styling to view
  private HBox createDetailPanel() {
    SimpleObjectProperty<SaveSummaryDto> selected = controller.getSelected();

    Label nameVal = new Label("-");
    Label statusVal = new Label("-");
    Label weekVal = new Label("-");
    Label netWorthVal = new Label("-");
    Label lastPlayedVal = new Label("-");
    Label finishedVal = new Label("-");

    // Build the layout once — only update label text inside the listener
    HBox panel = new HBox(
        new VBox(new Label("PLAYER"), nameVal),
        new VBox(new Label("STATUS"), statusVal),
        new VBox(new Label("WEEK"), weekVal),
        new VBox(new Label("NET WORTH"), netWorthVal),
        new VBox(new Label("LAST PLAYED"), lastPlayedVal),
        new VBox(new Label("FINISHED"), finishedVal)
    );

    selected.addListener((_, _, s) -> {
      if (s == null) {
        nameVal.setText("-");
        statusVal.setText("-");
        weekVal.setText("-");
        netWorthVal.setText("-");
        lastPlayedVal.setText("-");
        finishedVal.setText("-");
      } else {
        nameVal.setText(s.playerName());
        statusVal.setText(s.status());
        weekVal.setText(String.valueOf(s.week()));
        netWorthVal.setText(s.netWorth().toString());
        lastPlayedVal.setText(s.lastPlayed().toString());
        finishedVal.setText(String.valueOf(s.isFinished()));
      }
    });

    return panel;
  }

  private HBox createButtons() {
    Button load = new Button("Load game");
    load.setOnAction(_ -> controller.load());
    load.disableProperty().bind(controller.getSelected().isNull());

    Button delete = new Button("Delete game");
    delete.setOnAction(_ -> controller.delete());
    delete.disableProperty().bind(controller.getSelected().isNull());

    return new HBox(load, delete);
  }
}
