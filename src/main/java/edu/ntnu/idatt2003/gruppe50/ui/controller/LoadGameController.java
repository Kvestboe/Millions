package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.DeleteSaveUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetAllSavesUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import java.util.UUID;
import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LoadGameController {

  private final GetAllSavesUseCase getAllSaves;
  private final LoadGameSessionUseCase loadGameSession;
  private final DeleteSaveUseCase deleteSave;
  private final Consumer<UUID> onGameLoaded;

  private final ObservableList<SaveSummaryDto> saves = FXCollections.observableArrayList();
  private final SimpleObjectProperty<SaveSummaryDto> selected = new SimpleObjectProperty<>();

  public LoadGameController(
      GetAllSavesUseCase getAllSaves,
      LoadGameSessionUseCase loadGameSession,
      DeleteSaveUseCase deleteSave,
      Consumer<UUID> onGameLoaded
  ) {
    this.getAllSaves = getAllSaves;
    this.loadGameSession = loadGameSession;
    this.deleteSave = deleteSave;
    this.onGameLoaded = onGameLoaded;
    refresh();
  }

  public ObservableList<SaveSummaryDto> getSaves() {
    return saves;
  }

  public SimpleObjectProperty<SaveSummaryDto> getSelected() {
    return selected;
  }

  public void select(SaveSummaryDto save) {
    selected.set(save);
  }

  public void load() {
    if (selected.get() != null) {
      UUID gameId = selected.get().gameId();
      loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
      onGameLoaded.accept(gameId);
    }
  }

  public void delete() {
    if (selected.get() != null) {
      deleteSave.execute(selected.get().gameId());
      selected.set(null);
      refresh();
    }
  }

  private void refresh() {
    saves.setAll(getAllSaves.execute());
  }
}
