package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.AdvanceWeekUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.BuyShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.GetPortfolioUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.SellShareUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionsUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.CSVFileHandler;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.InMemoryGameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.ui.controller.*;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.NewGameView;

import java.math.BigDecimal;
import java.util.UUID;
import javafx.application.Application;
import javafx.stage.Stage;

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
  private final GetTransactionsUseCase getTransactions = new GetTransactionsUseCase(sessions);

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    this.stage = stage;

    boolean bypassMenu = Boolean.parseBoolean(System.getProperty("skipMenu", "true"));

    if (bypassMenu) {
      var stocks = CSVFileHandler.readLines(
          java.nio.file.Path.of(getClass().getResource("/data/sp500.csv").toURI())
      );
      var player = new Player("Dev", new BigDecimal(10000));
      var exchange = new Exchange("Stock exchange", stocks, new TransactionFactory());

      UUID gameId = startGameSession.execute(
          new StartGameSessionUseCase.Request(player, exchange)
      ).gameId();

      switchToGame(gameId);
    } else {
      NewGameController controller = new NewGameController(startGameSession, this::switchToGame);
      NewGameView newGame = new NewGameView(stage, controller);
      stage.setScene(newGame.getScene());
      stage.show();

    }

  }

  public void switchToGame(UUID gameId) {
    loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSession session = sessions.findById(gameId).orElseThrow(GameSessionNotFoundException::new);
    GameController gameController = new GameController(session.getGameId(), buyShare, sellShare, advanceWeek);
    PortfolioQueryController portfolioQueryController = new PortfolioQueryController(gameId, getPortfolio, session.getExchange());
    TransactionQueryController transactionQueryController =
        new TransactionQueryController(gameId, getTransactions, session.getExchange());
    GameViewCoordinator gameViewCoordinator = new GameViewCoordinator(
        gameController,
        portfolioQueryController,
        transactionQueryController,
        buyShare,
        gameId,
        session.getExchange(),
        session.getPlayer()
    );


    stage.setScene(gameViewCoordinator.getScene());
    stage.show();
  }
}
