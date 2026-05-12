package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.*;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.MarketController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.NewGameView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.UUID;

/**
 * Hello world!
 *
 */
public class App extends Application {

  private Stage stage;
  private final GameSessionRepository sessions = new InMemoryGameSessionRepository();
  private final StartGameSessionUseCase startGameSession = new StartGameSessionUseCase(sessions);
  private final LoadGameSessionUseCase loadGameSession = new LoadGameSessionUseCase(sessions);
  private final BuyShareUseCase buyShare = new BuyShareUseCase(sessions);
  private final SellShareUseCase sellShare = new SellShareUseCase(sessions);
  private final AdvanceWeekUseCase advanceWeek = new AdvanceWeekUseCase(sessions);
  private final GetPortfolioUseCase getPortfolio = new GetPortfolioUseCase(sessions);

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;
    NewGameController controller = new NewGameController(startGameSession, this::switchToGame);
    NewGameView newGame = new NewGameView(stage, controller);
    stage.setScene(newGame.getScene());
    stage.show();
  }

  public void switchToGame(UUID gameId) {
    loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSession session = sessions.findById(gameId).orElseThrow(GameSessionNotFoundException::new);
    GameController gameController = new GameController(session.getGameId(), buyShare, sellShare, advanceWeek);
    PortfolioQueryController portfolioQueryController = new PortfolioQueryController(gameId, getPortfolio);
    GameViewCoordinator gameViewCoordinator = new GameViewCoordinator(
        gameController,
        portfolioQueryController,
        buyShare,
        gameId,
        session.getExchange(),
        session.getPlayer()
    );
    stage.setScene(gameViewCoordinator.getScene());
  }
}
