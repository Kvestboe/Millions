package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.DeleteSaveUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetAllSavesUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.SaveSummaryDto;
import java.util.UUID;
import java.util.function.Consumer;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controls loading, selecting and deleting saved game sessions.
 */
public class LoadGameController {

  private final GetAllSavesUseCase getAllSaves;
  private final LoadGameSessionUseCase loadGameSession;
  private final DeleteSaveUseCase deleteSave;
  private final Consumer<UUID> onGameLoaded;

  private final ObservableList<SaveSummaryDto> saves = FXCollections.observableArrayList();
  private final SimpleObjectProperty<SaveSummaryDto> selected = new SimpleObjectProperty<>();

  /**
   * Creates a controller for the load-game screen.
   *
   * @param getAllSaves     use case used to retrieve saved games
   * @param loadGameSession use case used to load a selected game
   * @param deleteSave      use case used to delete selected saves
   * @param onGameLoaded    callback invoked with the loaded game id
   */
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

  /**
   * Returns the observable list of available saves.
   *
   * @return available save summaries
   */
  public ObservableList<SaveSummaryDto> getSaves() {
    return saves;
  }

  /**
   * Returns the currently selected save property.
   *
   * @return selected save property
   */
  public SimpleObjectProperty<SaveSummaryDto> getSelected() {
    return selected;
  }

  /**
   * Selects a save in the load-game screen.
   *
   * @param save save summary to select
   */
  public void select(SaveSummaryDto save) {
    selected.set(save);
  }

  /**
   * Loads the currently selected save if it is not finished.
   */
  public void load() {
    SaveSummaryDto save = selected.get();

    if (save == null || save.isFinished()) {
      return;
    }

    onGameLoaded.accept(save.gameId());
  }

  /**
   * Deletes the currently selected save and refreshes the list.
   */
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
