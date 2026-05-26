package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetGoalProgressUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetStatusProgressUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.GoalProgressDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StatusProgressDto;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Controller for the dashboard view.
 *
 * <p>Exposes observable data used by the dashboard cards and refreshes itself
 * whenever the underlying exchange notifies its observers.</p>
 */
public class DashboardQueryController implements Observer {

  private final UUID gameId;
  private final GetGoalProgressUseCase getGoalProgress;
  private final ObjectProperty<GoalProgressDto> goalProgress = new SimpleObjectProperty<>();
  private final GetStatusProgressUseCase getStatusProgress;
  private final ObjectProperty<StatusProgressDto> statusProgress = new SimpleObjectProperty<>();

  /**
   * Creates a new dashboard query controller and registers it as an observer
   * of the given exchange.
   *
   * @param gameId the id of the game session this controller belongs to
   * @param getGoalProgress the use case used to fetch goal progress
   * @param exchange the exchange to observe for updates
   */
  public DashboardQueryController(
      UUID gameId,
      GetGoalProgressUseCase getGoalProgress,
      Exchange exchange, GetStatusProgressUseCase getStatusProgress
  ) {
    this.gameId = gameId;
    this.getGoalProgress = getGoalProgress;
    this.getStatusProgress = getStatusProgress;
    exchange.addObserver(this);
    refresh();
  }

  /**
   * Returns the observable goal progress property.
   *
   * <p>UI components can bind to this property to react to changes in the
   * player's progress towards the win threshold.</p>
   *
   * @return the read-only goal progress property
   */
  public ReadOnlyObjectProperty<GoalProgressDto> goalProgressProperty() {
    return goalProgress;
  }

  public ReadOnlyObjectProperty<StatusProgressDto> statusProgressProperty() {
    return statusProgress;
  }

  /**
   * Called by the exchange whenever its state changes.
   */
  @Override
  public void update() {
    refresh();
  }

  /**
   * Re-fetches all dashboard data from the underlying use cases.
   */
  public void refresh() {
    goalProgress.set(getGoalProgress.execute(gameId));
    statusProgress.set(getStatusProgress.execute(gameId));
  }
}